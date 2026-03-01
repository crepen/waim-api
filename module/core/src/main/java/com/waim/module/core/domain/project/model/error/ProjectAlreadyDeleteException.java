package com.waim.module.core.domain.project.model.error;

import org.springframework.http.HttpStatus;

public class ProjectAlreadyDeleteException extends ProjectServerException{
    public ProjectAlreadyDeleteException() {
        super(
                HttpStatus.NOT_FOUND.value(),
                "WSE_PRJ_0002",
                "waim.domain.project.error.wse_prj_0002.already_delete",
                "Project already deleted."
        );
    }
}
