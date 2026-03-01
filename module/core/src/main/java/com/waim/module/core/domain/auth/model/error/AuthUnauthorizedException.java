package com.waim.module.core.domain.auth.model.error;

import org.springframework.http.HttpStatus;

public class AuthUnauthorizedException extends AuthServerException{
    public AuthUnauthorizedException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_ATH_0006",
                "waim.domain.auth.error.wse_ath_0006.unauthorized",
                "Unauthorized."
        );
    }
}
