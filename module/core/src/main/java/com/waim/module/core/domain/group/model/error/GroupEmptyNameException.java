package com.waim.module.core.domain.group.model.error;

import org.springframework.http.HttpStatus;

public class GroupEmptyNameException extends GroupServerException {
    public GroupEmptyNameException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_GRP_0003",
                "waim.domain.group.error.wse_grp_0003.empty_name",
                "Group name cannot be empty."
        );
    }
}
