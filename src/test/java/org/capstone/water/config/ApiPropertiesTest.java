package org.capstone.water.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ApiPropertiesTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
            .withUserConfiguration(Config.class);

    @EnableConfigurationProperties(ApiProperties.class)
    static class Config {}

    @Test
    void 설정값이_record에_연결된다() {
        runner.withPropertyValues("api.khoa-key=k1", "api.kware-key=k2", "api.triton-url=http://t")
                .run(ctx -> {
                    ApiProperties api = ctx.getBean(ApiProperties.class);
                    assertThat(api.khoaKey()).isEqualTo("k1");
                    assertThat(api.kwareKey()).isEqualTo("k2");
                    assertThat(api.tritonUrl()).isEqualTo("http://t");
                });
    }

    @Test
    void 값이_비어있으면_시작에_실패한다() {
        runner.withPropertyValues("api.khoa-key=", "api.kware-key=k2", "api.triton-url=http://t")
                .run(ctx -> assertThat(ctx).hasFailed());
    }
}
