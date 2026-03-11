package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserInvalidEmailException extends UserServerException {

    public UserInvalidEmailException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_USR_0017",
                "waim.domain.user.error.wse_usr_0017.invalid_email",
                "Invalid user email format."
        );
    }
}
