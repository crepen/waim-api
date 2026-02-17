package com.waim.core.plugin.gitlab.model.error;

public class GitLabConnException extends GitLabPluginException{
    public GitLabConnException(String message) {
        super(message , 500);
    }
    public GitLabConnException(String message, Throwable cause) {
        super(message, 500 , cause );
    }
}
