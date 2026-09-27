package org.capstone.water.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "api")
public record ApiProperties(
        @NotBlank String khoaKey,
        @NotBlank String kwareKey,
        @NotBlank String tritonUrl
) {}
