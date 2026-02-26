package com.waim.core.plugin.gitlab.model.error;

import com.waim.core.plugin.common.model.error.PluginException;
import lombok.Getter;

@Getter
public class GitLabPluginException extends PluginException {


    public GitLabPluginException(int statusCode,String errorCode, String message) {
        super(statusCode, errorCode ,message);
    }

    public GitLabPluginException(int statusCode , String errorCode, String message , String[] messageArgs) {
        super(statusCode, errorCode ,message ,messageArgs);
    }

    public GitLabPluginException(int statusCode ,String errorCode,String message,  Throwable cause) {
        super(statusCode, errorCode , message, cause);
    }

    public GitLabPluginException(int statusCode, String errorCode,String message,String[] messageArgs ,  Throwable cause) {
        super(statusCode, errorCode , message, messageArgs, cause);
    }
}
