package com.waim.module.core.domain.project.model.error;

import org.springframework.http.HttpStatus;

public class ProjectEmptyGroupUidException extends ProjectServerException {
    public ProjectEmptyGroupUidException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_PRJ_0004",
                "waim.domain.project.error.wse_prj_0004.empty_group_uid",
                "Project group UID cannot be empty."
        );
    }
}
