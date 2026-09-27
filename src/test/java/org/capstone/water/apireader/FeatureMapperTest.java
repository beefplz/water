package org.capstone.water.apireader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeatureMapperTest {

    // 기존 if-else에서는 구간 사이의 소수 각도가 어느 조건에도 걸리지 않아 0이 되던 값들
    @ParameterizedTest
    @CsvSource({"11.5, 16", "34.5, 9", "349.5, 8", "359.9, 8"})
    void 구간_사이의_소수_각도도_방위_코드로_변환된다(float degree, float code) {
        assertThat(FeatureMapper.windDirectionCode(degree)).isEqualTo(code);
    }

    @Test
    void 입력_데이터가_부족하면_예외() {
        assertThatThrownBy(() -> FeatureMapper.toInput(List.of(), 30))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("0/30");
    }
}
