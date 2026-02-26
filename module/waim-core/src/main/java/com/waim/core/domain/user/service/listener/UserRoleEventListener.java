package com.waim.core.domain.user.service.listener;

import com.waim.core.common.util.network.NetworkUtil;
import com.waim.core.common.util.security.SecurityUtil;
import com.waim.core.domain.user.model.dto.enumable.UserRoleEventAction;
import com.waim.core.domain.user.model.entity.UserRoleEntity;
import com.waim.core.domain.user.model.event.CommonUserRoleEvent;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRoleEventListener {

    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        UserRoleEventListener.eventPublisher = eventPublisher;
    }

    @PostPersist
    public void onPostPersist(UserRoleEntity roleEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(new CommonUserRoleEvent(
                roleEntity,
                UserRoleEventAction.INSERT_ROLE,
                clientIp,
                requestUserUid
        ));
    }

    @PostUpdate
    public void onPostUpdate(UserRoleEntity roleEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(new CommonUserRoleEvent(
                roleEntity,
                UserRoleEventAction.UPDATE_ROLE,
                clientIp,
                requestUserUid
        ));
    }

    @PostRemove
    public void onPostRemove(UserRoleEntity roleEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(new CommonUserRoleEvent(
                roleEntity,
                UserRoleEventAction.DELETE_ROLE,
                clientIp,
                requestUserUid
        ));
    }
}
