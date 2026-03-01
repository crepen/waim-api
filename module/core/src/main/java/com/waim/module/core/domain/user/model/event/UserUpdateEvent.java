package com.waim.module.core.domain.user.model.event;

import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.data.common.security.SecurityUserDetail;

import java.util.Optional;

public record UserUpdateEvent(
        UserEntity user,
        String clientIp,
        Optional<SecurityUserDetail> loginUserDetail
) {}
