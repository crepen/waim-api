package com.waim.core.domain.user.service;

import com.waim.core.common.model.error.WAIMException;
import com.waim.core.domain.user.model.dto.UserConfig;
import com.waim.core.domain.user.model.entity.UserConfigEntity;
import com.waim.core.domain.user.model.error.UserErrorCode;
import com.waim.core.domain.user.repoisitory.UserConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserConfigService {
    private final UserConfigRepository userConfigRepository;


    public List<UserConfig> getUserConfig(String uid){
        if(!StringUtils.hasText(uid)){
            throw new WAIMException(UserErrorCode.Validation.USER_UID_EMPTY);
        }

        var findConfigList = userConfigRepository.findAllByUserUid(uid);

        return findConfigList.stream().map(x->
                UserConfig.builder()
                        .key(x.getConfigKey())
                        .value(x.getConfigValue())
                        .build()
        ).toList();
    }

    public void setUserConfig(String uid, String key , String value){
        if(!StringUtils.hasText(key) || !StringUtils.hasText(value)){
            return;
        }

        var matchConfig = userConfigRepository.findByUserUidAndConfigKey(uid , key);

        if(matchConfig.isPresent()){
            matchConfig.get().setConfigValue(value);
            userConfigRepository.save(matchConfig.get());

        }
        else{
            userConfigRepository.save(
                    UserConfigEntity.builder()
                            .userUid(uid)
                            .configKey(key)
                            .configValue(value)
                            .build()
            );
        }
    }
}
