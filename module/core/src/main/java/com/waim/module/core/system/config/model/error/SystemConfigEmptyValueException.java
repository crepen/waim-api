package com.waim.module.core.system.config.model.error;

import org.springframework.http.HttpStatus;

public class SystemConfigEmptyValueException extends SystemConfigServerException {
    public SystemConfigEmptyValueException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_SYS_0002",
                "waim.system.config.error.wse_sys_0002.empty_config_value",
                "Config value cannot be empty."
        );
    }
}
