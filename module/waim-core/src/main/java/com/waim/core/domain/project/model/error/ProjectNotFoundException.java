package com.waim.core.domain.project.model.error;

import org.springframework.http.HttpStatus;

public class ProjectNotFoundException extends PlatformProjectException{

    private static final String errorMessage = "waim.api.project.common.error.project_not_found";
    private static final Integer errorStatusCode = HttpStatus.NOT_FOUND.value();
    private static final String errorCode = "ER-SY-PJT-0001";

    public ProjectNotFoundException() {
        super(
                errorStatusCode,
                errorCode,
                errorMessage
        );
    }

    public ProjectNotFoundException(Throwable throwable) {
        super(
                errorStatusCode,
                errorCode,
                errorMessage,
                throwable
        );
    }
}
