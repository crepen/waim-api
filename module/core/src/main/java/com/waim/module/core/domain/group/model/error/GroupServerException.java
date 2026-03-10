package com.waim.module.core.domain.group.model.error;

import com.waim.module.core.common.model.error.ServerException;

public class GroupServerException extends ServerException {
    public GroupServerException(int statusCode, String errorCode, String localeMessageCode, String message) {
        super("GROUP", statusCode, errorCode, localeMessageCode, message);
    }

    public GroupServerException(int statusCode, String errorCode, String localeMessageCode, String message, Throwable throwable) {
        super("GROUP", statusCode, errorCode, localeMessageCode, message, throwable);
    }

    public GroupServerException(int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message) {
        super("GROUP", statusCode, errorCode, localeMessageCode, messageArgs, message);
    }

    public GroupServerException(int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message, Throwable throwable) {
        super("GROUP", statusCode, errorCode, localeMessageCode, messageArgs, message, throwable);
    }
}
