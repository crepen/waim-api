package com.waim.core.domain.project.model.event;

import com.waim.core.domain.project.model.entity.ProjectEntity;

public record ProjectRemoveEvent (
        ProjectEntity projectEntity,
        String actionIp,
        String actionUserUid
){}
