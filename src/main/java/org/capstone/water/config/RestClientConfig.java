package org.capstone.water.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

// 외부 호출용 HTTP 클라이언트. 상대 서버가 응답하지 않아도 스케줄러가 멈추지 않도록 모든 호출에 타임아웃 적용
@Configuration
public class RestClientConfig {
    static final int CONNECT_TIMEOUT_MS = 3_000;
    static final int READ_TIMEOUT_MS = 5_000;
    static final int INFERENCE_READ_TIMEOUT_MS = 10_000;

    // 외부 공공/센서 API(khoa, kware) 호출용
    @Bean
    public RestClient apiRestClient(RestClient.Builder builder) {
        return builder.requestFactory(requestFactory(READ_TIMEOUT_MS)).build();
    }

    // Triton 추론 서버 호출용. 입력이 커서 응답 대기 시간을 더 길게 둠
    @Bean
    public RestClient tritonRestClient(RestClient.Builder builder, ApiProperties api) {
        return builder.baseUrl(api.tritonUrl())
                .requestFactory(requestFactory(INFERENCE_READ_TIMEOUT_MS))
                .build();
    }

    private static SimpleClientHttpRequestFactory requestFactory(int readTimeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        factory.setReadTimeout(readTimeoutMs);
        return factory;
    }
}
