package com.waim.module.core.domain.auth.model.error;

import org.springframework.http.HttpStatus;

public class AuthNotAllowTokenTypeException extends AuthServerException{
    public AuthNotAllowTokenTypeException() {
        super(
                HttpStatus.FORBIDDEN.value(),
                "WSE_ATH_0003",
                "waim.domain.auth.error.wse_ath_0003.token_type_not_allow",
                "Token type not allowed."
        );
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
