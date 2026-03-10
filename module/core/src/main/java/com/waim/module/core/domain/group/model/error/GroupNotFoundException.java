package com.waim.module.core.domain.group.model.error;

import org.springframework.http.HttpStatus;

public class GroupNotFoundException extends GroupServerException {
    public GroupNotFoundException() {
        super(
                HttpStatus.NOT_FOUND.value(),
                "WSE_GRP_0001",
                "waim.domain.group.error.wse_grp_0001.not_found",
                "Group not found."
        );
    }
}
