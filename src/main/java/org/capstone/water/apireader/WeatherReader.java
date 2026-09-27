package org.capstone.water.apireader;

import com.fasterxml.jackson.databind.JsonNode;
import org.capstone.water.config.ApiProperties;
import org.capstone.water.repository.entity.weather.Weather;
import org.capstone.water.repository.entity.weather.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.capstone.water.apireader.JsonFields.floatOr;
import static org.capstone.water.apireader.JsonFields.shortOr;

// 국립해양조사원(khoa) 조위관측소 최신 관측값 수집
@Component
public class WeatherReader {
    private static final Logger log = LoggerFactory.getLogger(WeatherReader.class);
    private static final String KHOA_URL = "https://www.khoa.go.kr/api/oceangrid/tideObsRecent/search.do";
    private static final String STATION = "DT_0027";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final RestClient restClient;
    private final WeatherRepository weatherRepository;
    private final ApiProperties api;

    public WeatherReader(@Qualifier("apiRestClient") RestClient restClient,
                         WeatherRepository weatherRepository, ApiProperties api) {
        this.restClient = restClient;
        this.weatherRepository = weatherRepository;
        this.api = api;
    }

    public Weather weatherRead(String timeString) {
        // 서비스 키에 '/', '='가 있어 기존처럼 인코딩하지 않은 URL로 요청
        URI uri = URI.create(KHOA_URL + "?ServiceKey=" + api.khoaKey() + "&ObsCode=" + STATION + "&ResultType=json");
        JsonNode response = restClient.get().uri(uri).retrieve().body(JsonNode.class);
        JsonNode data = response == null ? null : response.path("result").get("data");
        if (data == null || !data.isObject()) {
            throw new IllegalStateException("khoa 응답에 관측 데이터 없음: " + response);
        }
        log.debug("khoa 응답: {}", data);

        // 비어 있는 값은 가장 최근 저장값으로 채움 (필요할 때 한 번만 조회)
        Lazy<Weather> latest = Lazy.of(weatherRepository::findFirstByOrderByTimeDesc);

        return Weather.builder()
                .time(LocalDateTime.parse(timeString, FORMATTER))
                .swt(floatOr(data, "water_temp", "기상", () -> latest.get().getSwt()))
                .wdir(shortOr(data, "wind_dir", "기상", () -> latest.get().getWdir()))
                .ws(floatOr(data, "wind_speed", "기상", () -> latest.get().getWs()))
                .ssa(floatOr(data, "Salinity", "기상", () -> latest.get().getSsa()))
                .sat(floatOr(data, "air_temp", "기상", () -> latest.get().getSat()))
                .sap(floatOr(data, "air_press", "기상", () -> latest.get().getSap()))
                .swh(floatOr(data, "tide_level", "기상", () -> latest.get().getSwh()))
                // 유향·유속은 이 관측소에서 제공하지 않아 0으로 저장
                .scd(0F)
                .scs(0F)
                .build();
    }
}
