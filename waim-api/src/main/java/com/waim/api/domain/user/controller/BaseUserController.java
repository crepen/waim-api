package com.waim.api.domain.user.controller;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.user.model.request.AddUserRequest;
import com.waim.core.common.util.jwt.model.JwtUserDetail;
import com.waim.core.domain.user.model.UserState;
import com.waim.core.domain.user.model.dto.AddUserDTO;
import com.waim.core.domain.user.service.UserService;
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
public class BaseUserController {

    private final UserService userService;

    @PutMapping
    @Operation(summary = "사용자 가입 신청" , description = "가입 신청")
    public ResponseEntity<?> addGeneralUser(
            @AuthenticationPrincipal JwtUserDetail userDetail,
            @RequestBody AddUserRequest reqBody
    ) {
        userService.addUser(
                AddUserDTO.builder()
                        .userName(reqBody.getUserName())
                        .password(reqBody.getPassword())
                        .userId(reqBody.getUserId())
                        .email(reqBody.getEmail())
                        .userState(UserState.PENDING)
                        .build()
        );

        log.info("REGISTER USER : {}" , reqBody.getUserId());



        return ResponseEntity.ok()
                .body(
                        BaseResponse.Success.builder()
                                .build()
                );
    }


    @DeleteMapping
    @Operation(summary = "회원 탈퇴" , description = "로그인 사용자 회원 탈퇴")
    public ResponseEntity<?> removeLoginUser(
            @AuthenticationPrincipal JwtUserDetail userDetail
    ){
        userService.removeUser(userDetail.getUserUid());

        return ResponseEntity.ok()
                .body(
                        BaseResponse.Success.builder().build()
                );
    }
}
