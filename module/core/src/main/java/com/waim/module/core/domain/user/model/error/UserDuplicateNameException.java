package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserDuplicateNameException extends UserServerException{
    public UserDuplicateNameException() {
        super(
                HttpStatus.CONFLICT.value(),
                "WSE_USR_0004",
                "waim.domain.user.error.wse_usr_0004.duplicate_name",
                "Duplicate user name found."
        );
    }
}
