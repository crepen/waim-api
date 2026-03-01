package com.waim.module.core.domain.project.model.error;

import org.springframework.http.HttpStatus;

public class ProjectEmptyUidException extends ProjectServerException{
    public ProjectEmptyUidException() {
        super(
                HttpStatus.NOT_FOUND.value(),
                "WSE_PRJ_0003",
                "waim.domain.project.error.wse_prj_0003.empty_uid",
                "Project UID cannot be empty."
        );
    }
}
