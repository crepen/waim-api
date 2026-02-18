package com.waim.core.common.model.error;

import lombok.Getter;

@Getter
public class PlatformException extends RuntimeException{
    private String[] messageArgs;
    private int statusCode;
    String errorCode;

    public PlatformException(int statusCode, String errorCode ,String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public PlatformException(int statusCode , String errorCode , String message , String[] messageArgs) {
        super(message);
        this.statusCode = statusCode;
        this.messageArgs = messageArgs;
        this.errorCode = errorCode;
    }

    public PlatformException(int statusCode , String errorCode,String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public PlatformException(int statusCode, String errorCode, String message,String[] messageArgs , Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.messageArgs = messageArgs;
        this.errorCode = errorCode;
    }
}
