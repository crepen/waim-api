package com.waim.taskmaster.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary // 기본 직렬화 도구로 지정
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                // 타임존 및 날짜 모듈 활성화 (Java 8 Date/Time API)
                .addModule(new JavaTimeModule())
                // 모르는 속성이 있어도 에러를 내지 않음
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // 날짜를 숫자 배열이 아닌 ISO-8601 문자열로 저장
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                // 빈 객체 직렬화 시 에러 방지
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                // Null 값인 필드는 제외 (선택 사항: 저장 공간 절약)
                .defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.ALWAYS))
                .build();
    }
}
