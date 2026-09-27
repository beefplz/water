package org.capstone.water.service;

import org.capstone.water.repository.entity.waterdata.Waterdata;
import org.capstone.water.repository.entity.waterdata.WaterdataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// getWaterdataWithOs는 Caffeine 캐시(30분)를 사용: 같은 수조는 캐시가 만료될 때까지 DB를 다시 조회하지 않음
@SpringBootTest
@ActiveProfiles("test")
class WaterServiceCacheTest {

    @Autowired
    WaterServiceImpl service;

    @MockBean
    WaterdataRepository waterdataRepository;

    @Test
    void 같은_수조는_캐시에서_반환하고_다른_수조는_따로_조회한다() {
        given(waterdataRepository.findWaterdataByTankid("iw1")).willReturn(List.of(Waterdata.builder().wt(20f).wdo(9.1f).build()));
        given(waterdataRepository.findWaterdataByTankid("rt1")).willReturn(List.of(Waterdata.builder().wt(20f).wdo(9.1f).build()));

        service.getWaterdataWithOs("iw1");
        service.getWaterdataWithOs("iw1");
        service.getWaterdataWithOs("rt1");

        verify(waterdataRepository, times(1)).findWaterdataByTankid("iw1");
        verify(waterdataRepository, times(1)).findWaterdataByTankid("rt1");
    }
}
