package com.waim.module.storage.domain.user.listener;

import com.waim.module.storage.domain.user.entity.UserEntity;

public interface UserLifecycleCallback {
    void onUserCreated(UserEntity user);
    void onUserDeleted(UserEntity user);
    void onUserUpdated(UserEntity user);
}
