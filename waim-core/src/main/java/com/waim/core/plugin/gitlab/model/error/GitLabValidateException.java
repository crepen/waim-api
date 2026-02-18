package com.waim.core.plugin.gitlab.model.error;

import com.waim.core.plugin.gitlab.model.enumable.GitLabValidateError;
import org.springframework.http.HttpStatus;

public class GitLabValidateException extends GitLabPluginException{
    private static final String errorCode = "ER-SY-PLUG-GITLAB-0006";
    private static final String errorMessage = "waim.api.plugin.gitlab.error.config.validate_error";
    private static final Integer statusCode = HttpStatus.BAD_REQUEST.value();


    public GitLabValidateException(GitLabValidateError error){
        super(statusCode , errorCode , errorMessage , new String[]{error.getValue()});
    }
}
