package com.waim.module.core.domain.user.listener;

import com.waim.module.core.domain.user.model.event.UserAttributeUpdateEvent;
import com.waim.module.core.domain.user.model.event.UserCreateEvent;
import com.waim.module.core.domain.user.service.UserLogService;
import com.waim.module.data.common.security.SecurityUserDetail;
import com.waim.module.data.domain.user.log.UserLogAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserLogEventListener {


    private final UserLogService userLogService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addUserCreateLog(UserCreateEvent evt){
        userLogService.addLog(
                evt.user(),
                UserLogAction.CREATE,
                evt.clientIp(),
                evt.loginUserDetail().map(SecurityUserDetail::getUniqueId).orElse("")
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addUserAttrCreateLog(UserAttributeUpdateEvent evt){
        userLogService.addAttrLog(
                evt.userAttr(),
                UserLogAction.UPDATE_ATTR,
                evt.clientIp(),
                evt.loginUserDetail().map(SecurityUserDetail::getUniqueId).orElse("")
        );
    }
}
