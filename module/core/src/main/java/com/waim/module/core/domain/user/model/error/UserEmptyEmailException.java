package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserEmptyEmailException extends UserServerException{
    public UserEmptyEmailException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_USR_0008",
                "waim.domain.user.error.wse_usr_0008.empty_email",
                "User email cannot be empty."
        );
    }
}
