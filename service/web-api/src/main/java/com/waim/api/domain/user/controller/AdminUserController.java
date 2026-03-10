package com.waim.api.domain.user.controller;

import com.waim.api.common.model.CommonPageable;
import com.waim.api.common.model.response.BasePageableResponse;
import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.user.model.request.AddUserRequest;
import com.waim.api.domain.user.model.request.UpdateAdminUserRequest;
import com.waim.api.domain.user.model.response.AdminUserResponse;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.core.domain.user.service.UserService;
import com.waim.module.data.common.security.SecurityUserDetail;
import com.waim.module.data.domain.user.UserRole;
import com.waim.module.data.domain.user.UserStatus;
import com.waim.module.data.domain.user.prop.AddUserProp;
import com.waim.module.data.domain.user.prop.UpdateUserProp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Tag(name = "[ADMIN] USER", description = "[관리자] 유저 관리 API")
@Slf4j
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "사용자 목록 조회", description = "관리자 사용자 목록 조회")
    public ResponseEntity<?> searchUsers(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PageableDefault(size = 20, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable,
                        @RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "status", required = false) String status
    ) {
                Page<UserEntity> resultPage = userService.searchUsers(keyword, status, pageable);

        List<AdminUserResponse> result = resultPage.getContent().stream().map(x ->
                AdminUserResponse.builder()
                        .uid(x.getUid())
                        .userId(x.getUserId())
                        .userName(x.getUserName())
                        .email(x.getUserEmail())
                        .role(x.getUserRole().name())
                        .status(x.getUserStatus().name())
                        .build()
        ).toList();

        return ResponseEntity.ok(
                BasePageableResponse.Success.builder()
                        .result(result)
                        .pageable(CommonPageable.cast(resultPage))
                        .build()
        );
    }

    @PutMapping
    @Operation(summary = "사용자 생성", description = "관리자 사용자 생성")
    public ResponseEntity<?> addUser(
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
                        .status(UserStatus.ACTIVE)
                        .build()
        );

        log.info("[ADMIN] INSERT USER : {}", reqBody.getUserId());

        return ResponseEntity.ok(
                BaseResponse.Success.builder().build()
        );
    }

    @GetMapping("/{uid}")
    @Operation(summary = "사용자 상세 조회", description = "관리자 사용자 상세 조회")
    public ResponseEntity<?> getUser(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable("uid") String uid
    ) {
                var userOpt = userService.findUser(uid);

                if (userOpt.isEmpty()) {
                        userOpt = userService.findUserById(uid);
                }

        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(
                    BaseResponse.Error.builder().message("User not found.").build()
            );
        }

        UserEntity user = userOpt.get();

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .result(AdminUserResponse.builder()
                                .uid(user.getUid())
                                .userId(user.getUserId())
                                .userName(user.getUserName())
                                .email(user.getUserEmail())
                                .role(user.getUserRole().name())
                                .status(user.getUserStatus().name())
                                .build())
                        .build()
        );
    }

    @PostMapping("/{uid}")
    @Operation(summary = "사용자 정보 수정", description = "관리자 사용자 정보 수정")
    public ResponseEntity<?> updateUser(
            @AuthenticationPrincipal SecurityUserDetail userDetail,
            @PathVariable("uid") String uid,
            @RequestBody UpdateAdminUserRequest reqBody
    ) {
        userService.updateUser(
                UpdateUserProp.builder()
                        .userUid(uid)
                        .name(reqBody.getUserName())
                        .email(reqBody.getEmail())
                        .status(reqBody.getStatus())
                        .password(reqBody.getPassword())
                        .role(reqBody.getRole())
                        .build()
        );

        log.info("[ADMIN] UPDATE USER : {}", uid);

        return ResponseEntity.ok(
                BaseResponse.Success.builder().build()
        );
    }
}
