package com.waim.api.common.config.handler;

import com.waim.core.common.model.error.CommonErrorCode;
import com.waim.api.common.model.response.BaseResponse;
import com.waim.core.common.model.error.WAIMException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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



    @ExceptionHandler(WAIMException.class)
    public ResponseEntity<?> handleException(
            WAIMException ex,
            HttpServletRequest request
    ) {
        Locale locale = LocaleContextHolder.getLocale();

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(
                        BaseResponse.Error.builder()
                                .code(ex.getErrorCode().getCode()) // 적절한 공통 코드 사용
                                .message(
                                        messageSource.getMessage(
                                                ex.getErrorCode().getMessage(),
                                                null,
                                                ex.getErrorCode().getMessage()
                                                , locale)
                                )
                                .build()
                );
    }


    // Request Body 누락 Exception
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.error(ex.getMessage());
        Locale locale = LocaleContextHolder.getLocale();

        return ResponseEntity
                .status(CommonErrorCode.INVALID_PARAMETER.getHttpStatus())
                .body(
                        BaseResponse.Error.builder()
                                .code(CommonErrorCode.INVALID_PARAMETER.getCode()) // 적절한 공통 코드 사용
                                .message(
                                        messageSource.getMessage(
                                                CommonErrorCode.INVALID_PARAMETER.getMessage(),
                                                null,
                                                "Bad Request"
                                                , locale)
                                )
                                .build()
                );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {

        log.error("UNCATEGORIZED EXCEPTION", ex);

        return ResponseEntity.status(500)
                .body(
                        BaseResponse.Error.builder()
                                .code(CommonErrorCode.INTERNAL_SERVER_ERROR.getCode())
                                .message(CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                                .build()
                );
    }
}
