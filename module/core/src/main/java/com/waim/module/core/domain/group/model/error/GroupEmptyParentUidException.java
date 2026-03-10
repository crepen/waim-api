package com.waim.module.core.domain.group.model.error;

import org.springframework.http.HttpStatus;

public class GroupEmptyParentUidException extends GroupServerException {
    public GroupEmptyParentUidException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_GRP_0007",
                "waim.domain.group.error.wse_grp_0007.empty_parent_uid",
                "Parent group UID cannot be empty."
        );
    }
}
