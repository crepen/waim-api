package com.waim.core.domain.project.model.event;

import com.waim.core.domain.project.model.dto.enumable.ProjectEventAction;
import com.waim.core.domain.project.model.entity.ProjectEntity;

public record CommonProjectEvent(
    ProjectEntity projectEntity,
    ProjectEventAction action,
    String requestClientIp,
    String requestActUserUid
) {}
