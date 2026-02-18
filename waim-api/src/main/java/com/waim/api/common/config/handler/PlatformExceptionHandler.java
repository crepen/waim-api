package com.waim.api.common.config.handler;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.core.common.model.error.PlatformException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class PlatformExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(PlatformException.class)
    public ResponseEntity<?> handlePlatformException(
            PlatformException ex
    ) {
        Locale locale = LocaleContextHolder.getLocale();

        var castLocaleArgs =
                ex.getMessageArgs() == null
                        ? new String[]{}
                        : Arrays.stream(ex.getMessageArgs()).map(x ->
                        messageSource.getMessage(
                                x,
                                null,
                                x,
                                locale
                        )
                ).toArray(String[]::new);

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(
                        BaseResponse.Error.builder()
                                .code(ex.getErrorCode()) // 적절한 공통 코드 사용
                                .message(
                                        messageSource.getMessage(
                                                ex.getMessage(),
                                                castLocaleArgs,
                                                ex.getMessage(),
                                                locale
                                        )
                                )
                                .build()
                );
    }
}
