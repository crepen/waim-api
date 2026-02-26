package com.waim.core.plugin.gitlab.model.error;

import lombok.Getter;

@Getter
public class GitLabApiUnknownException extends GitLabPluginException{

    private static final String errorCode = "ER-SY-PLUG-GITLAB-0002";
    private static final Integer statusCode = 500;
    private static final String errorMessage = "waim.api.plugin.gitlab.error.server.unknown_error";
    private final String apiErrorMessage;

    public GitLabApiUnknownException(String apiErrorMessage , Throwable cause) {
        super(statusCode , errorCode , errorMessage , cause);
        this.apiErrorMessage = apiErrorMessage;
    }

    public GitLabApiUnknownException(String apiErrorMessage){
        super(statusCode , errorCode , errorMessage );
        this.apiErrorMessage = apiErrorMessage;
    }


}
