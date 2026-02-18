package com.waim.core.plugin.gitlab.model.error;

import org.springframework.http.HttpStatus;

public class GitLabConnException extends GitLabPluginException{

    private static final String errorCode = "ER-SY-PLUG-GITLAB-0001";
    private static final Integer statusCode = HttpStatus.BAD_GATEWAY.value();
    private static final String errorMessage = "waim.api.plugin.gitlab.error.server.connect_failed";

    public GitLabConnException(Throwable cause) {
        super(statusCode , errorCode , errorMessage , cause);
    }

    public GitLabConnException() {
        super(statusCode , errorCode , errorMessage );
    }
}
