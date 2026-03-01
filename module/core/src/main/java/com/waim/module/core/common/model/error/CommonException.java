package com.waim.module.core.common.model.error;


import lombok.Getter;

public class CommonException extends ServerException {
    public CommonException(int statusCode, String errorCode, String localeMessageCode, String message) {
        super("COMMON", statusCode, errorCode, localeMessageCode, message);
    }

    public CommonException(int statusCode, String errorCode, String localeMessageCode, String message, Throwable throwable) {
        super("COMMON", statusCode, errorCode, localeMessageCode, message, throwable);
    }

    public CommonException(int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message) {
        super("COMMON", statusCode, errorCode, localeMessageCode, messageArgs, message);
    }

    public CommonException(int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message, Throwable throwable) {
        super("COMMON", statusCode, errorCode, localeMessageCode, messageArgs, message, throwable);
    }

}
