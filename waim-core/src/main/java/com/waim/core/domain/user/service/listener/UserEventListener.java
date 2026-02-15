package com.waim.core.domain.user.service.listener;

import com.waim.core.common.util.network.NetworkUtil;
import com.waim.core.common.util.security.SecurityUtil;
import com.waim.core.domain.user.model.dto.enumable.UserEventAction;
import com.waim.core.domain.user.model.entity.UserEntity;
import com.waim.core.domain.user.model.event.CommonUserEvent;
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
public class UserEventListener {

    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        UserEventListener.eventPublisher = eventPublisher;
    }


    /**
     * Insert User Entity Event
     *
     * @param insertUserEntity
     */
    @PostPersist
    public void onPostPersist(UserEntity insertUserEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(
                new CommonUserEvent(
                        insertUserEntity,
                        UserEventAction.INSERT,
                        clientIp,
                        requestUserUid
                )
        );
    }

    /**
     * Update User Entity Event
     *
     * @param updateUserEntity
     */
    @PostUpdate
    public void onPostUpdate(UserEntity updateUserEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(
                new CommonUserEvent(
                        updateUserEntity,
                        UserEventAction.UPDATE,
                        clientIp,
                        requestUserUid
                )
        );
    }

    /**
     * Delete User Entity Event
     *
     * @param removeUserEntity
     */
    @PostRemove
    public void onPostRemove(UserEntity removeUserEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(
                new CommonUserEvent(
                        removeUserEntity,
                        UserEventAction.DELETE,
                        clientIp,
                        requestUserUid
                )
        );
    }
}
