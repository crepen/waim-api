package com.waim.module.core.domain.auth.model.error;

import org.springframework.http.HttpStatus;

public class AuthTokenExpireException extends AuthServerException{
    public AuthTokenExpireException() {
        super(
                HttpStatus.UNAUTHORIZED.value(),
                "WSE_ATH_0002",
                "waim.domain.auth.error.wse_ath_0002.expired_token",
                "Session expired."
        );
    }
}
