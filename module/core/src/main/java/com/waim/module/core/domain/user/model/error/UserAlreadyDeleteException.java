package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserAlreadyDeleteException extends UserServerException {

    public UserAlreadyDeleteException() {
        super(
                HttpStatus.FORBIDDEN.value(),
                "WSE_USR_0011",
                "waim.domain.user.error.wse_usr_0011.already_delete",
                "Target user is already deleted."
        );
    }
}
