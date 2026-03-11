package com.waim.module.core.domain.task.model.error;

import org.springframework.http.HttpStatus;

public class TaskInvalidTypeException extends TaskServerException {
    public TaskInvalidTypeException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_TSK_0003",
                "waim.domain.task.error.wse_tsk_0003.invalid_type",
                "Invalid task type."
        );
    }
}
