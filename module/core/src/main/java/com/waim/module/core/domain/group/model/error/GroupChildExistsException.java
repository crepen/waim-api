package com.waim.module.core.domain.group.model.error;

import org.springframework.http.HttpStatus;

public class GroupChildExistsException extends GroupServerException {
    public GroupChildExistsException() {
        super(
                HttpStatus.CONFLICT.value(),
                "WSE_GRP_0008",
                "waim.domain.group.error.wse_grp_0008.child_exists",
                "Cannot delete group with child groups."
        );
    }
}
