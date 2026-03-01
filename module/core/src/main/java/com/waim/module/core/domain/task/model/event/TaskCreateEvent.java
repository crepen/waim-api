package com.waim.module.core.domain.task.model.event;

import com.waim.module.core.domain.task.model.entity.TaskEntity;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.data.common.security.SecurityUserDetail;

import java.util.Optional;

public record TaskCreateEvent(
        TaskEntity user,
        String clientIp,
        Optional<SecurityUserDetail> loginUserDetail
) {}
