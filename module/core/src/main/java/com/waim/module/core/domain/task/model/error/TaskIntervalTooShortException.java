package com.waim.module.core.domain.task.model.error;

import org.springframework.http.HttpStatus;

public class TaskIntervalTooShortException extends TaskServerException {
    public TaskIntervalTooShortException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "WSE_TSK_0002",
                "waim.domain.task.error.wse_tsk_0002.interval_too_short",
                "Task interval must be at least 10 seconds."
        );
    }
}
