package com.waim.module.core.domain.project.model.error;

import org.springframework.http.HttpStatus;

public class ProjectDuplicateAliasException extends ProjectServerException {
    public ProjectDuplicateAliasException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_PRJ_0007",
                "waim.domain.project.error.wse_prj_0007.duplicate_alias",
                "Project alias already exists under same parent group."
        );
    }
}
