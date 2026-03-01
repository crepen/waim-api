package com.waim.module.data.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RemoveUserProp {
    private String userUid;
    private boolean isAdmin;
    private String actionUserUid;
}
