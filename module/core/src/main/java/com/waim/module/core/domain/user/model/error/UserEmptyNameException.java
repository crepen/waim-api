package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserEmptyNameException extends UserServerException{
    public UserEmptyNameException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_USR_0007",
                "waim.domain.user.error.wse_usr_0007.empty_name",
                "User name cannot be empty."
        );
    }
}
