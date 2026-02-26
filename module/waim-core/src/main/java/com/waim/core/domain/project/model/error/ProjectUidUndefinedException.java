package com.waim.core.domain.project.model.error;

import org.springframework.http.HttpStatus;

public class ProjectUidUndefinedException extends PlatformProjectException {
    private static final String errorMessage = "waim.api.project.common.error.uid_empty";
    private static final Integer errorStatusCode = HttpStatus.NOT_FOUND.value();
    private static final String errorCode = "ER-SY-PJT-0002";

    public ProjectUidUndefinedException() {
        super(errorStatusCode, errorCode, errorMessage);
    }

    public ProjectUidUndefinedException(Throwable throwable) {
        super(errorStatusCode, errorCode, errorMessage, throwable);
    }
}
