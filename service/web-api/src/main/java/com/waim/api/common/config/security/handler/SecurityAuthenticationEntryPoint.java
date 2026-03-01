package com.waim.api.common.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waim.api.common.model.response.BaseResponse;
import com.waim.module.core.domain.auth.model.error.AuthUnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageSource messageSource;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        var baseErrorCode = new AuthUnauthorizedException();


        Locale locale = request.getLocale();
        if (request.getHeader("Accept-Language") != null) {
            locale = Locale.forLanguageTag(request.getHeader("Accept-Language"));
        }

        Locale castLocale = Locale.of(locale.getLanguage());


        // HTTP 상태 코드 설정 (401 Unauthorized)
        response.setStatus(baseErrorCode.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        var responseObj = BaseResponse.Error.builder()
                .code(baseErrorCode.getErrorCode())
                .message(
                        messageSource.getMessage(
                                baseErrorCode.getLocaleMessageCode(),
                                null,
                                baseErrorCode.getMessage(),
                                castLocale
                        )
                )
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(responseObj));
    }
}
