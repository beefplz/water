package org.capstone.water.apireader;

import com.fasterxml.jackson.databind.JsonNode;
import org.capstone.water.config.ApiProperties;
import org.capstone.water.repository.entity.waterdata.Waterdata;
import org.capstone.water.repository.entity.waterdata.WaterdataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.capstone.water.apireader.JsonFields.floatOr;

// kware 양식장 센서 API에서 수조별 수온, DO, pH, 염도 수집
@Component
public class WaterReader {
    private static final Logger log = LoggerFactory.getLogger(WaterReader.class);
    private static final String KWARE_URL = "http://aqua.kware.co.kr/openapi/v1";
    private static final String FACILITY_ID = "61AF1";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter SENSOR_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestClient restClient;
    private final WaterdataRepository waterdataRepository;
    private final ApiProperties api;

    public WaterReader(@Qualifier("apiRestClient") RestClient restClient,
                       WaterdataRepository waterdataRepository, ApiProperties api) {
        this.restClient = restClient;
        this.waterdataRepository = waterdataRepository;
        this.api = api;
    }

    public List<Waterdata> waterRead(String timeString) {
        JsonNode list = requestSensorStatus(requestAccessToken(), timeString).path("content").path("list");
        if (!list.isArray()) {
            throw new IllegalStateException("kware 응답 형식 오류: content.list 없음");
        }
        if (list.isEmpty()) {
            return pastData(LocalDateTime.parse(timeString, FORMATTER));
        }
        if (list.size() < Tanks.IDS.size()) {
            throw new IllegalStateException("kware 수조 데이터 부족: " + list.size() + "/" + Tanks.IDS.size());
        }

        // pH와 염도는 iw1 센서에만 있어 세 수조에 같은 값을 사용
        JsonNode iw1 = list.get(0);
        log.debug("iw1: {}", iw1);
        Lazy<Waterdata> latestIw1 = Lazy.of(() -> waterdataRepository.findFirstByTankidOrderByTimeDesc("iw1"));
        Float ph = floatOr(iw1, "ph", "iw1", () -> latestIw1.get().getPh());
        Float sa = floatOr(iw1, "sa", "iw1", () -> latestIw1.get().getSa());

        return List.of(toWaterdata(list.get(0), ph, sa), toWaterdata(list.get(1), ph, sa), toWaterdata(list.get(2), ph, sa));
    }

    private String requestAccessToken() {
        URI uri = URI.create(KWARE_URL + "/acesstoken?key=" + api.kwareKey());
        JsonNode response = restClient.get().uri(uri).retrieve().body(JsonNode.class);
        // 응답 필드명 철자가 API 원본 그대로 'acessToken'
        String token = response == null ? null : response.path("acessToken").asText(null);
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("kware 액세스 토큰 발급 실패");
        }
        return token;
    }

    private JsonNode requestSensorStatus(String token, String timeString) {
        URI uri = UriComponentsBuilder.fromUriString(KWARE_URL + "/fac/sensorstatus")
                .queryParam("acessToken", token)
                .build()
                .encode()
                .toUri();
        SensorStatusRequest request = new SensorStatusRequest(200, 1, List.of(FACILITY_ID), timeString, timeString);
        log.debug("kware 요청: {}", request);

        JsonNode response = restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(JsonNode.class);
        log.debug("kware 응답: {}", response);
        if (response == null) {
            throw new IllegalStateException("kware 응답 없음");
        }
        return response;
    }

    private Waterdata toWaterdata(JsonNode json, Float ph, Float sa) {
        String tankid = json.path("tankId").asText().substring(6);
        Lazy<Waterdata> latest = Lazy.of(() -> waterdataRepository.findFirstByTankidOrderByTimeDesc(tankid));
        Float wt = floatOr(json, "wt", tankid, () -> latest.get().getWt());
        Float wdo = floatOr(json, "do1", tankid, () -> latest.get().getWdo());
        // collectDe가 날짜, collectTime이 시각
        LocalDateTime time = LocalDateTime.parse(
                json.path("collectDe").asText() + " " + json.path("collectTime").asText(), SENSOR_TIME_FORMATTER);

        return Waterdata.builder().tankid(tankid).time(time).wt(wt).wdo(wdo).ph(ph).sa(sa).build();
    }

    // 센서 데이터가 없으면 1개월 전(없으면 3개월 전) 같은 시각의 데이터를 현재 시각으로 복사해 사용
    private List<Waterdata> pastData(LocalDateTime now) {
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        log.warn("수조 데이터 없음, 1개월 전 데이터로 대체");
        List<Waterdata> past = findAt(oneMonthAgo);
        if (past.get(0) == null) {
            log.warn("1개월 전 데이터도 없음, 3개월 전 데이터로 대체");
            past = findAt(oneMonthAgo.minusMonths(2));
        }
        if (past.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("대체할 과거 수조 데이터 없음");
        }
        past.forEach(w -> {
            w.setTime(now);
            w.setNum(null);
        });
        return past;
    }

    private List<Waterdata> findAt(LocalDateTime time) {
        return Tanks.IDS.stream().map(id -> waterdataRepository.findFistByTankidAndTime(id, time)).toList();
    }

    record SensorStatusRequest(int pageSize, int pageNumber, List<String> fcltyIds, String startDate, String endDate) {}
}
