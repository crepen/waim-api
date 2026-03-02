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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(102)
public class ServerInitializeRunner implements ApplicationRunner {
    private final SystemConfigService systemConfigService;
    private final UserService userService;

    @Override
    @Transactional
    @NullMarked
    public void run(ApplicationArguments args) {

        Optional<SystemConfigEntity> initConfig = systemConfigService.getConfig(SystemConfigKey.INIT_SYSTEM.name());

        if (initConfig.isEmpty() || initConfig.get().getConfigValue().equals("F")) {
            // SYSTEM INIT

            try {
                // region Add Admin account

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

                // endregion


                // region Update protected User attribute keys

                List<String> userProtectAttrList = List.of(
                        "TEST"
                );

                systemConfigService.setConfig(
                        SystemConfigKey.USER_PROTECT_ATTR_KEY.name(),
                        String.join(",", userProtectAttrList)
                );

                log.info("Update user protect attribute keys");

                // endregion


                // region Complete initialization

                if (initConfig.isEmpty()) {
                    systemConfigService.setConfig(SystemConfigKey.INIT_SYSTEM.name(), "T");
                } else {
                    systemConfigService.setConfig(initConfig.get(), "T");
                }

                // endregion

            } catch (Exception ex) {
                log.error("Server initialization failed.");
                throw ex;
            }

        }
    }
}
