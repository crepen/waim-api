package com.waim.module.core.common.model.error;

import org.springframework.http.HttpStatus;

public class DataBodyUnboundException extends CommonException {
    public DataBodyUnboundException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WRE_0001",
                "runtime.error.wre_0001.message_not_readable",
                "Data body not readable."
        );
    }
}
