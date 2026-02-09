package com.waim.api.domain.auth.model.error;

import com.waim.core.common.model.error.WAIMErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements WAIMErrorCode {

    UNAUTHORIZED_WA_A0001(HttpStatus.UNAUTHORIZED , "WA_A0001" , "waim.api.auth.error.unauthorized"),
    ACCESS_DENIED_WA_A0002(HttpStatus.FORBIDDEN , "WA_A0002" , "waim.api.auth.error.forbidden"),
    OTP_DENIED_WA_A0003(HttpStatus.UNAUTHORIZED , "WA_A0003" , "waim.api.auth.error.otp_unauthorized"),


    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
