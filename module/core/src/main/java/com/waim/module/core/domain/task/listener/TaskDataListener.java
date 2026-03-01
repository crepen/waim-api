package com.waim.module.core.domain.task.listener;

import com.waim.module.core.domain.auth.util.SecurityUtil;
import com.waim.module.core.domain.task.model.entity.TaskEntity;
import com.waim.module.core.domain.task.model.event.TaskCreateEvent;
import com.waim.module.core.domain.task.model.event.TaskDeleteEvent;
import com.waim.module.core.domain.task.model.event.TaskUpdateEvent;
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
public class TaskDataListener {
    private static ApplicationEventPublisher eventPublisher;

    @PreUpdate
    public void preUpdate(TaskEntity task) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        Optional<SecurityUserDetail> loginUserDetail = SecurityUtil.getUserData();

        eventPublisher.publishEvent(new TaskUpdateEvent(task, clientIp, loginUserDetail));
    }

    @PreRemove
    public void preRemove(TaskEntity task) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        Optional<SecurityUserDetail> loginUserDetail = SecurityUtil.getUserData();

        eventPublisher.publishEvent(new TaskDeleteEvent(task, clientIp, loginUserDetail));
    }

    @PrePersist
    public void prePersist(TaskEntity task) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        Optional<SecurityUserDetail> loginUserDetail = SecurityUtil.getUserData();

        eventPublisher.publishEvent(new TaskCreateEvent(task, clientIp, loginUserDetail));
    }
}
