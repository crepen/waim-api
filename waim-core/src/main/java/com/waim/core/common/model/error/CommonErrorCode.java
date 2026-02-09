package com.waim.core.common.model.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements WAIMErrorCode{
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "WA_C0001", "waim.api.common.error.bad_request"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "WA_C0002", "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "WA_C0003", "waim.api.common.error.internal_server_error"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "WA_C0004", "허용되지 않은 HTTP 메서드입니다."),


    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
