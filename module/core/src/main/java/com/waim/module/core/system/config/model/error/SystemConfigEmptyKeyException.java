package com.waim.module.core.system.config.model.error;

import org.springframework.http.HttpStatus;

public class SystemConfigEmptyKeyException extends SystemConfigServerException {
    public SystemConfigEmptyKeyException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_SYS_0001",
                "waim.system.config.error.wse_sys_0001.empty_config_key",
                "Config key cannot be empty."
        );
    }
}
