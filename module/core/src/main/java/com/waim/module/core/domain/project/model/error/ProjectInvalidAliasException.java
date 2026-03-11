package com.waim.module.core.domain.project.model.error;

import org.springframework.http.HttpStatus;

public class ProjectInvalidAliasException extends ProjectServerException {
    public ProjectInvalidAliasException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_PRJ_0010",
                "waim.domain.project.error.wse_prj_0010.invalid_alias",
                "Project alias is invalid."
        );
    }
}
