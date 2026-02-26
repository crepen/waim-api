package com.waim.core.domain.project.service.listener;


import com.waim.core.common.util.network.NetworkUtil;
import com.waim.core.common.util.security.SecurityUtil;
import com.waim.core.domain.project.model.dto.enumable.ProjectEventAction;
import com.waim.core.domain.project.model.entity.ProjectEntity;
import com.waim.core.domain.project.model.event.CommonProjectEvent;
import com.waim.core.domain.user.service.listener.UserEventListener;
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
public class ProjectEventListener {


    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        ProjectEventListener.eventPublisher = eventPublisher;
    }


    @PostPersist
    public void onPostPersist(ProjectEntity projectEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(
                new CommonProjectEvent(
                        projectEntity,
                        ProjectEventAction.INSERT,
                        clientIp,
                        requestUserUid
                )
        );
    }

    @PostUpdate
    public void onPostUpdate(ProjectEntity projectEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(
                new CommonProjectEvent(
                        projectEntity,
                        ProjectEventAction.UPDATE,
                        clientIp,
                        requestUserUid
                )
        );
    }

    @PostRemove
    public void onPostRemove(ProjectEntity projectEntity) {
        String clientIp = NetworkUtil.GetRequestClientIp();
        String requestUserUid = SecurityUtil.getCurrentUserUid();

        eventPublisher.publishEvent(
                new CommonProjectEvent(
                        projectEntity,
                        ProjectEventAction.DELETE,
                        clientIp,
                        requestUserUid
                )
        );
    }
}
