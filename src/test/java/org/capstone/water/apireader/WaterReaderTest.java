package org.capstone.water.apireader;

import org.capstone.water.config.ApiProperties;
import org.capstone.water.repository.entity.waterdata.Waterdata;
import org.capstone.water.repository.entity.waterdata.WaterdataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WaterReaderTest {
    private static final String BASE = "http://aqua.kware.co.kr/openapi/v1";
    private static final String NOW = "2026-09-27 11:59";

    private MockRestServiceServer server;
    private WaterdataRepository repository;
    private WaterReader reader;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        repository = mock(WaterdataRepository.class);
        reader = new WaterReader(builder.build(), repository, new ApiProperties("k", "abc123", "http://t"));
    }

    @Test
    void 토큰을_받아_센서값을_조회하고_수조별로_변환한다() {
        expectToken();
        server.expect(requestTo(BASE + "/fac/sensorstatus?acessToken=tok"))
                .andExpect(method(POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {"pageSize":200,"pageNumber":1,"fcltyIds":["61AF1"],
                         "startDate":"2026-09-27 11:59","endDate":"2026-09-27 11:59"}""", true))
                .andRespond(withSuccess("""
                        {"content":{"list":[
                          %s, %s, %s]}}""".formatted(
                        sensor("iw1", "20.5", "8.1", "7.9", "31.0"),
                        sensor("rt1", "21.0", "7.5", null, null),
                        sensor("rt2", "19.5", "6.9", null, null)), MediaType.APPLICATION_JSON));

        List<Waterdata> result = reader.waterRead(NOW);

        server.verify();
        assertThat(result).extracting(Waterdata::getTankid).containsExactly("iw1", "rt1", "rt2");
        assertThat(result).extracting(Waterdata::getWt).containsExactly(20.5f, 21.0f, 19.5f);
        assertThat(result).extracting(Waterdata::getWdo).containsExactly(8.1f, 7.5f, 6.9f);
        // pH, 염도는 iw1 값을 세 수조에 같이 사용
        assertThat(result).extracting(Waterdata::getPh).containsOnly(7.9f);
        assertThat(result).extracting(Waterdata::getSa).containsOnly(31.0f);
        assertThat(result).extracting(Waterdata::getTime).containsOnly(LocalDateTime.of(2026, 9, 27, 11, 58));
        verifyNoInteractions(repository);
    }

    @Test
    void 센서값이_비어있으면_해당_수조의_최근값으로_채운다() {
        given(repository.findFirstByTankidOrderByTimeDesc("rt1")).willReturn(Waterdata.builder().wdo(7.7f).build());
        expectToken();
        server.expect(requestTo(BASE + "/fac/sensorstatus?acessToken=tok"))
                .andRespond(withSuccess("""
                        {"content":{"list":[%s, %s, %s]}}""".formatted(
                        sensor("iw1", "20.5", "8.1", "7.9", "31.0"),
                        sensor("rt1", "21.0", null, null, null),
                        sensor("rt2", "19.5", "6.9", null, null)), MediaType.APPLICATION_JSON));

        List<Waterdata> result = reader.waterRead(NOW);

        assertThat(result.get(1).getWdo()).isEqualTo(7.7f);
    }

    @Test
    void 센서_데이터가_없으면_1개월_전_데이터를_현재_시각으로_복사한다() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 27, 11, 59);
        for (String id : Tanks.IDS) {
            Waterdata past = Waterdata.builder().num(100L).tankid(id).time(now.minusMonths(1)).wt(18f).build();
            given(repository.findFistByTankidAndTime(id, now.minusMonths(1))).willReturn(past);
        }
        expectToken();
        server.expect(requestTo(BASE + "/fac/sensorstatus?acessToken=tok"))
                .andRespond(withSuccess("{\"content\":{\"list\":[]}}", MediaType.APPLICATION_JSON));

        List<Waterdata> result = reader.waterRead(NOW);

        assertThat(result).extracting(Waterdata::getTankid).containsExactly("iw1", "rt1", "rt2");
        assertThat(result).extracting(Waterdata::getTime).containsOnly(now);
        assertThat(result).extracting(Waterdata::getNum).containsOnlyNulls();
    }

    @Test
    void 토큰_발급에_실패하면_예외() {
        server.expect(requestTo(BASE + "/acesstoken?key=abc123"))
                .andRespond(withSuccess("{\"message\":\"invalid key\"}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> reader.waterRead(NOW))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("토큰");
    }

    private void expectToken() {
        server.expect(requestTo(BASE + "/acesstoken?key=abc123")).andExpect(method(GET))
                .andRespond(withSuccess("{\"acessToken\":\"tok\"}", MediaType.APPLICATION_JSON));
    }

    private static String sensor(String tank, String wt, String do1, String ph, String sa) {
        return """
                {"tankId":"61AF1_%s","collectDe":"2026-09-27","collectTime":"11:58:00",
                 "wt":%s,"do1":%s,"ph":%s,"sa":%s}""".formatted(tank, quote(wt), quote(do1), quote(ph), quote(sa));
    }

    private static String quote(String v) {
        return v == null ? "null" : "\"" + v + "\"";
    }
}
