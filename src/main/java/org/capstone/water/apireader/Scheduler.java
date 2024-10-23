package org.capstone.water.apireader;

import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.mldata.MldataViewRepository;
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
    private final WaterdataRepository waterdataRepository;
    private final WeatherRepository weatherRepository;
    private final PredictDoRepository predictDoRepository;
    private final MldataViewRepository mldataViewRepository;
    private final PredictDoWeekRepository predictDoWeekRepository;;
    final Logger log = LoggerFactory.getLogger(getClass());
    @Scheduled(fixedRate = 60000)
    public void run() {

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusMinutes(1);
        String localDateTimeString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        log.info(localDateTimeString);

        WeatherReader weatherReader = new WeatherReader();
        Weather weather = weatherReader.weatherRead(localDateTimeString, weatherRepository);
        if (weatherRepository.existsByTime(weather.getTime())){
            log.info("weather already exist");
        }
        else {
            weatherRepository.save(weather);
        }

        WaterReader waterReader = new WaterReader();
        List<Waterdata> waterdataList = waterReader.waterRead(localDateTimeString, waterdataRepository);
        if (waterdataRepository.existsByTime(waterdataList.get(0).getTime())){
            log.info("water already exist");
        }
        else {
            waterdataRepository.save(waterdataList.get(0));
            waterdataRepository.save(waterdataList.get(1));
            waterdataRepository.save(waterdataList.get(2));
        }

        PdoReader pdoReader = new PdoReader();
        List<PredictDo> predictDoList = pdoReader.pdoRead(localDateTimeString, mldataViewRepository);
        if(predictDoRepository.existsByTime(predictDoList.get(0).getTime())){
            log.info("predict do already exist");
        }
        else {
            predictDoRepository.save(predictDoList.get(0));
            predictDoRepository.save(predictDoList.get(1));
            predictDoRepository.save(predictDoList.get(2));
        }

        PdoWeekReader pdoWeekReader = new PdoWeekReader();
        List<PredictDoWeek> predictDoWeekList = pdoWeekReader.pdoweekRead(localDateTimeString, mldataViewRepository);
        if(predictDoWeekRepository.existsByTime(predictDoList.get(0).getTime())){
            log.info("predict do already exist");
        }
        else {
            predictDoWeekRepository.save(predictDoWeekList.get(0));
            predictDoWeekRepository.save(predictDoWeekList.get(1));
            predictDoWeekRepository.save(predictDoWeekList.get(2));
        }

    }
}
