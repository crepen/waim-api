package com.waim.api.common.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waim.api.common.model.response.BaseResponse;
import com.waim.module.core.domain.auth.model.error.AuthForbiddenException;
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
        var baseErrorCode = new AuthForbiddenException();

        response.setStatus(baseErrorCode.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        var responseObj = BaseResponse.Error.builder()
                .code(baseErrorCode.getErrorCode())
                .message(
                        messageSource.getMessage(
                                baseErrorCode.getMessage(),
                                null,
                                baseErrorCode.getMessage(),
                                request.getLocale()
                        )
                )
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(responseObj));
    }
}
