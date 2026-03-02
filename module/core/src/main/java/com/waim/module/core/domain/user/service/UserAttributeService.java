package com.waim.module.core.domain.user.service;

import com.waim.module.core.domain.user.model.entity.UserAttributeEntity;
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
    public List<UserAttributeEntity> getConfigs(String... attrKeys){
        return getConfigs(Arrays.stream(attrKeys).toList());
    }

    @Transactional
    public List<UserAttributeEntity> getConfigs(List<String> attrKeyList){
        return attributeRepository.findAll(
                (root , query , cb) -> {
                    query.distinct(true);

                    return root.get("attrKey").in(attrKeyList);
                }
        );
    }


    @Transactional
    public void setConfig(String key, String value) {
        Optional<UserAttributeEntity> attr = attributeRepository.findByAttrKey(key);

        UserAttributeEntity addAttr = attr.orElseGet(
                () -> UserAttributeEntity.builder()
                        .attrKey(key)
                        .build()
        );

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
