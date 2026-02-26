package com.waim.api.common.config.handler;


import com.waim.api.common.model.response.BaseResponse;
import com.waim.core.plugin.gitlab.model.error.GitLabPluginException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Order(99)
public class PluginExceptionHandler {


    private final MessageSource messageSource;

    @ExceptionHandler(GitLabPluginException.class)
    public ResponseEntity<?> handleGitlabPluginException(
            final GitLabPluginException e,
            HttpServletRequest request
    ) {
        Locale locale = LocaleContextHolder.getLocale();

        var castLocaleArgs =
                e.getMessageArgs() == null
                        ? new String[]{}
                        : Arrays.stream(e.getMessageArgs()).map(x ->
                        messageSource.getMessage(
                                x,
                                null,
                                x,
                                locale
                        )
                ).toArray(String[]::new);


        return ResponseEntity
                .status(e.getStatusCode())
                .body(
                        BaseResponse.Error.builder()
                                .message(
                                        messageSource.getMessage(
                                                e.getMessage(),
                                                castLocaleArgs,
                                                e.getMessage(),
                                                locale
                                        )
                                )
                                .build()
                );
    }

}
