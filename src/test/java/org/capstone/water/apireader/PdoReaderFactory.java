package org.capstone.water.apireader;

import org.capstone.water.repository.entity.mldata.MldataViewRepository;
import org.springframework.web.client.RestClient;

// 테스트에서 Reader를 만드는 방법을 한곳에 모음 (구현이 바뀌어도 테스트 본문은 그대로 유지)
final class PdoReaderFactory {
    private PdoReaderFactory() {}

    static PdoReader pdoReader(MldataViewRepository repo, String tritonUrl) {
        return new PdoReader(repo, tritonClient(tritonUrl));
    }

    static PdoWeekReader pdoWeekReader(MldataViewRepository repo, String tritonUrl) {
        return new PdoWeekReader(repo, tritonClient(tritonUrl));
    }

    private static TritonClient tritonClient(String tritonUrl) {
        return new TritonClient(RestClient.builder().baseUrl(tritonUrl).build());
    }
}
