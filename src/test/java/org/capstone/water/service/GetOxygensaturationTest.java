package org.capstone.water.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class GetOxygensaturationTest {
    private final GetOxygensaturation calc = new GetOxygensaturation();

    @Test
    void 용존산소가_포화량과_같으면_100퍼센트() {
        assertThat(calc.getOxygensaturation(20f, 9.1f)).isEqualTo(100.0f);
    }

    @Test
    void 소수점_둘째_자리까지_계산한다() {
        // 8.0 / 9.1 = 0.879120... → 87.91%
        assertThat(calc.getOxygensaturation(20f, 8.0f)).isEqualTo(87.91f);
    }

    @ParameterizedTest
    @CsvSource({"9.5, 11.3", "10.4, 11.3", "25.5, 8.1", "30.4, 7.6"})
    void 수온은_반올림해서_표를_찾는다(float temp, float saturation) {
        assertThat(calc.getOxygensaturation(temp, saturation)).isEqualTo(100.0f);
    }

    @ParameterizedTest
    @ValueSource(floats = {9.4f, 30.5f, 0f, -1.5f, 35f})
    void 표_범위_밖_수온은_계산하지_않고_null(float temp) {
        assertThat(calc.getOxygensaturation(temp, 8f)).isNull();
    }
}
