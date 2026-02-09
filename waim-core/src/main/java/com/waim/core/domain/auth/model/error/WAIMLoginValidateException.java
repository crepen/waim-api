package com.waim.core.domain.auth.model.error;

import com.waim.core.common.model.error.WAIMErrorCode;

@Deprecated
public class WAIMLoginValidateException extends WAIMAuthModuleException {
    public WAIMLoginValidateException(WAIMErrorCode errorCode) {
        super(errorCode);
    }


    public static WAIMLoginValidateException INVALID_ID = new WAIMLoginValidateException(AuthErrorCode.Validate.LOGIN_INVALID_ID);
    public static WAIMLoginValidateException INVALID_PASSWORD = new WAIMLoginValidateException(AuthErrorCode.Validate.LOGIN_INVALID_PASSWORD);
    public static WAIMLoginValidateException NOT_FOUND_USER = new WAIMLoginValidateException(AuthErrorCode.Validate.LOGIN_INVALID_NOT_FOUND_USER);
    public static WAIMLoginValidateException PASSWORD_NOT_MATCH = new WAIMLoginValidateException(AuthErrorCode.Validate.LOGIN_INVALID_PASSWORD_NOT_MATCH);
    public static WAIMLoginValidateException UNMATCHED_GRANT_TYPE = new WAIMLoginValidateException(AuthErrorCode.Validate.LOGIN_INVALID_UNMATCHED_GRANT_TYPE);
}
