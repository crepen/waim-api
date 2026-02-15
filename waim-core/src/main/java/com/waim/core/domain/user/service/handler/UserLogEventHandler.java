package com.waim.core.domain.user.service.handler;


import com.waim.core.domain.user.model.entity.UserLogEntity;
import com.waim.core.domain.user.model.entity.UserRoleLogEntity;
import com.waim.core.domain.user.repoisitory.UserLogRepository;
import com.waim.core.domain.user.repoisitory.UserRoleLogRepository;
import com.waim.core.domain.user.model.event.CommonUserEvent;
import com.waim.core.domain.user.model.event.CommonUserRoleEvent;
import com.waim.core.domain.user.repoisitory.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserLogEventHandler {

    private final UserRoleLogRepository logRepository;
    private final UserLogRepository userLogRepository;


    /**
     * User CRUD Event
     *
     * @param event User Event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserLogEvent(CommonUserEvent event){
        log.info(
                "[UserLogEventHandler.handleUserLogEvent] {} User Entity. -> {}/{}" ,
                event.action() ,
                event.requestClientIp(),
                event.requestUserUid()
        );

        userLogRepository.save(
                UserLogEntity.builder()
                        .userUid(event.actionUserEntity().getUid())
                        .logState(event.action())
                        .ip(event.requestClientIp())
                        .actUserUid(event.requestUserUid())
                        .build()
        );
    }

    /**
     * User Role CRUD Event
     *
     * @param event User Role Event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserRoleLogEvent(CommonUserRoleEvent event){
        log.info(
                "[UserLogEventHandler.handleUserRoleLogEvent] {} User Role Entity. -> {}/{}" ,
                event.action() ,
                event.requestClientIp(),
                event.requestUserUid()
        );

        logRepository.save(
                UserRoleLogEntity.builder()
                        .userUid(event.actionUserRoleEntity().getUser().getUid())
                        .role(event.actionUserRoleEntity().getRole())
                        .state(event.action())
                        .ip(event.requestClientIp())
                        .actUserUid(event.requestUserUid())
                        .build()
        );
    }
}
