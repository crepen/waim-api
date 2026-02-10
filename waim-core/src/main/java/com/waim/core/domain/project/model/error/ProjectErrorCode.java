package com.waim.core.domain.project.model.error;

import com.waim.core.common.model.error.WAIMErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProjectErrorCode implements WAIMErrorCode {
    PROJECT_ALIAS_NOT_ALLOW(HttpStatus.BAD_REQUEST , "WA_PRJ0001" , "waim.api.project.common.error.alias_invalid"),
    PROJECT_ALIAS_EMPTY(HttpStatus.BAD_REQUEST , "WA_PRJ0002" , "waim.api.project.common.error.alias_empty"),
    PROJECT_NAME_EMPTY(HttpStatus.BAD_REQUEST , "WA_PRJ0003" , "waim.api.project.common.error.name_empty"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
