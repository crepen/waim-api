package com.waim.core.domain.project.service.listener;

import com.waim.core.common.util.network.NetworkUtil;
import com.waim.core.common.util.security.SecurityUtil;
import com.waim.core.domain.project.model.entity.ProjectConfigEntity;
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
public class ProjectConfigEventListener {

    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setEventPublisher(final ApplicationEventPublisher eventPublisher) {
        ProjectConfigEventListener.eventPublisher = eventPublisher;
    }

    @PostPersist
    public void onPostPersist(ProjectConfigEntity projectConfigEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();
    }

    @PostUpdate
    public void onPostUpdate(ProjectConfigEntity projectConfigEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();
    }

    @PostRemove
    public void onPostRemove(ProjectConfigEntity projectConfigEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();
    }

}
