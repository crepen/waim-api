package com.waim.module.core.domain.group.model.error;

import org.springframework.http.HttpStatus;

public class GroupEmptyUidException extends GroupServerException {
    public GroupEmptyUidException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_GRP_0002",
                "waim.domain.group.error.wse_grp_0002.empty_uid",
                "Group UID cannot be empty."
        );
    }
}
