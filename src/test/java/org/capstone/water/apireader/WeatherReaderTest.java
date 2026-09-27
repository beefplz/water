package org.capstone.water.apireader;

import org.capstone.water.config.ApiProperties;
import org.capstone.water.repository.entity.weather.Weather;
import org.capstone.water.repository.entity.weather.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WeatherReaderTest {
    // 실제 키처럼 '/'와 '='가 들어간 키가 인코딩되지 않고 그대로 전송되는지 확인
    private static final String KEY = "ab/cd==";
    private static final String URL = "https://www.khoa.go.kr/api/oceangrid/tideObsRecent/search.do"
            + "?ServiceKey=" + KEY + "&ObsCode=DT_0027&ResultType=json";

    private MockRestServiceServer server;
    private WeatherRepository repository;
    private WeatherReader reader;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        repository = mock(WeatherRepository.class);
        reader = new WeatherReader(builder.build(), repository, new ApiProperties(KEY, "k", "http://t"));
    }

    @Test
    void 관측값을_Weather로_변환한다() {
        respond("""
                {"result":{"meta":{"obs_post_id":"DT_0027"},"data":{
                  "record_time":"2026-09-27 11:59:00","tide_level":"123","water_temp":"21.5","Salinity":"31.2",
                  "air_temp":"20.1","air_press":"1012.3","wind_dir":"270","wind_speed":"3.4"}}}""");

        Weather weather = reader.weatherRead("2026-09-27 11:59");

        server.verify();
        assertThat(weather.getTime()).isEqualTo(LocalDateTime.of(2026, 9, 27, 11, 59));
        assertThat(weather.getSwh()).isEqualTo(123f);
        assertThat(weather.getSwt()).isEqualTo(21.5f);
        assertThat(weather.getSsa()).isEqualTo(31.2f);
        assertThat(weather.getSat()).isEqualTo(20.1f);
        assertThat(weather.getSap()).isEqualTo(1012.3f);
        assertThat(weather.getWdir()).isEqualTo((short) 270);
        assertThat(weather.getWs()).isEqualTo(3.4f);
        assertThat(weather.getScd()).isZero();
        assertThat(weather.getScs()).isZero();
        verifyNoInteractions(repository);
    }

    @Test
    void 비어있는_값은_가장_최근_저장값으로_채우고_DB는_한번만_조회한다() {
        given(repository.findFirstByOrderByTimeDesc()).willReturn(Weather.builder()
                .swt(19f).ssa(30f).wdir((short) 90).build());
        respond("""
                {"result":{"data":{"tide_level":"123","water_temp":null,"Salinity":"","air_temp":"20.1",
                  "air_press":"1012.3","wind_speed":"3.4"}}}""");

        Weather weather = reader.weatherRead("2026-09-27 11:59");

        assertThat(weather.getSwt()).isEqualTo(19f);            // null
        assertThat(weather.getSsa()).isEqualTo(30f);            // 빈 문자열
        assertThat(weather.getWdir()).isEqualTo((short) 90);    // 필드 없음
        assertThat(weather.getSwh()).isEqualTo(123f);
        verify(repository, times(1)).findFirstByOrderByTimeDesc();
    }

    @Test
    void 관측_데이터가_없으면_예외() {
        respond("""
                {"result":{"error":"invalid key"}}""");

        assertThatThrownBy(() -> reader.weatherRead("2026-09-27 11:59"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("관측 데이터 없음");
    }

    private void respond(String json) {
        server.expect(requestTo(URL)).andExpect(method(GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }
}
