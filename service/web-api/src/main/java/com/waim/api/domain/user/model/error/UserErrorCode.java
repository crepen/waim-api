package com.waim.api.domain.user.model.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode {
    USER_NOT_FOUND_WA_U0001(HttpStatus.FORBIDDEN , "WA_U0001" , "waim.api.user.error.user_not_found"),


    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
