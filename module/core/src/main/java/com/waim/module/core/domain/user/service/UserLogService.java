package com.waim.module.core.domain.user.service;

import com.waim.module.core.domain.user.model.entity.UserAttributeEntity;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.core.domain.user.model.entity.UserLogEntity;
import com.waim.module.core.domain.user.repository.UserLogRepository;
import com.waim.module.data.domain.user.log.UserLogAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserLogService {
    private final UserLogRepository userLogRepository;

    public void addLog(UserEntity user, UserLogAction action, String actionIp , String actionUserUid){
        userLogRepository.save(
                UserLogEntity.builder()
                        .logActionType(action)
                        .user(user)
                        .ip(actionIp)
                        .actUserUid(actionUserUid)
                        .build()
        );
    }

    public void addAttrLog(UserAttributeEntity userAttr, UserLogAction action, String actionIp , String actionUserUid){
        userLogRepository.save(
                UserLogEntity.builder()
                        .logActionType(action)
                        .user(userAttr.getUser())
                        .ip(actionIp)
                        .actUserUid(actionUserUid)
                        .build()
        );
    }
}
