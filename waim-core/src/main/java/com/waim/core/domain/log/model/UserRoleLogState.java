package com.waim.core.domain.log.model;

import lombok.Getter;

@Getter
public enum UserRoleLogState {

    INSERT("ROLE_INSERT"),
    REMOVE("ROLE_REMOVE"),

    ;
    private final String value;

    UserRoleLogState(String value) {
        this.value = value;
    }

}
