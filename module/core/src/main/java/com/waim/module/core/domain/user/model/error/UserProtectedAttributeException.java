package com.waim.module.core.domain.user.model.error;

import org.springframework.http.HttpStatus;

import java.util.Arrays;

public class UserProtectedAttributeException extends UserServerException {

    public UserProtectedAttributeException(String attributeKey) {
        super(
                HttpStatus.FORBIDDEN.value(),
                "WSE_USR_0014",
                "waim.domain.user.error.wse_usr_0014.protected_user_attr",
                new String[]{attributeKey},
                String.format("This attribute is protected. (%s)" , attributeKey)
        );
    }
}
