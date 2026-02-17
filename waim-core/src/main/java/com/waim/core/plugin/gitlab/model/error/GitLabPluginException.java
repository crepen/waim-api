package com.waim.core.plugin.gitlab.model.error;

import lombok.Getter;

@Getter
public class GitLabPluginException extends RuntimeException{


    private final int statusCode;
    public GitLabPluginException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public GitLabPluginException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
