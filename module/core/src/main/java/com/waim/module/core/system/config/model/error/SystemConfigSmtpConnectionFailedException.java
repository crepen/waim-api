package com.waim.module.core.system.config.model.error;

import org.springframework.http.HttpStatus;

public class SystemConfigSmtpConnectionFailedException extends SystemConfigServerException {

    public SystemConfigSmtpConnectionFailedException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_SYS_0003",
                "waim.system.config.error.wse_sys_0003.smtp_connection_failed",
                "SMTP connection test failed."
        );
    }
}
