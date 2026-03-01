package com.waim.module.core.system.config.service;

import com.waim.module.core.system.config.model.entity.SystemConfigEntity;
import com.waim.module.core.system.config.model.error.SystemConfigEmptyKeyException;
import com.waim.module.core.system.config.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SystemConfigService {
    private final SystemConfigRepository systemConfigRepository;

    public Optional<SystemConfigEntity> getConfig(String configKey) {
        Specification<SystemConfigEntity> spec = (
                (root, query, cb) -> {
                    query.distinct(true);

                    return cb.equal(root.get("configKey"), configKey);
                }
        );

        return systemConfigRepository.findOne(spec);
    }

    public List<SystemConfigEntity> getConfigs(List<String> configKeys) {
        Specification<SystemConfigEntity> spec = (
                (root, query, cb) -> {
                    query.distinct(true);

                    return root.get("configKey").in(configKeys);
                }
        );

        return systemConfigRepository.findAll(spec);
    }

    public List<SystemConfigEntity> getConfigs(String... configKeys) {
        return getConfigs(Arrays.stream(configKeys).toList());
    }

    public void setConfig(String configKey, String configValue) {

        if (!StringUtils.hasText(configKey)) {
            throw new SystemConfigEmptyKeyException();
        }

        if (!StringUtils.hasText(configValue)) {
            throw new SystemConfigEmptyKeyException();
        }

        Optional<SystemConfigEntity> matchConfig = getConfig(configKey);

        SystemConfigEntity saveEntity = matchConfig.orElseGet(
                () -> SystemConfigEntity.builder()
                        .configKey(configKey)
                        .build()
        );

        saveEntity.setConfigValue(configValue);

        systemConfigRepository.save(saveEntity);
    }
}
