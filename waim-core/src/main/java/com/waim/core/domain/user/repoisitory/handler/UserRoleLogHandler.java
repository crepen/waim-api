package com.waim.core.domain.user.repoisitory.handler;

import com.waim.core.domain.log.model.entity.UserRoleLogEntity;
import com.waim.core.domain.log.model.event.UserRoleLogEvent;
import com.waim.core.domain.log.repository.UserRoleLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserRoleLogHandler {
    private final UserRoleLogRepository logRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 별도 트랜잭션으로 저장
    public void handleUserRoleLog(UserRoleLogEvent event) {
        UserRoleLogEntity roleLog = UserRoleLogEntity.builder()
                .userUid(event.userUid())
                .role(event.role())
                .state(event.state())
                .ip(event.ip())
                .actUserUid(event.actUserUid())
                .build();
        logRepository.save(roleLog);
    }
}
