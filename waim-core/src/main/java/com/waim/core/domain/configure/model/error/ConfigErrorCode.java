package com.waim.core.domain.configure.model.error;

import com.waim.core.common.model.error.WAIMErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ConfigErrorCode implements WAIMErrorCode {
    CONFIG_UNDEFINED_WA_CFG0001(HttpStatus.NOT_FOUND , "WA_CFG0001" , "waim.api.config.common.error.undefined")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
