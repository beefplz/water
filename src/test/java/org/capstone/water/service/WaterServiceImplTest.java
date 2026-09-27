package org.capstone.water.service;

import org.capstone.water.repository.entity.mldata.MldataViewRepository;
import org.capstone.water.repository.entity.pdo.PredictDoRepository;
import org.capstone.water.repository.entity.pdoweek.PredictDoWeekRepository;
import org.capstone.water.repository.entity.waterdata.Waterdata;
import org.capstone.water.repository.entity.waterdata.WaterdataRepository;
import org.capstone.water.repository.entity.weather.WeatherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WaterServiceImplTest {
    @Mock PredictDoRepository predictDoRepository;
    @Mock MldataViewRepository mldataViewRepository;
    @Mock WaterdataRepository waterdataRepository;
    @Mock WeatherRepository weatherRepository;
    @Mock PredictDoWeekRepository predictDoWeekRepository;
    @InjectMocks WaterServiceImpl service;

    @Test
    void 최신_수조데이터에_산소포화도를_채워서_반환한다() {
        given(waterdataRepository.findFirstByTankidOrderByTimeDesc("iw1"))
                .willReturn(Waterdata.builder().tankid("iw1").wt(20f).wdo(8.0f).build());

        Waterdata result = service.getWaterdataOnewithOs("iw1");

        assertThat(result.getOs()).isEqualTo(87.91f);
    }

    @Test
    void 수온이_계산_범위_밖이면_산소포화도만_비우고_데이터는_반환한다() {
        given(waterdataRepository.findFirstByTankidOrderByTimeDesc("iw1"))
                .willReturn(Waterdata.builder().tankid("iw1").wt(8f).wdo(10f).build());

        Waterdata result = service.getWaterdataOnewithOs("iw1");

        assertThat(result.getWdo()).isEqualTo(10f);
        assertThat(result.getOs()).isNull();
    }

    @Test
    void 수조_데이터가_없으면_null() {
        assertThat(service.getWaterdataOnewithOs("iw1")).isNull();
    }

    @Test
    void 목록의_모든_데이터에_산소포화도를_채운다() {
        given(waterdataRepository.findWaterdataByTankid("iw1")).willReturn(List.of(
                Waterdata.builder().wt(20f).wdo(9.1f).build(),
                Waterdata.builder().wt(8f).wdo(9.1f).build(),
                Waterdata.builder().wt(25f).wdo(8.3f).build()));

        List<Waterdata> result = service.getWaterdataWithOs("iw1");

        // 범위 밖 수온(8℃)이 섞여 있어도 목록 전체가 실패하지 않음
        assertThat(result).extracting(Waterdata::getOs).containsExactly(100.0f, null, 100.0f);
    }
}
