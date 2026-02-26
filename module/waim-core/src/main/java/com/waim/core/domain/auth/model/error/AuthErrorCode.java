package com.waim.core.domain.auth.model.error;


import com.waim.core.common.model.error.WAIMErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;


public class AuthErrorCode {
    @Getter
    @RequiredArgsConstructor
    public enum Common implements WAIMErrorCode {
        TOKEN_INVALID(HttpStatus.BAD_REQUEST , "WA_AUC0001" , "waim.api.auth.error.common.invalid_token"),
        REFRESH_TOKEN_INVALID_TOKEN(HttpStatus.BAD_REQUEST , "WA_AU0006" , "waim.api.auth.error.refresh_token.invalid.token"),
        REFRESH_TOKEN_EXPIRE_TOKEN(HttpStatus.UNAUTHORIZED , "WA_AU0007" , "waim.api.auth.error.refresh_token.expire_token"),
        REFRESH_TOKEN_UNMATCHED_TYPE(HttpStatus.BAD_REQUEST , "WA_AU0008" , "waim.api.auth.error.refresh_token.unmatched_type"),

        ;
        private final HttpStatus httpStatus;
        private final String code;
        private final String message;
    }


    @Getter
    @RequiredArgsConstructor
    public enum Validate implements WAIMErrorCode {
        LOGIN_INVALID_ID(HttpStatus.BAD_REQUEST , "WA_AU0001" , "waim.api.auth.error.login.invalid.id"),
        LOGIN_INVALID_PASSWORD(HttpStatus.BAD_REQUEST , "WA_AU0002" , "waim.api.auth.error.login.invalid.password"),
        LOGIN_INVALID_NOT_FOUND_USER(HttpStatus.NOT_FOUND , "WA_AU0003" , "waim.api.auth.error.login.invalid.user_not_found"),
        LOGIN_INVALID_PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED , "WA_AU0004" , "waim.api.auth.error.login.invalid.password_not_match"),
        LOGIN_INVALID_UNMATCHED_GRANT_TYPE(HttpStatus.UNAUTHORIZED , "WA_AU0005" , "waim.api.auth.error.login.invalid.grant_type"),



        LOGIN_FAILED_USER_STATE_PENDING(HttpStatus.FORBIDDEN , "WA_AE_00001" , "waim.api.auth.error.login.failed.user_state_pending"),
        LOGIN_FAILED_USER_STATE_WITHDRAWN(HttpStatus.FORBIDDEN , "WA_AE_00002" , "waim.api.auth.error.login.failed.user_state_withdrawn"),
        LOGIN_FAILED_USER_STATE_SUSPENDED(HttpStatus.FORBIDDEN , "WA_AE_00003" , "waim.api.auth.error.login.failed.user_state_suspended"),
        LOGIN_FAILED_USER_STATE_UNKNOWN(HttpStatus.FORBIDDEN , "WA_AE_00004" , "waim.api.auth.error.login.failed.user_state_unknown"),

        ;

        private final HttpStatus httpStatus;
        private final String code;
        private final String message;
    }
}
