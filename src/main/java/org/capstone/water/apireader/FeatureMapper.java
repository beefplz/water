package org.capstone.water.apireader;

import org.capstone.water.repository.entity.mldata.MldataMapping;

import java.util.List;

// DB의 mldata 행을 예측 모델 입력 배열로 변환
public final class FeatureMapper {
    public static final List<String> TANK_IDS = List.of("iw1", "rt1", "rt2");

    // 16방위 구간의 상한 각도와 모델이 학습한 방위 코드. 350도 이상은 북(8)
    private static final int[] UPPER_DEGREES = {11, 34, 56, 79, 101, 124, 146, 169, 191, 214, 236, 260, 281, 304, 326, 349};
    private static final float[] DIRECTION_CODES = {8, 16, 9, 10, 11, 12, 1, 2, 3, 15, 13, 14, 4, 6, 5, 7};

    private FeatureMapper() {}

    /**
     * 모델 입력 순서: 유속, 풍향코드, 유향, 수온, 0, DO, 양식장수온, pH, 염도, 0.
     * 행 순서는 조회 결과(최신순) 그대로 유지한다.
     */
    public static float[][] toInput(List<MldataMapping> rows, int expectedRows) {
        if (rows.size() < expectedRows) {
            throw new IllegalStateException("모델 입력 데이터 부족: " + rows.size() + "/" + expectedRows);
        }
        float[][] input = new float[expectedRows][];
        for (int i = 0; i < expectedRows; i++) {
            MldataMapping r = rows.get(i);
            input[i] = new float[]{r.getScs(), windDirectionCode(r.getWdir()), r.getScd(), r.getSwt(),
                    0F, r.getWdo(), r.getWt(), r.getPh(), r.getSa(), 0F};
        }
        return input;
    }

    static float windDirectionCode(float degree) {
        if (degree >= 350) {
            return 8;
        }
        for (int i = 0; i < UPPER_DEGREES.length; i++) {
            if (degree <= UPPER_DEGREES[i]) {
                return DIRECTION_CODES[i];
            }
        }
        return 8;
    }
}
