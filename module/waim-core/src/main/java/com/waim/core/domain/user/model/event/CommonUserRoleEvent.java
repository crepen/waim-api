package com.waim.core.domain.user.model.event;

import com.waim.core.domain.user.model.dto.enumable.UserEventAction;
import com.waim.core.domain.user.model.dto.enumable.UserRoleEventAction;
import com.waim.core.domain.user.model.entity.UserEntity;
import com.waim.core.domain.user.model.entity.UserRoleEntity;

public record CommonUserRoleEvent(
        UserRoleEntity actionUserRoleEntity,
        UserRoleEventAction action,
        String requestClientIp,
        String requestUserUid
) {}
