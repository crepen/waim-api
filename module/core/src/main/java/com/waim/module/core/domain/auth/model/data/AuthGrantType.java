package com.waim.module.core.domain.auth.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AuthGrantType {
    LOGIN("login"),
    REFRESH("refresh");

    private final String value;

    // JSON으로 나갈 때 사용되는 값 (serialize)
    @JsonValue
    public String getValue() {
        return value;
    }

    // JSON 문자열을 Enum으로 변환할 때 사용 (deserialize)
    @JsonCreator
    public static AuthGrantType from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        // value 필드값과 입력값이 일치하는지 비교하여 검색
        return Arrays.stream(AuthGrantType.values())
                .filter(type -> type.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
