package org.capstone.water.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component("customKeyGenerator")
public class CustomKeyGenerator implements KeyGenerator {

    private static final Logger log = LoggerFactory.getLogger(CustomKeyGenerator.class);

    @Override
    public Object generate(Object target, Method method, Object... params) {
        // 예: 메소드 이름 + 파라미터 조합으로 캐시 키 생성
        String methodName = method.getName();
        List<Object> paramList = Arrays.asList(params);

        String key = methodName + "[" + paramList.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",")) + "]";

        log.info("Generate key: {}", key);

        return key;
    }
}