package com.waim.core.common.model.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serial;
import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Deprecated
public class WAIMException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    private WAIMErrorCode errorCode;
    private HttpStatus httpStatus;
    private List<String> messageArgs;

    public WAIMException(final WAIMErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();


    }

    public WAIMException(final WAIMErrorCode errorCode, final String ...messageArgs) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
        this.messageArgs = Arrays.stream(messageArgs).toList();
    }



    public static WAIMException INTERNAL_SERVER_ERROR = new WAIMException(CommonErrorCode.INTERNAL_SERVER_ERROR);
}
