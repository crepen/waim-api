package com.waim.core.plugin.gitlab.model.error;

public class GitLabPluginConfigureUndefinedException extends GitLabPluginException{

    private static final String errorCode = "ER-SY-PLUG-GITLAB-0003";
    private static final String errorMessage = "waim.api.plugin.gitlab.error.config.undefined";
    private static final Integer statusCode = 403;

    public GitLabPluginConfigureUndefinedException() {
        super(
                statusCode,
                errorCode,
                errorMessage
        );
    }

    public GitLabPluginConfigureUndefinedException(Throwable cause) {
        super(
                statusCode,
                errorCode,
                errorMessage,
                cause
        );
    }
}
