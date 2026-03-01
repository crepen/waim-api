package com.waim.module.core.system.config.model.error;

import com.waim.module.core.common.model.error.ServerException;

public class SystemConfigServerException extends ServerException {
    public SystemConfigServerException(int statusCode, String errorCode, String localeMessageCode, String message) {
        super("SYS_CONFIG", statusCode, errorCode, localeMessageCode, message);
    }

    public SystemConfigServerException(int statusCode, String errorCode, String localeMessageCode, String message, Throwable throwable) {
        super("SYS_CONFIG", statusCode, errorCode, localeMessageCode, message, throwable);
    }

    public SystemConfigServerException(int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message) {
        super("SYS_CONFIG", statusCode, errorCode, localeMessageCode, messageArgs, message);
    }

    public SystemConfigServerException(int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message, Throwable throwable) {
        super("SYS_CONFIG", statusCode, errorCode, localeMessageCode, messageArgs, message, throwable);
    }
}
