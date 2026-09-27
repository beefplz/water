package org.capstone.water.apireader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.capstone.water.repository.entity.mldata.MldataMapping;
import org.capstone.water.repository.entity.mldata.MldataViewRepository;
import org.capstone.water.repository.entity.pdo.PredictDo;
import org.capstone.water.repository.entity.pdoweek.PredictDoWeek;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * 리팩터링 전후로 Triton에 보내는 요청과 저장되는 결과가 같은지 고정하는 테스트.
 * 로컬에 가짜 Triton 서버를 띄워 실제로 전송된 요청 본문을 검사한다.
 */
class PdoReaderCharacterizationTest {
    private static final List<String> TANKS = List.of("iw1", "rt1", "rt2");

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> requestPaths = new ArrayList<>();
    private final List<JsonNode> requestBodies = new ArrayList<>();
    private HttpServer triton;
    private String tritonUrl;

    @BeforeEach
    void startTriton() throws IOException {
        triton = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        triton.createContext("/", exchange -> {
            requestPaths.add(exchange.getRequestURI().getPath());
            requestBodies.add(objectMapper.readTree(exchange.getRequestBody()));
            byte[] body = "{\"model_name\":\"m\",\"outputs\":[{\"name\":\"output0\",\"datatype\":\"FP32\",\"shape\":[1,1],\"data\":[8.456]}]}"
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        triton.start();
        tritonUrl = "http://127.0.0.1:" + triton.getAddress().getPort();
    }

    @AfterEach
    void stopTriton() {
        triton.stop(0);
    }

    @Test
    void 단기예측_요청과_결과() {
        MldataViewRepository repo = mock(MldataViewRepository.class);
        // 풍향 330~359도: 주간 테스트(0~335도)와 합쳐 0~359도 전체 정수 각도를 검증
        IntUnaryOperator wdir = i -> 330 + i;
        for (int t = 0; t < TANKS.size(); t++) {
            List<MldataMapping> rows = rows(30, t, wdir);
            given(repo.findMldataViewsByTankidOrderByTimeDesc(TANKS.get(t))).willReturn(rows);
        }

        List<PredictDo> result = PdoReaderFactory.pdoReader(repo, tritonUrl).pdoRead("2026-09-27 12:00");

        assertRequests("/v2/models/predictdo/infer", 30, wdir);
        assertThat(result).extracting(PredictDo::getTankid).containsExactly("IW1", "RT1", "RT2");
        assertThat(result).allSatisfy(p -> {
            assertThat(p.getTime()).isEqualTo(LocalDateTime.of(2026, 9, 27, 12, 30));
            assertThat(p.getPdo()).isEqualTo(8.46f);
            assertThat(p.getNum()).isNull();
        });
    }

    @Test
    void 주간예측_요청과_결과() {
        MldataViewRepository repo = mock(MldataViewRepository.class);
        IntUnaryOperator wdir = i -> i;   // 0~335도
        for (int t = 0; t < TANKS.size(); t++) {
            List<MldataMapping> rows = rows(336, t, wdir);
            given(repo.findMldataViewsByTankidOrderByTimeDescWeek(TANKS.get(t))).willReturn(rows);
        }

        List<PredictDoWeek> result = PdoReaderFactory.pdoWeekReader(repo, tritonUrl).pdoweekRead("2026-09-27 12:00");

        assertRequests("/v2/models/predictdoweek/infer", 336, wdir);
        assertThat(result).extracting(PredictDoWeek::getTankid).containsExactly("IW1", "RT1", "RT2");
        assertThat(result).allSatisfy(p -> {
            assertThat(p.getTime()).isEqualTo(LocalDateTime.of(2026, 10, 4, 12, 0));
            assertThat(p.getPdo()).isEqualTo(8.46f);
        });
    }

    private void assertRequests(String path, int size, IntUnaryOperator wdir) {
        assertThat(requestPaths).containsOnly(path).hasSize(3);
        for (int t = 0; t < 3; t++) {
            JsonNode body = requestBodies.get(t);
            JsonNode input = body.get("inputs").get(0);
            assertThat(input.get("name").asText()).isEqualTo("input0");
            assertThat(input.get("datatype").asText()).isEqualTo("FP32");
            assertThat(input.get("shape").toString()).isEqualTo("[1," + size + ",10]");
            assertThat(body.get("outputs").get(0).get("name").asText()).isEqualTo("output0");

            JsonNode data = input.get("data");
            assertThat(data.size()).isEqualTo(size);
            for (int i = 0; i < size; i++) {
                float[] expected = expectedRow(i, t, wdir.applyAsInt(i));
                JsonNode row = data.get(i);
                assertThat(row.size()).isEqualTo(10);
                for (int j = 0; j < 10; j++) {
                    assertThat((float) row.get(j).doubleValue())
                            .as("tank %d, row %d, col %d", t, i, j)
                            .isEqualTo(expected[j]);
                }
            }
        }
    }

    // 모델 입력 순서: 유속, 풍향코드, 유향, 수온, 0, DO, 양식장수온, pH, 염도, 0
    private static float[] expectedRow(int i, int tank, int wdir) {
        float b = tank * 1000 + i;
        return new float[]{b + 0.1f, expectedWindCode(wdir), b + 0.2f, b + 0.3f, 0f, b + 0.4f, b + 0.5f, b + 0.6f, b + 0.7f, 0f};
    }

    // 기존 코드의 16방위 구간표를 그대로 옮긴 기댓값
    private static float expectedWindCode(int deg) {
        int[][] table = {
                {350, 359, 8}, {0, 11, 8}, {12, 34, 16}, {35, 56, 9}, {57, 79, 10}, {80, 101, 11},
                {102, 124, 12}, {125, 146, 1}, {147, 169, 2}, {170, 191, 3}, {192, 214, 15},
                {215, 236, 13}, {237, 260, 14}, {261, 281, 4}, {282, 304, 6}, {305, 326, 5}, {327, 349, 7}};
        for (int[] r : table) {
            if (r[0] <= deg && deg <= r[1]) return r[2];
        }
        throw new IllegalArgumentException("각도 범위 밖: " + deg);
    }

    private static List<MldataMapping> rows(int size, int tank, IntUnaryOperator wdir) {
        List<MldataMapping> rows = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            float b = tank * 1000 + i;
            MldataMapping row = mock(MldataMapping.class);
            given(row.getScs()).willReturn(b + 0.1f);
            given(row.getScd()).willReturn(b + 0.2f);
            given(row.getWdir()).willReturn((float) wdir.applyAsInt(i));
            given(row.getSwt()).willReturn(b + 0.3f);
            given(row.getWdo()).willReturn(b + 0.4f);
            given(row.getWt()).willReturn(b + 0.5f);
            given(row.getPh()).willReturn(b + 0.6f);
            given(row.getSa()).willReturn(b + 0.7f);
            rows.add(row);
        }
        return rows;
    }
}
