package com.waim.core.domain.log.repository.listener;


import com.waim.core.domain.log.model.UserRoleLogState;
import com.waim.core.domain.user.model.entity.UserRoleEntity;
import com.waim.core.common.util.network.NetworkUtil;
import com.waim.core.common.util.security.SecurityUtil;
import com.waim.core.domain.log.model.event.UserRoleLogEvent;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserRoleEntityListener {

    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        UserRoleEntityListener.eventPublisher = eventPublisher;
    }

    @PostPersist
    public void onPostPersist(UserRoleEntity roleEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(new UserRoleLogEvent(
                roleEntity.getUser().getUid(),
                roleEntity.getRole(),
                UserRoleLogState.INSERT,
                clientIp,
                requestUserUid
        ));
    }

    @PostRemove
    public void onPostRemove(UserRoleEntity roleEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(new UserRoleLogEvent(
                roleEntity.getUser().getUid(),
                roleEntity.getRole(),
                UserRoleLogState.REMOVE,
                clientIp,
                requestUserUid
        ));
    }

}