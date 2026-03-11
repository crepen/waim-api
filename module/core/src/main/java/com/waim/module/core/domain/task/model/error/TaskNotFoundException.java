package com.waim.module.core.domain.task.model.error;

import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends TaskServerException {
    public TaskNotFoundException() {
        super(
                HttpStatus.NOT_FOUND.value(),
                "WSE_TSK_0001",
                "waim.domain.task.error.wse_tsk_0001.not_found",
                "Task not found."
        );
    }
}
