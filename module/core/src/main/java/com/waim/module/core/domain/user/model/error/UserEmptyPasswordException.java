package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserEmptyPasswordException extends UserServerException{
    public UserEmptyPasswordException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_USR_0009",
                "waim.domain.user.error.wse_usr_0009.empty_password",
                "User password cannot be empty."
        );
    }
}
