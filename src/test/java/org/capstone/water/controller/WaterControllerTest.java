package org.capstone.water.controller;

import org.capstone.water.repository.entity.pdo.PdoMapping;
import org.capstone.water.repository.entity.pdoweek.PdoWeekMapping;
import org.capstone.water.repository.entity.waterdata.Waterdata;
import org.capstone.water.service.WaterServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 프론트엔드가 사용하는 API의 주소와 응답 JSON 형식을 고정
@WebMvcTest(WaterController.class)
@ActiveProfiles("test")
class WaterControllerTest {
    private static final LocalDateTime TIME = LocalDateTime.of(2026, 9, 27, 11, 58);

    @Autowired
    MockMvc mockMvc;

    @MockBean
    WaterServiceImpl service;

    @Test
    void 최신_수조데이터와_산소포화도() throws Exception {
        Waterdata data = Waterdata.builder().num(1L).time(TIME).tankid("iw1").wdo(8.0f).wt(20f).ph(7.9f).sa(31f).build();
        data.setOs(87.91f);
        given(service.getWaterdataOnewithOs("iw1")).willReturn(data);

        mockMvc.perform(get("/api/wateronewithos").param("tankid", "iw1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"num":1,"time":"202609271158","tankid":"iw1","wdo":8.0,"wt":20.0,"ph":7.9,"sa":31.0,"os":87.91}
                        """, true));
    }

    @Test
    void 단기_예측값_목록() throws Exception {
        given(service.getPdo("IW1")).willReturn(List.of(pdo(TIME, "IW1", 8.46f)));

        mockMvc.perform(get("/api/pdo").param("tankid", "IW1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"time":"202609271158","tankid":"IW1","pdo":8.46}]
                        """, true));
    }

    @Test
    void 주간_예측값_목록은_pdoweek_키로_반환한다() throws Exception {
        given(service.getPdoWeek("IW1")).willReturn(List.of(pdoWeek(TIME, "IW1", 7.5f)));

        mockMvc.perform(get("/api/pdoweek").param("tankid", "IW1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"time":"202609271158","tankid":"IW1","pdoweek":7.5}]
                        """, true));
    }

    @Test
    void tankid가_없으면_400() throws Exception {
        mockMvc.perform(get("/api/water")).andExpect(status().isBadRequest());
    }

    private static PdoMapping pdo(LocalDateTime time, String tankid, float value) {
        return new PdoMapping() {
            public LocalDateTime getTime() { return time; }
            public String getTankid() { return tankid; }
            public Float getPdo() { return value; }
        };
    }

    private static PdoWeekMapping pdoWeek(LocalDateTime time, String tankid, float value) {
        return new PdoWeekMapping() {
            public LocalDateTime getTime() { return time; }
            public String getTankid() { return tankid; }
            public Float getPdoweek() { return value; }
        };
    }
}
