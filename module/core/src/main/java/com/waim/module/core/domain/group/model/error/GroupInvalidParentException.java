package com.waim.module.core.domain.group.model.error;

import org.springframework.http.HttpStatus;

public class GroupInvalidParentException extends GroupServerException {
    public GroupInvalidParentException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_GRP_0010",
                "waim.domain.group.error.wse_grp_0010.invalid_parent",
                "Parent group is invalid."
        );
    }
}
