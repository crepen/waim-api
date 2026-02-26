package com.waim.core.domain.auth.model.error;

import com.waim.core.common.model.error.WAIMErrorCode;

@Deprecated
public class WAIMRefreshTokenValidateException extends WAIMAuthModuleException  {
    public WAIMRefreshTokenValidateException(WAIMErrorCode errorCode) {
        super(errorCode);
    }

    public static WAIMRefreshTokenValidateException INVALID_REFRESH_TOKEN = new WAIMRefreshTokenValidateException(AuthErrorCode.Common.REFRESH_TOKEN_INVALID_TOKEN);
    public static WAIMRefreshTokenValidateException EXPIRED_REFRESH_TOKEN = new WAIMRefreshTokenValidateException(AuthErrorCode.Common.REFRESH_TOKEN_EXPIRE_TOKEN);
    public static WAIMRefreshTokenValidateException UNMATCHED_TOKEN_TYPE = new WAIMRefreshTokenValidateException(AuthErrorCode.Common.REFRESH_TOKEN_UNMATCHED_TYPE);
    public static WAIMRefreshTokenValidateException NOT_FOUND_USER = new WAIMRefreshTokenValidateException(AuthErrorCode.Validate.LOGIN_INVALID_NOT_FOUND_USER);
}
