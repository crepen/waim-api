package com.waim.module.core.domain.user.listener;


import com.waim.module.core.domain.auth.util.SecurityUtil;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.core.domain.user.model.event.UserCreateEvent;
import com.waim.module.core.domain.user.model.event.UserDeleteEvent;
import com.waim.module.core.domain.user.model.event.UserUpdateEvent;
import com.waim.module.data.common.security.SecurityUserDetail;
import com.waim.module.util.network.NetworkUtil;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDataListener {
    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void init(ApplicationEventPublisher eventPublisher) {
        UserDataListener.eventPublisher = eventPublisher;
    }

    @PreUpdate
    public void preUpdate(UserEntity user) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        Optional<SecurityUserDetail> loginUserDetail = SecurityUtil.getUserData();

        eventPublisher.publishEvent(new UserUpdateEvent(user, clientIp, loginUserDetail));
    }

    @PreRemove
    public void preRemove(UserEntity user) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        Optional<SecurityUserDetail> loginUserDetail = SecurityUtil.getUserData();

        eventPublisher.publishEvent(new UserDeleteEvent(user, clientIp, loginUserDetail));
    }

    @PrePersist
    public void prePersist(UserEntity user) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        Optional<SecurityUserDetail> loginUserDetail = SecurityUtil.getUserData();

        eventPublisher.publishEvent(new UserCreateEvent(user, clientIp, loginUserDetail));
    }
}
