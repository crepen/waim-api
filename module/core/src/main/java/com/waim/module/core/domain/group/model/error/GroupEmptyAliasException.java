package com.waim.module.core.domain.group.model.error;

import org.springframework.http.HttpStatus;

public class GroupEmptyAliasException extends GroupServerException {
    public GroupEmptyAliasException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_GRP_0004",
                "waim.domain.group.error.wse_grp_0004.empty_alias",
                "Group alias cannot be empty."
        );
    }
}
