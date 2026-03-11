package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserSignupDisabledException extends UserServerException {

    public UserSignupDisabledException() {
        super(
                HttpStatus.FORBIDDEN.value(),
                "WSE_USR_0015",
                "waim.domain.user.error.wse_usr_0015.signup_disabled",
                "User signup is disabled."
        );
    }
}
