package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserEmptyIdException extends UserServerException{
    public UserEmptyIdException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_USR_0006",
                "waim.domain.user.error.wse_usr_0006.empty_id",
                "User ID cannot be empty."
        );
    }
}
