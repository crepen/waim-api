package com.waim.core.domain.user.model.event;

import com.waim.core.domain.user.model.dto.enumable.UserEventAction;
import com.waim.core.domain.user.model.entity.UserEntity;

public record CommonUserEvent(
        UserEntity actionUserEntity,
        UserEventAction action,
        String requestClientIp,
        String requestUserUid
) {}
