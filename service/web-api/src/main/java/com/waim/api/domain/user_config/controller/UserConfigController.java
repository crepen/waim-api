package com.waim.api.domain.user_config.controller;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.user_config.model.request.SetUserConfigRequest;
import com.waim.api.domain.user_config.model.response.UserConfigResponse;
import com.waim.module.core.domain.user.service.UserAttributeService;
import com.waim.module.data.common.security.SecurityUserDetail;
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

    private final UserAttributeService userAttributeService;


    @GetMapping()
    @Operation(
	    summary = "사용자 설정 조회",
	    description = "로그인한 사용자 설정 조회"
    )
    public ResponseEntity<?> getUserConfig(
	    @AuthenticationPrincipal SecurityUserDetail userDetail
    ){
	List<UserConfigResponse> userConfigList = userAttributeService.getUserConfig(userDetail.getUniqueId())
		.stream()
		.map(x -> UserConfigResponse.builder()
			.key(x.getAttrKey())
			.value(x.getAttrValue())
			.build())
		.toList();

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
	    @AuthenticationPrincipal SecurityUserDetail userDetail,
	    @RequestBody SetUserConfigRequest userConfig
    ){
	userAttributeService.setConfig(userDetail.getUniqueId(), userConfig.getKey(), userConfig.getValue());

	return ResponseEntity.ok().body(
		BaseResponse.Success.builder().build()
	);
    }
}
