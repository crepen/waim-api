package com.waim.module.core.domain.project.model.event;

import com.waim.module.core.domain.project.model.entity.ProjectEntity;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.data.common.security.SecurityUserDetail;

import java.util.Optional;

public record ProjectCreateEvent(
        ProjectEntity user,
        String clientIp,
        Optional<SecurityUserDetail> loginUserDetail
) {}
