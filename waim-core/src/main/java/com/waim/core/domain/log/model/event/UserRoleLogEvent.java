package com.waim.core.domain.log.model.event;

import com.waim.core.domain.log.model.UserRoleLogState;

public record UserRoleLogEvent(
        String userUid,
        String role,
        UserRoleLogState state,
        String ip,
        String actUserUid
) {}