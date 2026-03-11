package com.waim.api.domain.configure.controller;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.configure.model.request.UpdateSmtpGlobalConfigRequest;
import com.waim.api.domain.configure.model.request.UpdateGlobalConfigRequest;
import com.waim.api.domain.configure.model.response.ConfigResponse;
import com.waim.api.domain.configure.service.SmtpGlobalConfigService;
import com.waim.module.core.system.config.model.entity.SystemConfigEntity;
import com.waim.module.core.system.config.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/config/global")
public class GlobalConfigureController {

    private final SystemConfigService systemConfigService;
        private final SmtpGlobalConfigService smtpGlobalConfigService;

    @GetMapping("")
    @Operation(
            summary = "System config 검색",
            description = "System config 검색"
    )
    public ResponseEntity<?> getGlobalConfig(
            @RequestParam(name = "keys") List<String> configKey
    ) {
        // TODO : UPDATE - Search system config API : reqParam -> body 변경
        List<SystemConfigEntity> configList = systemConfigService.getConfigs(configKey);

        List<ConfigResponse> caseRes = configList.stream().map(
                x-> ConfigResponse.builder()
                        .key(x.getConfigKey())
                        .value(x.getConfigValue())
                        .build()
        ).toList();

        return ResponseEntity.ok().body(
                BaseResponse.Success.builder()
                        .result(caseRes)
                        .build()
        );
    }


    @PostMapping("")
    @Operation(
            summary = "System config 설정",
            description = "System config 생성/수정"
    )
    public ResponseEntity<?> updateGlobalConfig(
            @RequestBody UpdateGlobalConfigRequest reqBody
    ){
        systemConfigService.setConfig(
                reqBody.getKey(),
                reqBody.getValue()
        );

        return ResponseEntity.ok().body(
                BaseResponse.Success.builder()
                        .build()
        );
    }

    @PostMapping("/smtp")
    @Operation(
            summary = "SMTP System config 설정",
            description = "SMTP 연결 테스트 후 설정값을 저장합니다. 테스트 실패 시 저장하지 않습니다."
    )
    public ResponseEntity<?> updateSmtpGlobalConfig(
            @RequestBody UpdateSmtpGlobalConfigRequest reqBody
    ) {
        smtpGlobalConfigService.validateAndSave(reqBody);

        return ResponseEntity.ok().body(
                BaseResponse.Success.builder().build()
        );
    }

    @PostMapping("/smtp/test")
    @Operation(
            summary = "SMTP 연결 테스트",
            description = "저장 없이 SMTP 연결 테스트만 수행합니다."
    )
    public ResponseEntity<?> testSmtpGlobalConfig(
            @RequestBody UpdateSmtpGlobalConfigRequest reqBody
    ) {
        smtpGlobalConfigService.validateOnly(reqBody);

        return ResponseEntity.ok().body(
                BaseResponse.Success.builder().build()
        );
    }

    @DeleteMapping("")
    @Operation(
            summary = "System config 삭제",
            description = "System config 삭제"
    )
    public ResponseEntity<?> deleteGlobalConfig(

    ){
        // TODO : UPDATE - Delete system config API
        return ResponseEntity.ok().body(
                BaseResponse.Success.builder()
                        .build()
        );
    }
}
