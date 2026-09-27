package org.capstone.water.apireader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

// Triton 추론 서버(KServe v2 프로토콜) 호출
@Component
public class TritonClient {
    private static final Logger log = LoggerFactory.getLogger(TritonClient.class);

    private final RestClient restClient;

    public TritonClient(@Qualifier("tritonRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    // 모델에 [행 수 x 특성 수] 입력 하나를 보내고 첫 번째 출력값을 소수 둘째 자리로 반올림해 반환
    public float infer(String model, float[][] input) {
        InferRequest request = new InferRequest(
                List.of(new Input("input0", List.of(1, input.length, input[0].length), "FP32", input)),
                List.of(new Output("output0")));

        InferResponse response = restClient.post()
                .uri("/v2/models/{model}/infer", model)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(InferResponse.class);

        if (response == null || response.outputs() == null || response.outputs().isEmpty()) {
            throw new IllegalStateException("Triton 응답에 출력값이 없음: " + model);
        }
        double value = response.outputs().get(0).data().get(0);
        log.debug("Triton {} 출력값: {}", model, value);
        return (float) (Math.round(value * 100) / 100.0);
    }

    record InferRequest(List<Input> inputs, List<Output> outputs) {}

    record Input(String name, List<Integer> shape, String datatype, float[][] data) {}

    record Output(String name) {}

    record InferResponse(List<OutputData> outputs) {}

    record OutputData(String name, List<Double> data) {}
}
