package com.waim.core.plugin.gitlab.model.enumable;

import lombok.Getter;


@Getter
public enum GitLabValidateError {
    PROJECT_ID("waim.api.plugin.gitlab.error.config.validate_error.project_id"),
    GITLAB_BASE_URL("waim.api.plugin.gitlab.error.config.validate_error.base_url"),
    GITLAB_PROJECT_TOKEN("waim.api.plugin.gitlab.error.config.validate_error.token")

    ;


    private final String value;

    private GitLabValidateError(String value){
        this.value = value;
    }
}
