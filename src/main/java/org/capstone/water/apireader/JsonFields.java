package org.capstone.water.apireader;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

// 외부 API 응답의 숫자 필드 읽기. 값이 없거나 숫자가 아니면 경고를 남기고 대체값을 사용
final class JsonFields {
    private static final Logger log = LoggerFactory.getLogger(JsonFields.class);

    private JsonFields() {}

    static Float floatOr(JsonNode node, String field, String source, Supplier<Float> fallback) {
        String text = textOf(node, field);
        if (text != null) {
            try {
                return Float.parseFloat(text);
            } catch (NumberFormatException ignored) {
                // 아래에서 대체값 사용
            }
        }
        log.warn("{} {} 값 없음({}), 직전 값으로 대체", source, field, text);
        return fallback.get();
    }

    static Short shortOr(JsonNode node, String field, String source, Supplier<Short> fallback) {
        String text = textOf(node, field);
        if (text != null) {
            try {
                return Short.parseShort(text);
            } catch (NumberFormatException ignored) {
                // 아래에서 대체값 사용
            }
        }
        log.warn("{} {} 값 없음({}), 직전 값으로 대체", source, field, text);
        return fallback.get();
    }

    private static String textOf(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        String text = value.asText().trim();
        return text.isEmpty() ? null : text;
    }
}
