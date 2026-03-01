package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends UserServerException {

    public UserNotFoundException() {
        super(
                HttpStatus.NOT_FOUND.value(),
                "WSE_USR_0001",
                "waim.domain.user.error.wse_usr_0001.not_found",
                "User not found."
        );
    }
}
