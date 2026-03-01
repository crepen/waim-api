package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserDuplicateEmailException extends UserServerException{
    public UserDuplicateEmailException() {
        super(
                HttpStatus.CONFLICT.value(),
                "WSE_USR_0005",
                "waim.domain.user.error.wse_usr_0005.duplicate_email",
                "Duplicate user email found."
        );
    }
}
