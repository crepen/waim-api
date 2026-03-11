package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserInvalidPasswordPolicyException extends UserServerException {

    public UserInvalidPasswordPolicyException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_USR_0016",
                "waim.domain.user.error.wse_usr_0016.invalid_password_policy",
                "Password does not satisfy system password policy."
        );
    }
}
