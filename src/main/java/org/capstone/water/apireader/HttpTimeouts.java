package org.capstone.water.apireader;

// 외부 호출 타임아웃(ms). 상대 서버가 응답하지 않아도 스케줄러가 멈추지 않도록 모든 외부 호출에 적용
final class HttpTimeouts {
    static final int CONNECT = 3_000;
    static final int READ = 5_000;
    static final int INFERENCE_READ = 10_000;

    private HttpTimeouts() {}
}
