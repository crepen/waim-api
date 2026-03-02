package com.waim.module.core.common.model.error;

import org.springframework.http.HttpStatus;

public class UncategorizedException extends CommonException {
    public UncategorizedException() {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "WRE_0002",
                "runtime.error.wre_0002.uncategorized_exception",
                "Unknown Error."
        );
    }
}
