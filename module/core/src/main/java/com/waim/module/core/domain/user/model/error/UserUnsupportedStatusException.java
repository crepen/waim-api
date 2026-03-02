package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserUnsupportedStatusException extends UserServerException {

    public UserUnsupportedStatusException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_USR_0013",
                "waim.domain.user.error.wse_usr_0013.unsupported_user_status",
                "Unsupported user status."
        );
    }
}
