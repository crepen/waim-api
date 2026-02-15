package com.waim.core.domain.project.service.handler;

import com.waim.core.domain.project.model.event.CommonProjectEvent;
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
     * Project Entity Log
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleProjectLogEvent(CommonProjectEvent event){
        // Project Remove Log
        log.info(
                "[ProjectEventHandler.handleProjectLogEvent] {} Project Entity. -> {}/{}",
                event.action(),
                event.requestClientIp(),
                event.requestActUserUid()
        );
    }



}
