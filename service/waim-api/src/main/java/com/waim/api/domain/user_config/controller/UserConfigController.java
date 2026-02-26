package com.waim.api.domain.user_config.controller;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.user_config.model.request.SetUserConfigRequest;
import com.waim.core.common.util.jwt.model.JwtUserDetail;
import com.waim.core.domain.user.model.dto.UserConfig;
import com.waim.core.domain.user.service.UserConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/config")
@RequiredArgsConstructor
@Tag(name = "USER CONFIG" , description = "사용자 설정 관리")
public class UserConfigController {

    private final UserConfigService userConfigService;


    @GetMapping()
    @Operation(
            summary = "사용자 설정 조회",
            description = "로그인한 사용자 설정 조회"
    )
    public ResponseEntity<?> getUserConfig(
            @AuthenticationPrincipal JwtUserDetail userDetail
    ){
        List<UserConfig> userConfigList = userConfigService.getUserConfig(userDetail.getUserUid());
        return ResponseEntity.ok().body(
                BaseResponse.Success.builder()
                        .result(userConfigList)
                        .build()
        );
    }


    @PutMapping()
    @Operation(
            summary = "사용자 설정 등록/수정",
            description = "로그인한 사용자 설정 등록/수정"
    )
    public ResponseEntity<?> setUserConfig(
            @AuthenticationPrincipal JwtUserDetail userDetail,
            @RequestBody SetUserConfigRequest userConfig
    ){
        userConfigService.setUserConfig(userDetail.getUserUid(), userConfig.getKey() , userConfig.getValue());

        return ResponseEntity.ok().body(
                BaseResponse.Success.builder().build()
        );
    }
}
