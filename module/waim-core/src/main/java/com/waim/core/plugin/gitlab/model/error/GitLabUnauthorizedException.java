package com.waim.core.plugin.gitlab.model.error;

import org.springframework.http.HttpStatus;

public class GitLabUnauthorizedException extends GitLabPluginException{
    private static final String errorCode = "ER-SY-PLUG-GITLAB-0005";
    private static final String errorMessage = "waim.api.plugin.gitlab.error.server.unauthorized";
    private static final Integer statusCode = HttpStatus.UNAUTHORIZED.value();


    public GitLabUnauthorizedException() {
        super(
                statusCode,
                errorCode,
                errorMessage
        );
    }

    public GitLabUnauthorizedException(Throwable cause) {
        super(
                statusCode,
                errorCode,
                errorMessage,
                cause
        );
    }
}
