package com.waim.core.domain.auth.model.error;

import com.waim.core.common.model.error.WAIMErrorCode;
import com.waim.core.common.model.error.WAIMException;

@Deprecated
public class WAIMAuthModuleException extends WAIMException {
    public WAIMAuthModuleException(WAIMErrorCode errorCode) {
        super(errorCode);
    }


    public static WAIMAuthModuleException INVALID_TOKEN = new WAIMAuthModuleException(AuthErrorCode.Common.TOKEN_INVALID);
}
