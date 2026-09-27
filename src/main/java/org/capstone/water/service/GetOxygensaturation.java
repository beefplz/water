package org.capstone.water.service;

// 수온별 포화 용존산소량 표로 산소포화도(%)를 계산
public class GetOxygensaturation {
    private static final int MIN_TEMP = 10;
    private static final int MAX_TEMP = 30;

    // 10~30℃의 포화 용존산소량(mg/L)
    private static final float[] SATURATION = {
            11.3f, 11.1f, 10.9f, 10.7f, 10.5f, 10.1f, 10.0f, 9.8f, 9.6f, 9.4f, 9.1f,
            8.9f, 8.7f, 8.6f, 8.4f, 8.3f, 8.1f, 7.9f, 7.8f, 7.7f, 7.6f};

    /**
     * 수온을 반올림해 표에서 포화량을 찾고, 용존산소 / 포화량을 소수 둘째 자리 백분율로 반환.
     * 표 범위(10~30℃) 밖이거나 값이 없으면 계산할 수 없으므로 null.
     */
    public Float getOxygensaturation(Float temp, Float wdo) {
        if (temp == null || wdo == null) {
            return null;
        }
        int t = Math.round(temp);
        if (t < MIN_TEMP || t > MAX_TEMP) {
            return null;
        }
        return (float) Math.round(wdo / SATURATION[t - MIN_TEMP] * 10000) / 100;
    }
}
