package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserBlockException extends UserServerException {

    public UserBlockException() {
        super(
                HttpStatus.FORBIDDEN.value(),
                "WSE_USR_0002",
                "waim.domain.user.error.wse_usr_0002.user_block",
                "User blocked."
        );
    }
}
