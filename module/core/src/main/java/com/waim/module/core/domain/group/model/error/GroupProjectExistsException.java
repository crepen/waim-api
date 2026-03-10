package com.waim.module.core.domain.group.model.error;

import org.springframework.http.HttpStatus;

public class GroupProjectExistsException extends GroupServerException {
    public GroupProjectExistsException() {
        super(
                HttpStatus.CONFLICT.value(),
                "WSE_GRP_0009",
                "waim.domain.group.error.wse_grp_0009.project_exists",
                "Cannot delete group with linked projects."
        );
    }
}
