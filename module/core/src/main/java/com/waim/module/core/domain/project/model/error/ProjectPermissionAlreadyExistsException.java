package com.waim.module.core.domain.project.model.error;

import org.springframework.http.HttpStatus;

public class ProjectPermissionAlreadyExistsException extends ProjectServerException {
    public ProjectPermissionAlreadyExistsException() {
        super(
                HttpStatus.CONFLICT.value(),
                "WSE_PRJ_0005",
                "waim.domain.project.error.wse_prj_0005.permission_exists",
                "Project permission already exists."
        );
    }
}
