package com.waim.module.core.domain.group.model.error;

import org.springframework.http.HttpStatus;

public class GroupDuplicateAliasException extends GroupServerException {
    public GroupDuplicateAliasException() {
        super(
                HttpStatus.CONFLICT.value(),
                "WSE_GRP_0006",
                "waim.domain.group.error.wse_grp_0006.duplicate_alias",
                "Group alias already exists."
        );
    }
}
