package com.waim.module.core.common.model.error;


import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
    private String[] messageArgs;

    private final String localeMessageCode;
    private final String errorCode;
    private final int statusCode;
    private final String domain;

    public ServerException(String domain, int statusCode, String errorCode, String localeMessageCode, String message) {
        super(message);
        this.domain = domain;
        this.localeMessageCode = localeMessageCode;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public ServerException(String domain, int statusCode, String errorCode, String localeMessageCode, String message, Throwable throwable) {
        super(message, throwable);
        this.domain = domain;
        this.localeMessageCode = localeMessageCode;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public ServerException(String domain, int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message) {
        super(message);
        this.domain = domain;
        this.localeMessageCode = localeMessageCode;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
        this.messageArgs = messageArgs;
    }

    public ServerException(String domain, int statusCode, String errorCode, String localeMessageCode, String[] messageArgs, String message, Throwable throwable) {
        super(message, throwable);
        this.domain = domain;
        this.localeMessageCode = localeMessageCode;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
        this.messageArgs = messageArgs;
    }

}
