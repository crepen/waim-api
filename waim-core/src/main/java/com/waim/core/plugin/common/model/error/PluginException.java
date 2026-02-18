package com.waim.core.plugin.common.model.error;

import com.waim.core.common.model.error.PlatformException;
import lombok.Getter;

@Getter
public class PluginException extends PlatformException {
    public PluginException(int statusCode , String errorCode, String message) {
        super(statusCode , errorCode , message);
    }

    public PluginException(int statusCode, String errorCode , final String message , String[] messageArgs) {
        super(statusCode, errorCode , message , messageArgs);
    }

    public PluginException(int statusCode , String errorCode,String message, final Throwable cause) {
        super(statusCode, errorCode, message, cause);
    }

    public PluginException(int statusCode, String errorCode, String message,String[] messageArgs , final Throwable cause) {
        super(statusCode, errorCode , message, messageArgs , cause);
    }
}
