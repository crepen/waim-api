package com.waim.core.domain.project.service;

import com.waim.core.domain.project.model.event.ProjectRemoveEvent;
import com.waim.core.domain.project.model.event.ProjectUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Project Entity Event Handler
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectEventHandler {


    /**
     * Project Entity Remove 시 Log 삽입
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleRemoveProjectLogEvent(ProjectRemoveEvent event){
        // Project Remove Log
        log.info("[handleRemoveProjectLogEvent] Remove Project Entity. -> Log Insert");
    }

    /**
     * Project Entity Update 시 Log 삽입
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUpdateProjectLogEvent(ProjectUpdateEvent event){
        // Project Update Log
        log.info("[handleUpdateProjectLogEvent] Update Project Entity. -> Log Insert");
    }
}
