package com.waim.core.common.model.error;

public class PlatformUnknownException extends PlatformException{


    private static final Integer statusCode = 500;
    private static final String errorCode = "ER-SY-UNK";
    private static final String errorMessage = "waim.api.common.error.internal_server_error";

    public PlatformUnknownException() {
        super(statusCode , errorCode , errorMessage);
    }

    public PlatformUnknownException(Throwable throwable) {
        super(statusCode , errorCode , errorMessage , throwable);
    }

}
