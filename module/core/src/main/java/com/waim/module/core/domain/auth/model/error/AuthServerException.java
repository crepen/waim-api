package com.waim.module.core.domain.auth.model.error;

import com.waim.module.core.common.model.error.ServerException;

public class AuthServerException extends ServerException {
    public AuthServerException(int statusCode, String errorCode, String localeMessageCode, String message) {
        super("AUTH", statusCode, errorCode, localeMessageCode, message);
    }

    public AuthServerException(int statusCode, String errorCode, String localeMessageCode, String message, Throwable throwable) {
        super("AUTH", statusCode, errorCode, localeMessageCode, message, throwable);
    }

    public AuthServerException(int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message) {
        super("AUTH", statusCode, errorCode, localeMessageCode, messageArgs, message);
    }

    public AuthServerException(int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message, Throwable throwable) {
        super("AUTH", statusCode, errorCode, localeMessageCode, messageArgs, message, throwable);
    }
}
