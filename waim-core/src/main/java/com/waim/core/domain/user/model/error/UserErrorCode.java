package com.waim.core.domain.user.model.error;

import com.waim.core.common.model.error.WAIMErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;




public class UserErrorCode  {

    @Getter
    @RequiredArgsConstructor
    public enum Common implements WAIMErrorCode{
        USER_NOT_FOUND(HttpStatus.NOT_FOUND , "WA_UE0001" , "waim.api.user.error.invalid.user_not_found")

        ;

        private final HttpStatus httpStatus;
        private final String code;
        private final String message;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Validation implements WAIMErrorCode{
        @Deprecated
        ADD_ITEM_INVALID_ID(HttpStatus.BAD_REQUEST , "WA_U0001" , "waim.api.user.error.invalid.id"),
        @Deprecated
        ADD_ITEM_INVALID_PASSWORD(HttpStatus.BAD_REQUEST , "WA_U0002" , "waim.api.user.error.invalid.password"),
        @Deprecated
        ADD_ITEM_NOT_FOUND_USER(HttpStatus.NOT_FOUND , "WA_U0003" , "waim.api.user.error.user_not_found"),




        USER_NAME_EMPTY(HttpStatus.BAD_REQUEST , "WA_U0004" , "waim.api.user.error.empty.user_name"),
        USER_NAME_INVALID(HttpStatus.BAD_REQUEST , "WA_U0007" , "waim.api.user.error.invalid.user_name"),
        USER_NAME_DUPLICATED(HttpStatus.BAD_REQUEST , "WA_UE_00010" , "waim.api.user.error.duplicate.user_name"),

        USER_ID_EMPTY(HttpStatus.BAD_REQUEST , "WA_U0005" , "waim.api.user.error.empty.user_id"),
        USER_ID_INVALID(HttpStatus.BAD_REQUEST , "WA_U0008" , "waim.api.user.error.invalid.user_id"),
        USER_ID_DUPLICATED(HttpStatus.BAD_REQUEST , "WA_UE_00009" , "waim.api.user.error.duplicate.user_id"),

        USER_PASSWORD_EMPTY(HttpStatus.BAD_REQUEST , "WA_U0006" , "waim.api.user.error.empty.user_password"),
        USER_PASSWORD_INVALID(HttpStatus.BAD_REQUEST , "WA_U0009" , "waim.api.user.error.invalid.user_password"),

        USER_EMAIL_EMPTY(HttpStatus.BAD_REQUEST , "WA_UE_00011" , "waim.api.user.error.empty.user_email"),
        USER_EMAIL_INVALID(HttpStatus.BAD_REQUEST , "WA_UE_00013", "waim.api.user.error.invalid.user_email"),
        USER_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST , "WA_UE_00012" , "waim.api.user.error.duplicate.user_email"),

        USER_UID_EMPTY(HttpStatus.BAD_REQUEST , "WA_UEV_00004" , "waim.api.user.error.empty.user_uid"),



        ;

        private final HttpStatus httpStatus;
        private final String code;
        private final String message;
    }



}
