package org.capstone.water;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 인메모리 DB(H2)로 애플리케이션 전체를 띄워 설정과 Bean 구성이 올바른지 확인
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class WaterApplicationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void 애플리케이션이_정상적으로_시작된다() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    void 리포지토리가_REST_API로_자동_공개되지_않는다() throws Exception {
        mockMvc.perform(get("/waterdatas")).andExpect(status().isNotFound());
        mockMvc.perform(post("/waterdatas").contentType("application/json").content("{}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/weathers/1")).andExpect(status().isNotFound());
    }

    @Test
    void 캐시_관리_엔드포인트는_공개되지_않는다() throws Exception {
        mockMvc.perform(get("/actuator/caches")).andExpect(status().isNotFound());
        mockMvc.perform(delete("/actuator/caches")).andExpect(status().isNotFound());
    }
}
