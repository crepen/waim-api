package com.waim.module.core.domain.auth.model.error;

import org.springframework.http.HttpStatus;

public class AuthSmtpSendFailedException extends AuthServerException {

    public AuthSmtpSendFailedException() {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "WSE_ATH_0009",
                "waim.domain.auth.error.wse_ath_0009.smtp_send_failed",
                "Failed to send email."
        );
    }
}
