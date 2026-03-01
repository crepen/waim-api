package com.waim.module.core.domain.user.model.error;

import com.waim.module.core.common.model.error.ServerException;

public class UserServerException extends ServerException {
    public UserServerException(int statusCode, String errorCode, String localeMessageCode, String message) {
        super("USER", statusCode, errorCode, localeMessageCode, message);
    }

    public UserServerException(int statusCode, String errorCode, String localeMessageCode, String message, Throwable throwable) {
        super("USER", statusCode, errorCode, localeMessageCode, message, throwable);
    }

    public UserServerException(int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message) {
        super("USER", statusCode, errorCode, localeMessageCode, messageArgs, message);
    }

    public UserServerException(int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message, Throwable throwable) {
        super("USER", statusCode, errorCode, localeMessageCode, messageArgs, message, throwable);
    }
}
