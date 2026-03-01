package com.waim.api.common.config.handler;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.module.core.common.model.error.ServerException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private final MessageSource messageSource;







    // Request Body 누락 Exception
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.error(ex.getMessage());
        Locale locale = LocaleContextHolder.getLocale();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        BaseResponse.Error.builder()
                                .code("WRE_0001")
                                .message(
                                        messageSource.getMessage(
                                                "runtime.error.message_not_readable",
                                                null,
                                                "Data body is required.",
                                                locale
                                        )
                                )
                                .build()
                );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {

        log.error("UNCATEGORIZED EXCEPTION", ex);
        Locale locale = LocaleContextHolder.getLocale();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        BaseResponse.Error.builder()
                                .code("WRE_0002")
                                .message(
                                        messageSource.getMessage(
                                                "runtime.error.wre_0002.uncategorized_exception",
                                                null,
                                                "Unknown error occurred.",
                                                locale
                                        )
                                )
                                .build()
                );
    }
}
