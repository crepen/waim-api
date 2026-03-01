package com.waim.api.domain.auth.controller;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.auth.model.dto.request.IssuanceTokenRequest;
import com.waim.api.domain.auth.model.dto.response.JwtResponse;
import com.waim.module.core.domain.auth.model.data.AuthGrantType;
import com.waim.module.core.domain.auth.model.error.AuthInvalidGrantTypeException;
import com.waim.module.core.domain.auth.service.AuthService;
import com.waim.module.data.common.security.SecurityUserDetail;
import com.waim.module.util.jwt.data.JwtResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "AUTH" , description = "인증 관리 API")
public class AuthController {

    private final AuthService authService;

    @GetMapping
    @Operation(
            summary = "로그인 사용자 데이터 조회",
            description = "로그인 사용자 데이터 조회"
    )
    public ResponseEntity<?> getUserData(
            @AuthenticationPrincipal SecurityUserDetail userDetail
    ) {

        return ResponseEntity.ok()
                .body(userDetail);
    }

    @PutMapping
    @Operation(
            summary = "토큰 발급",
            description = "로그인 사용자 토큰 발급",
            security = @SecurityRequirement(name = "")
    )
    public ResponseEntity<?> authorization(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(oneOf = {IssuanceTokenRequest.Login.class, IssuanceTokenRequest.Refresh.class}),
                            examples = {
                                    @ExampleObject(
                                            name = "Login Case",
                                            summary = "ID/PW로 로그인할 때",
                                            description = "grant_type을 login으로 설정하고 id, password를 입력합니다.",
                                            value = "{\"grant_type\": \"login\", \"id\": \"admin\", \"password\": \"ww1111\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Refresh Case",
                                            summary = "토큰을 갱신할 때",
                                            description = "grant_type을 refresh로 설정합니다. id와 password는 필요하지 않습니다.",
                                            value = "{\"grant_type\": \"refresh\"}"
                                    )
                            }
                    )
            )
            @RequestBody IssuanceTokenRequest.Base reqBody
    ) {
        JwtResult loginUserGroup = null;
        if (Objects.equals(reqBody.getGrantType().toUpperCase(), AuthGrantType.LOGIN.name())) {
            IssuanceTokenRequest.Login loginBody = (IssuanceTokenRequest.Login) reqBody;
            loginUserGroup = authService.login(loginBody.getId(), loginBody.getPassword());
        } else if (Objects.equals(reqBody.getGrantType().toUpperCase(), AuthGrantType.REFRESH.name())) {
            String bearerToken = request.getHeader("Authorization");
            loginUserGroup = authService.renewToken(bearerToken);
        } else {
            throw new AuthInvalidGrantTypeException();
        }


        JwtResponse res = JwtResponse.builder()
                .access(
                        JwtResponse.Token.builder()
                                .token(loginUserGroup.getAccessToken().token)
                                .expiredAt(loginUserGroup.getAccessToken().expiredAt)
                                .build()
                )
                .refresh(
                        JwtResponse.Token.builder()
                                .token(loginUserGroup.getRefreshToken().token)
                                .expiredAt(loginUserGroup.getRefreshToken().expiredAt)
                                .build()
                )
                .build();

        return ResponseEntity.ok().body(
                BaseResponse.Success.builder()
                        .result(res)
                        .build()
        );
    }
}
