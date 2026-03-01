package com.waim.module.data.domain.project;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RemoveProjectProp {
    private String projectUid;
    private String actionUserUid;
    private boolean isAdmin;
}
