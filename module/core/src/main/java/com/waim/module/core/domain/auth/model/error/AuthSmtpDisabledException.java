package com.waim.module.core.domain.auth.model.error;

import org.springframework.http.HttpStatus;

public class AuthSmtpDisabledException extends AuthServerException {

    public AuthSmtpDisabledException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_ATH_0008",
                "waim.domain.auth.error.wse_ath_0008.smtp_disabled",
                "SMTP is disabled."
        );
    }
}
