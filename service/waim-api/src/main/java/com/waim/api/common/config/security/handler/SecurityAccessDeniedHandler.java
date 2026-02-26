package com.waim.api.common.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waim.api.domain.auth.model.error.AuthErrorCode;
import com.waim.api.common.model.response.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageSource messageSource;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        var baseErrorCode = AuthErrorCode.ACCESS_DENIED_WA_A0002;

        response.setStatus(baseErrorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        var responseObj = BaseResponse.Error.builder()
                .code(baseErrorCode.getCode())
                .message(messageSource.getMessage(baseErrorCode.getMessage(), null, "Forbidden" ,request.getLocale()))
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(responseObj));
    }
}
