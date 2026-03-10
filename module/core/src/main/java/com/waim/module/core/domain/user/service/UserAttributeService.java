package com.waim.module.core.domain.user.service;

import com.waim.module.core.domain.user.model.entity.UserAttributeEntity;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.core.domain.user.repository.UserRepository;
import com.waim.module.core.domain.user.repository.UserAttributeRepository;
import com.waim.module.core.system.config.model.entity.SystemConfigEntity;
import com.waim.module.core.system.config.service.SystemConfigService;
import com.waim.module.data.system.config.SystemConfigKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAttributeService {
    private final UserAttributeRepository attributeRepository;
    private final UserRepository userRepository;
    private final SystemConfigService systemConfigService;

    public List<String> getProtectedKeys(){
        Optional<SystemConfigEntity> protectKeyEntity = systemConfigService
                .getConfig(SystemConfigKey.USER_PROTECT_ATTR_KEY.name());

        return protectKeyEntity
                .map(
                        systemConfigEntity -> Arrays.stream(
                                systemConfigEntity.getConfigValue().split(",")
                        ).toList()
                ).orElseGet(ArrayList::new);
    }

    @Transactional
    public void getConfig(){

    }

    @Transactional
    public List<UserAttributeEntity> getUserConfig(String userUid) {
        if (!StringUtils.hasText(userUid)) {
            return List.of();
        }

        return attributeRepository.findAllByUser_Uid(userUid);
    }

    @Transactional
    public List<UserAttributeEntity> getConfigs(String userUid, String... attrKeys){
        return getConfigs(userUid, Arrays.stream(attrKeys).toList());
    }

    @Transactional
    public List<UserAttributeEntity> getConfigs(String userUid, List<String> attrKeyList){
        if (!StringUtils.hasText(userUid) || attrKeyList == null || attrKeyList.isEmpty()) {
            return List.of();
        }

        return attributeRepository.findAllByUser_UidAndAttrKeyIn(userUid, attrKeyList);
    }

    @Transactional
    public void setConfig(String userUid, String key, String value) {
        if (!StringUtils.hasText(userUid) || !StringUtils.hasText(key) || !StringUtils.hasText(value)) {
            return;
        }

        Optional<UserAttributeEntity> attr = attributeRepository.findByUser_UidAndAttrKey(userUid, key);

        UserAttributeEntity addAttr = attr.orElseGet(
                () -> {
                    UserEntity user = userRepository.findByUid(userUid).orElse(null);

                    if (user == null) {
                        return null;
                    }

                    return UserAttributeEntity.builder()
                            .user(user)
                            .attrKey(key)
                            .build();
                }
        );

        if (addAttr == null) {
            return;
        }

        setConfig(addAttr , value);
    }

    @Transactional
    public void setConfig(UserAttributeEntity entity , String value){
        entity.setAttrValue(StringUtils.hasText(value) ?  value : "");
        attributeRepository.save(entity);
    }

    @Transactional
    public void setConfigs(List<UserAttributeEntity> entities){
        attributeRepository.saveAll(entities);
    }

}
