package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserDuplicateIdException extends UserServerException{
    public UserDuplicateIdException() {
        super(
                HttpStatus.CONFLICT.value(),
                "WSE_USR_0003",
                "waim.domain.user.error.wse_usr_0003.duplicate_id",
                "Duplicate user ID found."
        );
    }
}
