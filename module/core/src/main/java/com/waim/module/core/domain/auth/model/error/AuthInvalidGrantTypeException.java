package com.waim.module.core.domain.auth.model.error;

import org.springframework.http.HttpStatus;

public class AuthInvalidGrantTypeException extends AuthServerException{
    public AuthInvalidGrantTypeException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_ATH_0004",
                "waim.domain.auth.error.wse_ath_0004.auth_grant_type_invalid",
                "Grant type not allowed."
        );
    }
}
