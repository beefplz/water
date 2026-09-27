package org.capstone.water.apireader;

import java.util.List;

// 수집·예측 대상 수조 ID. 외부 API 응답과 예측 결과도 이 순서를 따른다
public final class Tanks {
    public static final List<String> IDS = List.of("iw1", "rt1", "rt2");

    private Tanks() {}
}
