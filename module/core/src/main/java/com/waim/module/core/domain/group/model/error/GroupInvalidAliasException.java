package com.waim.module.core.domain.group.model.error;

import org.springframework.http.HttpStatus;

public class GroupInvalidAliasException extends GroupServerException {
    public GroupInvalidAliasException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_GRP_0005",
                "waim.domain.group.error.wse_grp_0005.invalid_alias",
                "Group alias is invalid."
        );
    }
}
