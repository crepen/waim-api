package com.waim.module.core.domain.project.model.error;

import org.springframework.http.HttpStatus;

public class ProjectEmptyAliasException extends ProjectServerException {
    public ProjectEmptyAliasException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_PRJ_0009",
                "waim.domain.project.error.wse_prj_0009.empty_alias",
                "Project alias cannot be empty."
        );
    }
}
