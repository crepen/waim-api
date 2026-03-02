package com.waim.api.domain.user.controller;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.user.model.request.AddUserRequest;
import com.waim.api.domain.user.model.request.RemoveUserRequest;
import com.waim.api.domain.user.model.request.UpdateUserRequest;
import com.waim.module.core.domain.user.service.UserService;
import com.waim.module.data.common.security.SecurityUserDetail;
import com.waim.module.data.domain.user.*;
import com.waim.module.data.domain.user.prop.AddUserProp;
import com.waim.module.data.domain.user.prop.RemoveUserProp;
import com.waim.module.data.domain.user.prop.UpdateUserProp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "USER" , description = "유저 관리 API")
@Slf4j
public class UserController {

    private final UserService userService;

    @PutMapping
    @Operation(summary = "사용자 가입 신청" , description = "가입 신청")
    public ResponseEntity<?> addGeneralUser(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @RequestBody AddUserRequest reqBody
    ) {
        userService.addUser(
                AddUserProp.builder()
                        .id(reqBody.getUserId())
                        .name(reqBody.getUserName())
                        .email(reqBody.getEmail())
                        .password(reqBody.getPassword())
                        .role(UserRole.GENERAL)
                        .status(UserStatus.INACTIVE)
                        .build()
        );

        log.info("REGISTER USER : {}" , reqBody.getUserId());



        return ResponseEntity.ok()
                .body(
                        BaseResponse.Success.builder()
                                .build()
                );
    }

    @PostMapping
    @Operation(summary = "사용자 정보 수정")
    public ResponseEntity<?> updateUser(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @RequestBody UpdateUserRequest reqBody
    ) {

        userService.updateUser(
                UpdateUserProp.builder()
                        .userUid(userDetail.getUniqueId())
                        .name(reqBody.getName())
                        .email(reqBody.getEmail())
                        .password(reqBody.getPassword())
                        .role(reqBody.getRole())
                        .status(reqBody.getStatus())
                        .config(reqBody.getConfig())
                        .build()
        );

        return ResponseEntity.ok().body(
                BaseResponse.Success.builder()
                        .build()
        );
    }


    @DeleteMapping
    @Operation(summary = "회원 탈퇴" , description = "로그인 사용자 회원 탈퇴")
    public ResponseEntity<?> removeLoginUser(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @RequestBody RemoveUserRequest reqBody
    ){

        userService.removeUser(
                RemoveUserProp.builder()
                        .userUid(reqBody.getUserUid())
                        .actionUserUid(userDetail.getUniqueId())
                        .isAdmin(false)
                        .build()
        );

        return ResponseEntity.ok()
                .body(
                        BaseResponse.Success.builder().build()
                );
    }
}
