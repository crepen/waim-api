package com.waim.core.plugin.gitlab.model.error;

import org.springframework.http.HttpStatus;

public class GitLabProjectNotFoundException extends GitLabPluginException {

    private static final String errorCode = "ER-SY-PLUG-GITLAB-0004";
    private static final Integer statusCode = HttpStatus.NOT_FOUND.value();
    private static final String errorMessage = "waim.api.plugin.gitlab.error.project.not_found";

    public GitLabProjectNotFoundException(final Throwable cause) {
        super(statusCode, errorCode, errorMessage, cause);
    }

    public GitLabProjectNotFoundException() {
        super(statusCode, errorCode, errorMessage);
    }
}
