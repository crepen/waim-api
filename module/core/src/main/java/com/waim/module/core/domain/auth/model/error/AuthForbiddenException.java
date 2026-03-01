package com.waim.module.core.domain.auth.model.error;

import org.springframework.http.HttpStatus;

public class AuthForbiddenException extends AuthServerException{
    public AuthForbiddenException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_ATH_0005",
                "waim.domain.auth.error.wse_ath_0005.forbidden",
                "Forbidden."
        );
    }
}
