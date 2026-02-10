package com.waim.core.domain.project.model.entity.listener;

import com.waim.core.common.util.network.NetworkUtil;
import com.waim.core.common.util.security.SecurityUtil;
import com.waim.core.domain.log.repository.listener.UserRoleEntityListener;
import com.waim.core.domain.project.model.entity.ProjectEntity;
import com.waim.core.domain.project.model.event.ProjectRemoveEvent;
import com.waim.core.domain.project.model.event.ProjectUpdateEvent;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEntityListener {
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Project Entity update event
     * @param projectEntity
     */
    @PostPersist
    public void onPostPersist(ProjectEntity projectEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(new ProjectUpdateEvent(
                projectEntity,
                clientIp,
                requestUserUid
        ));
    }

    /**
     * Project Entity remove event
     * @param projectEntity
     */
    @PostRemove
    public void onPostRemove(ProjectEntity projectEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(new ProjectRemoveEvent(
                projectEntity,
                clientIp,
                requestUserUid
        ));
    }

}
