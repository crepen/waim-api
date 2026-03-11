package com.waim.module.core.domain.project.model.error;

import org.springframework.http.HttpStatus;

public class ProjectEmptyNameException extends ProjectServerException {
    public ProjectEmptyNameException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_PRJ_0008",
                "waim.domain.project.error.wse_prj_0008.empty_name",
                "Project name cannot be empty."
        );
    }
}
