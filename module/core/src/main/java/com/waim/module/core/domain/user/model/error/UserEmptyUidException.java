package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserEmptyUidException extends UserServerException{
    public UserEmptyUidException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_USR_0010",
                "waim.domain.user.error.wse_usr_0010.empty_uid",
                "User UID cannot be empty."
        );
    }
}
