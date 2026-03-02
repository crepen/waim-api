package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

public class UserUnsupportedRoleException extends UserServerException {

    public UserUnsupportedRoleException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_USR_0012",
                "waim.domain.user.error.wse_usr_0012.unsupported_user_role",
                "Unsupported user role."
        );
    }
}
