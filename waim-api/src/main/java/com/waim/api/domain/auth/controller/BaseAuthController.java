package com.waim.api.domain.auth.controller;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.auth.model.dto.WAIMAuthGrantType;
import com.waim.api.domain.auth.model.dto.request.IssuanceTokenRequest;
import com.waim.core.domain.auth.model.error.WAIMLoginValidateException;
import com.waim.core.domain.auth.service.AuthService;
import com.waim.core.domain.user.service.WAIMAdminUserService;
import com.waim.core.common.model.error.WAIMException;
import com.waim.core.common.util.jwt.JwtTokenProvider;
import com.waim.core.common.util.jwt.model.JwtGroup;
import com.waim.core.common.util.jwt.model.JwtUserDetail;
import com.waim.core.domain.user.service.WAIMUserService;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "AUTH" , description = "인증 관리 API")
public class BaseAuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final WAIMAdminUserService WAIMAdminUserService;
    private final WAIMUserService userService;
    private final AuthService authService;

    @GetMapping
    @Operation(
            summary = "로그인 사용자 데이터 조회" ,
            description = "로그인 사용자 데이터 조회"
    )
    public ResponseEntity<?> getUserData(
            @AuthenticationPrincipal JwtUserDetail userDetail
    ) {

        return ResponseEntity.ok()
                .body(userDetail);
    }

    @PutMapping
    @Operation(
            summary = "토큰 발급",
            description = "로그인 사용자 토큰 발급" ,
            security = @SecurityRequirement(name = "")
    )
    public ResponseEntity<?> authorization(
            HttpServletRequest request,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(oneOf = {IssuanceTokenRequest.Login.class, IssuanceTokenRequest.Refresh.class}),
                            // Scalar UI는 이 examples를 보고 우측 cURL과 샘플 코드를 생성합니다.
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
    ) throws WAIMException {



        JwtGroup loginUserGroup = null;
        if(reqBody.getGrantType() == WAIMAuthGrantType.LOGIN){
            IssuanceTokenRequest.Login loginBody = (IssuanceTokenRequest.Login) reqBody;
            loginUserGroup = authService.loginUser(loginBody.getId() , loginBody.getPassword());
        }
        else if(reqBody.getGrantType() == WAIMAuthGrantType.REFRESH){
            String refToken = null;
            String bearerToken = request.getHeader("Authorization");
            if(StringUtils.hasText(bearerToken)){
                refToken = bearerToken.replaceAll("Bearer", "").trim();
            }

            loginUserGroup = authService.refreshJwtGroup(refToken);
        }
        else{
            throw WAIMLoginValidateException.UNMATCHED_GRANT_TYPE;
        }

        return ResponseEntity.ok().body(
                BaseResponse.Success.builder()
                        .result(loginUserGroup)
                        .build()
        );
    }
}
