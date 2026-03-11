package com.waim.module.core.domain.task.model.error;

import com.waim.module.core.common.model.error.ServerException;

public class TaskServerException extends ServerException {
    public TaskServerException(int statusCode, String errorCode, String localeMessageCode, String message) {
        super("TASK", statusCode, errorCode, localeMessageCode, message);
    }
}
