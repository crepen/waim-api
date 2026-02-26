package com.waim.module.storage.domain.user.listener.event;

import com.waim.module.storage.domain.user.entity.UserEntity;

public record UserDeleteEvent(
        UserEntity user
) {}
