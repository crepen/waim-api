package com.waim.module.core.domain.project.listener;

import com.waim.module.core.domain.auth.util.SecurityUtil;
import com.waim.module.core.domain.project.model.entity.ProjectEntity;
import com.waim.module.core.domain.project.model.event.ProjectCreateEvent;
import com.waim.module.core.domain.project.model.event.ProjectDeleteEvent;
import com.waim.module.core.domain.project.model.event.ProjectUpdateEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectDataListener {
    private static ApplicationEventPublisher eventPublisher;

    @PreUpdate
    public void preUpdate(ProjectEntity project) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        Optional<SecurityUserDetail> loginUserDetail = SecurityUtil.getUserData();

        eventPublisher.publishEvent(new ProjectUpdateEvent(project, clientIp, loginUserDetail));
    }

    @PreRemove
    public void preRemove(ProjectEntity project) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        Optional<SecurityUserDetail> loginUserDetail = SecurityUtil.getUserData();

        eventPublisher.publishEvent(new ProjectDeleteEvent(project, clientIp, loginUserDetail));
    }

    @PrePersist
    public void prePersist(ProjectEntity project) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        Optional<SecurityUserDetail> loginUserDetail = SecurityUtil.getUserData();

        eventPublisher.publishEvent(new ProjectCreateEvent(project, clientIp, loginUserDetail));
    }
}
