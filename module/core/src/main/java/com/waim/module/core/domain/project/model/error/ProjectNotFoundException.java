package com.waim.module.core.domain.project.model.error;

import org.springframework.http.HttpStatus;

public class ProjectNotFoundException extends ProjectServerException{
    public ProjectNotFoundException() {
        super(
                HttpStatus.NOT_FOUND.value(),
                "WSE_PRJ_0001",
                "waim.domain.project.error.wse_prj_0001.not_found",
                "Project not found."
        );
    }
}
