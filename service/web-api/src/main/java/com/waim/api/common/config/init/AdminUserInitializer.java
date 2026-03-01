package com.waim.api.common.config.init;

import com.waim.module.core.domain.user.service.UserService;
import com.waim.module.core.system.config.model.entity.SystemConfigEntity;
import com.waim.module.core.system.config.service.SystemConfigService;
import com.waim.module.data.domain.user.AddUserProp;
import com.waim.module.data.domain.user.UserRole;
import com.waim.module.data.domain.user.UserStatus;
import com.waim.module.data.system.config.SystemConfigKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(102)
public class AdminUserInitializer implements ApplicationRunner {
    private final SystemConfigService systemConfigService;
    private final UserService userService;

    @Override
    @Transactional
    @NullMarked
    public void run( ApplicationArguments args) {

        Optional<SystemConfigEntity> initConfig = systemConfigService.getConfig(SystemConfigKey.INIT_SYSTEM.name());

        if (initConfig.isEmpty() || initConfig.get().getConfigValue().equals("F")) {
            // SYSTEM INIT

            try {
                // Add Admin account

                userService.addUser(
                        AddUserProp.builder()
                                .id("root")
                                .password("Waim0001!")
                                .email("waim-root@crepen.cloud")
                                .name("Administrator")
                                .role(UserRole.ADMIN)
                                .status(UserStatus.ACTIVE)
                                .build()
                );

                log.info("Add Admin Account.");
            } catch (Exception ex) {
                log.error("Add Admin Account failed.");
                throw ex;
            }

        }
    }
}
