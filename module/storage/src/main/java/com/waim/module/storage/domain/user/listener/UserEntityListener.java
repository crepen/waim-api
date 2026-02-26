package com.waim.module.storage.domain.user.listener;

import com.waim.module.storage.domain.user.entity.UserEntity;
import com.waim.module.storage.domain.user.listener.event.UserCreateEvent;
import com.waim.module.storage.domain.user.listener.event.UserDeleteEvent;
import com.waim.module.storage.domain.user.listener.event.UserUpdateEvent;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEntityListener {
    private static UserLifecycleCallback callback;
    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setCallback(UserLifecycleCallback callback) {
        UserEntityListener.callback = callback;
    }

    @PreUpdate
    public void preUpdate(UserEntity user) {
        if (callback != null) callback.onUserUpdated(user);

        eventPublisher.publishEvent(new UserUpdateEvent(user));
    }

    @PreRemove
    public void preRemove(UserEntity user) {
        if (callback != null) callback.onUserDeleted(user);

        eventPublisher.publishEvent(new UserDeleteEvent(user));
    }

    @PrePersist
    public void prePersist(UserEntity user) {
        if (callback != null) callback.onUserCreated(user);

        eventPublisher.publishEvent(new UserCreateEvent(user));
    }


}
