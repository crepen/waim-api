package com.waim.module.core.domain.auth.model.error;

import org.springframework.http.HttpStatus;

public class AuthTokenInvalidException extends AuthServerException{
    public AuthTokenInvalidException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_ATH_0001",
                "waim.domain.auth.error.wse_ath_0001.invalid_token",
                "Failed validate token."
        );
    }
}
