package org.capstone.water.apireader;

import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.pdo.PredictDo;
import org.capstone.water.repository.entity.pdo.PredictDoRepository;
import org.capstone.water.repository.entity.pdoweek.PredictDoWeek;
import org.capstone.water.repository.entity.pdoweek.PredictDoWeekRepository;
import org.capstone.water.repository.entity.waterdata.Waterdata;
import org.capstone.water.repository.entity.waterdata.WaterdataRepository;
import org.capstone.water.repository.entity.weather.Weather;
import org.capstone.water.repository.entity.weather.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Component
@RequiredArgsConstructor
public class Scheduler {
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final WaterdataRepository waterdataRepository;
    private final WeatherRepository weatherRepository;
    private final PredictDoRepository predictDoRepository;
    private final PredictDoWeekRepository predictDoWeekRepository;
    private final WeatherReader weatherReader;
    private final WaterReader waterReader;
    private final PdoReader pdoReader;
    private final PdoWeekReader pdoWeekReader;
    final Logger log = LoggerFactory.getLogger(getClass());

    // 매분 0초에 실행. 이전 실행이 끝나지 않았으면 해당 회차는 건너뜀
    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void run() {
        String time = LocalDateTime.now(SEOUL).minusMinutes(1).format(FORMATTER);
        log.info("수집 시작: {}", time);

        // 각 단계는 독립적으로 실행: 한 단계가 실패해도 다음 단계는 계속 진행
        runStep("weather", () -> collectWeather(time));
        runStep("water", () -> collectWater(time));
        runStep("pdo", () -> predictDo(time));
        runStep("pdoWeek", () -> predictDoWeek(time));
    }

    private void runStep(String name, Runnable step) {
        try {
            step.run();
        } catch (Exception e) {
            log.error("[{}] 단계 실패", name, e);
        }
    }

    private void collectWeather(String time) {
        Weather weather = weatherReader.weatherRead(time);
        if (weatherRepository.existsByTime(weather.getTime())) {
            log.info("weather already exist");
            return;
        }
        weatherRepository.save(weather);
        log.info("[weather] 저장 완료");
    }

    private void collectWater(String time) {
        List<Waterdata> waterdataList = waterReader.waterRead(time);
        if (waterdataRepository.existsByTime(waterdataList.get(0).getTime())) {
            log.info("water already exist");
            return;
        }
        waterdataRepository.saveAll(waterdataList);
        log.info("[water] {}건 저장", waterdataList.size());
    }

    private void predictDo(String time) {
        List<PredictDo> predictDoList = pdoReader.pdoRead(time);
        if (predictDoRepository.existsByTime(predictDoList.get(0).getTime())) {
            log.info("predict do already exist");
            return;
        }
        predictDoRepository.saveAll(predictDoList);
        log.info("[pdo] {}건 저장", predictDoList.size());
    }

    private void predictDoWeek(String time) {
        List<PredictDoWeek> predictDoWeekList = pdoWeekReader.pdoweekRead(time);
        if (predictDoWeekRepository.existsByTime(predictDoWeekList.get(0).getTime())) {
            log.info("predict do week already exist");
            return;
        }
        predictDoWeekRepository.saveAll(predictDoWeekList);
        log.info("[pdoWeek] {}건 저장", predictDoWeekList.size());
    }
}
