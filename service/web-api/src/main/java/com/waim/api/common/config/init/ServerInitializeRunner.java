package com.waim.api.common.config.init;

import com.waim.module.core.domain.user.service.UserService;
import com.waim.module.core.system.config.model.entity.SystemConfigEntity;
import com.waim.module.core.system.config.service.SystemConfigService;
import com.waim.module.data.domain.user.prop.AddUserProp;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(102)
public class ServerInitializeRunner implements ApplicationRunner {
    private static final String DEFAULT_USER_SIGNUP_REQUIRE_ADMIN_APPROVAL = "yes";
    private static final String DEFAULT_USER_SIGNUP_PASSWORD_REQUIREMENT = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$";
    private static final String DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_UPPERCASE = "yes";
    private static final String DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_SYMBOL = "no";
    private static final String DEFAULT_USER_SIGNUP_PASSWORD_ALLOWED_SYMBOLS = "!@#$%^&*()-_=+[]{};:,.?";
    private static final String DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_NUMBER = "no";
    private static final String DEFAULT_USER_SIGNUP_PASSWORD_MIN_LENGTH = "8";
    private static final String DEFAULT_USER_SIGNUP_PASSWORD_MAX_LENGTH = "64";
    private static final String DEFAULT_USER_SIGNUP_ENABLED = "yes";
    private static final String DEFAULT_SMTP_HOST = "smtp.example.com";
    private static final String DEFAULT_SMTP_ENABLED = "no";
    private static final String DEFAULT_SMTP_PORT = "587";
    private static final String DEFAULT_SMTP_USERNAME = "noreply@example.com";
    private static final String DEFAULT_SMTP_PASSWORD = "change-me";
    private static final String DEFAULT_SMTP_FROM_EMAIL = "noreply@example.com";
    private static final String DEFAULT_SMTP_FROM_NAME = "WAIM";
    private static final String DEFAULT_SMTP_AUTH_ENABLED = "yes";
    private static final String DEFAULT_SMTP_STARTTLS_ENABLED = "yes";
    private static final String DEFAULT_SMTP_SSL_ENABLED = "no";

    private final SystemConfigService systemConfigService;
    private final UserService userService;

    @Override
    @Transactional
    @NullMarked
    public void run(ApplicationArguments args) {
        ensureDefaultSignupPolicyConfigs();

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


                //noinspection ConstantValue
                if(!userProtectAttrList.isEmpty()){
                    systemConfigService.setConfig(
                            SystemConfigKey.USER_PROTECT_ATTR_KEY.name(),
                            String.join(",", userProtectAttrList)
                    );
                }


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

    private void ensureDefaultSignupPolicyConfigs() {
        ensureConfigIfMissing(
                SystemConfigKey.USER_SIGNUP_REQUIRE_ADMIN_APPROVAL.name(),
                DEFAULT_USER_SIGNUP_REQUIRE_ADMIN_APPROVAL
        );

        ensureConfigIfMissing(
                SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIREMENT.name(),
                DEFAULT_USER_SIGNUP_PASSWORD_REQUIREMENT
        );

        ensureConfigIfMissing(
            SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIRE_UPPERCASE.name(),
            DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_UPPERCASE
        );

        ensureConfigIfMissing(
            SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIRE_SYMBOL.name(),
            DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_SYMBOL
        );

        ensureConfigIfMissing(
            SystemConfigKey.USER_SIGNUP_PASSWORD_ALLOWED_SYMBOLS.name(),
            DEFAULT_USER_SIGNUP_PASSWORD_ALLOWED_SYMBOLS
        );

        ensureConfigIfMissing(
            SystemConfigKey.USER_SIGNUP_PASSWORD_REQUIRE_NUMBER.name(),
            DEFAULT_USER_SIGNUP_PASSWORD_REQUIRE_NUMBER
        );

        ensureConfigIfMissing(
            SystemConfigKey.USER_SIGNUP_PASSWORD_MIN_LENGTH.name(),
            DEFAULT_USER_SIGNUP_PASSWORD_MIN_LENGTH
        );

        ensureConfigIfMissing(
            SystemConfigKey.USER_SIGNUP_PASSWORD_MAX_LENGTH.name(),
            DEFAULT_USER_SIGNUP_PASSWORD_MAX_LENGTH
        );

        ensureConfigIfMissing(
                SystemConfigKey.USER_SIGNUP_ENABLED.name(),
                DEFAULT_USER_SIGNUP_ENABLED
        );

        ensureConfigIfMissing(
            SystemConfigKey.SMTP_ENABLED.name(),
            DEFAULT_SMTP_ENABLED
        );

        ensureConfigIfMissing(
            SystemConfigKey.SMTP_HOST.name(),
            DEFAULT_SMTP_HOST
        );

        ensureConfigIfMissing(
            SystemConfigKey.SMTP_PORT.name(),
            DEFAULT_SMTP_PORT
        );

        ensureConfigIfMissing(
            SystemConfigKey.SMTP_USERNAME.name(),
            DEFAULT_SMTP_USERNAME
        );

        ensureConfigIfMissing(
            SystemConfigKey.SMTP_PASSWORD.name(),
            DEFAULT_SMTP_PASSWORD
        );

        ensureConfigIfMissing(
            SystemConfigKey.SMTP_FROM_EMAIL.name(),
            DEFAULT_SMTP_FROM_EMAIL
        );

        ensureConfigIfMissing(
            SystemConfigKey.SMTP_FROM_NAME.name(),
            DEFAULT_SMTP_FROM_NAME
        );

        ensureConfigIfMissing(
            SystemConfigKey.SMTP_AUTH_ENABLED.name(),
            DEFAULT_SMTP_AUTH_ENABLED
        );

        ensureConfigIfMissing(
            SystemConfigKey.SMTP_STARTTLS_ENABLED.name(),
            DEFAULT_SMTP_STARTTLS_ENABLED
        );

        ensureConfigIfMissing(
            SystemConfigKey.SMTP_SSL_ENABLED.name(),
            DEFAULT_SMTP_SSL_ENABLED
        );
    }

    private void ensureConfigIfMissing(String key, String defaultValue) {
        if (systemConfigService.getConfig(key).isEmpty()) {
            systemConfigService.setConfig(key, defaultValue);
            log.info("Initialize default system config. key={} value={}", key, defaultValue);
        }
    }
}
