package com.waim.module.core.domain.project.model.error;

import com.waim.module.core.common.model.error.ServerException;

public class ProjectServerException extends ServerException {
    public ProjectServerException(int statusCode, String errorCode, String localeMessageCode, String message) {
        super("PROJECT", statusCode, errorCode, localeMessageCode, message);
    }

    public ProjectServerException(int statusCode, String errorCode, String localeMessageCode, String message, Throwable throwable) {
        super("PROJECT", statusCode, errorCode, localeMessageCode, message, throwable);
    }

    public ProjectServerException(int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message) {
        super("PROJECT", statusCode, errorCode, localeMessageCode, messageArgs, message);
    }

    public ProjectServerException(int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message, Throwable throwable) {
        super("PROJECT", statusCode, errorCode, localeMessageCode, messageArgs, message, throwable);
    }
}
