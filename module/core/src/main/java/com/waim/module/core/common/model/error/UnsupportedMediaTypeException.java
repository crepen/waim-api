package com.waim.module.core.common.model.error;

import org.springframework.http.HttpStatus;

public class UnsupportedMediaTypeException extends CommonException {
    public UnsupportedMediaTypeException() {
        super(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "WRE_0003",
                "runtime.error.wre_0003.unsupported_media_type",
                "Unsupported Media Type."
        );
    }
}
