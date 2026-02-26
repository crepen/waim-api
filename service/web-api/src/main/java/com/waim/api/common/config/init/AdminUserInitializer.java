package com.waim.api.common.config.init;

import com.waim.core.domain.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(102)
public class AdminUserInitializer implements ApplicationRunner {
    private final AdminUserService AdminUserService;

    @Override
    @Transactional
    @NullMarked
    public void run( ApplicationArguments args) {
        // 1. 관리자 존재 여부 확인
        if (!AdminUserService.isExistAdminRoleUser()) {
            AdminUserService.addAdminUserAccount();
            log.info("관리자 계정 생성 완료 ");
        } else {
            log.info("관리자 계정이 이미 존재합니다.");
        }
    }
}
