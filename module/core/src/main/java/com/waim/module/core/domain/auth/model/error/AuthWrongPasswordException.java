package com.waim.module.core.domain.auth.model.error;

import org.springframework.http.HttpStatus;

public class AuthWrongPasswordException extends AuthServerException{
    public AuthWrongPasswordException() {
        super(
                HttpStatus.UNAUTHORIZED.value(),
                "WSE_ATH_0007",
                "waim.domain.auth.error.wse_ath_0007.invalid_password",
                "Invalid password."
        );
    }
}
