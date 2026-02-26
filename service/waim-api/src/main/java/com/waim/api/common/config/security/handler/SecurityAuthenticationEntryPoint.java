package com.waim.api.common.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waim.api.domain.auth.model.error.AuthErrorCode;
import com.waim.api.common.model.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageSource messageSource;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        var baseErrorCode = AuthErrorCode.UNAUTHORIZED_WA_A0001;

        // HTTP 상태 코드 설정 (401 Unauthorized)
        response.setStatus(baseErrorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        var responseObj = BaseResponse.Error.builder()
                .code(baseErrorCode.getCode())
                .message(messageSource.getMessage(baseErrorCode.getMessage(), null, "Unauthorized" , request.getLocale()))
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(responseObj));
    }
}
