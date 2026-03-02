package com.waim.module.core.domain.user.model.event;

import com.waim.module.core.domain.user.model.entity.UserAttributeEntity;
import com.waim.module.data.common.security.SecurityUserDetail;

import java.util.Optional;

public record UserAttributeUpdateEvent (
        UserAttributeEntity userAttr,
        String clientIp,
        Optional<SecurityUserDetail> loginUserDetail
){}
