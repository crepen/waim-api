package com.waim.core.common.model.error;

import org.springframework.http.HttpStatus;

import java.util.List;

public interface WAIMErrorCode {
    String name();
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
