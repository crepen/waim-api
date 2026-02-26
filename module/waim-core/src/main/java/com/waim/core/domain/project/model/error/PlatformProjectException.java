package com.waim.core.domain.project.model.error;

import com.waim.core.common.model.error.PlatformException;

public class PlatformProjectException extends PlatformException {


    public PlatformProjectException(int statusCode, String errorCode, String message, Throwable cause) {
        super(statusCode, errorCode, message, cause);
    }

    public PlatformProjectException(int statusCode, String errorCode, String message) {
        super(statusCode, errorCode, message);
    }

    public PlatformProjectException(int statusCode, String errorCode, String message, String[] messageArgs) {
        super(statusCode, errorCode, message, messageArgs);
    }

    public PlatformProjectException(int statusCode, String errorCode, String message, String[] messageArgs, Throwable cause) {
        super(statusCode, errorCode, message, messageArgs, cause);
    }
}
