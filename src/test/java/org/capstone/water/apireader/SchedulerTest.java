package org.capstone.water.apireader;

import org.capstone.water.repository.entity.pdo.PredictDo;
import org.capstone.water.repository.entity.pdo.PredictDoRepository;
import org.capstone.water.repository.entity.pdoweek.PredictDoWeek;
import org.capstone.water.repository.entity.pdoweek.PredictDoWeekRepository;
import org.capstone.water.repository.entity.waterdata.Waterdata;
import org.capstone.water.repository.entity.waterdata.WaterdataRepository;
import org.capstone.water.repository.entity.weather.WeatherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {
    @Mock WaterdataRepository waterdataRepository;
    @Mock WeatherRepository weatherRepository;
    @Mock PredictDoRepository predictDoRepository;
    @Mock PredictDoWeekRepository predictDoWeekRepository;
    @Mock WeatherReader weatherReader;
    @Mock WaterReader waterReader;
    @Mock PdoReader pdoReader;
    @Mock PdoWeekReader pdoWeekReader;
    @InjectMocks Scheduler scheduler;

    private final LocalDateTime now = LocalDateTime.of(2026, 9, 27, 12, 0);

    @Test
    void 기상_수집이_실패해도_나머지_단계는_저장된다() {
        given(weatherReader.weatherRead(anyString())).willThrow(new RuntimeException("khoa API 장애"));

        List<Waterdata> water = List.of(Waterdata.builder().time(now).tankid("iw1").build());
        given(waterReader.waterRead(anyString())).willReturn(water);

        List<PredictDo> pdo = List.of(PredictDo.builder().time(now.plusMinutes(30)).tankid("IW1").build());
        given(pdoReader.pdoRead(anyString())).willReturn(pdo);

        List<PredictDoWeek> pdoWeek = List.of(PredictDoWeek.builder().time(now.plusDays(7)).tankid("IW1").build());
        given(pdoWeekReader.pdoweekRead(anyString())).willReturn(pdoWeek);

        scheduler.run();

        verify(weatherRepository, never()).save(any());
        verify(waterdataRepository).saveAll(water);
        verify(predictDoRepository).saveAll(pdo);
        verify(predictDoWeekRepository).saveAll(pdoWeek);
    }

    @Test
    void 주간_예측은_자기_목표시각으로_중복을_검사한다() {
        given(weatherReader.weatherRead(anyString())).willThrow(new RuntimeException());
        given(waterReader.waterRead(anyString())).willThrow(new RuntimeException());
        given(pdoReader.pdoRead(anyString())).willThrow(new RuntimeException());

        LocalDateTime weekTarget = now.plusDays(7);
        List<PredictDoWeek> pdoWeek = List.of(PredictDoWeek.builder().time(weekTarget).tankid("IW1").build());
        given(pdoWeekReader.pdoweekRead(anyString())).willReturn(pdoWeek);
        given(predictDoWeekRepository.existsByTime(weekTarget)).willReturn(true);

        scheduler.run();

        verify(predictDoWeekRepository, never()).saveAll(any());
    }
}
