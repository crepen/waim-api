package com.waim.api.common.config.handler;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.module.core.common.model.error.ServerException;
import com.waim.module.core.common.model.error.UncategorizedException;
import com.waim.module.core.common.model.error.UnsupportedMediaTypeException;
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
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private final MessageSource messageSource;


    // Media Type Not Supported
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> httpMediaTypeNotSupportedException(
            final HttpMediaTypeNotSupportedException e
    ) {
        var baseException = new UnsupportedMediaTypeException();
        Locale locale = LocaleContextHolder.getLocale();
        return getResponse(locale , baseException);
    }




    // Request Body 누락 Exception
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.error(ex.getMessage());
        var baseException = new UncategorizedException();
        Locale locale = LocaleContextHolder.getLocale();
        return getResponse(locale , baseException);
    }


    // Other Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        log.error("UNCATEGORIZED EXCEPTION", ex);
        var baseException = new UncategorizedException();
        Locale locale = LocaleContextHolder.getLocale();
        return getResponse(locale , baseException);
    }


    private ResponseEntity<?> getResponse(Locale locale , ServerException baseException){
        return ResponseEntity
                .status(baseException.getStatusCode())
                .body(
                        BaseResponse.Error.builder()
                                .code(baseException.getErrorCode())
                                .message(
                                        messageSource.getMessage(
                                                baseException.getLocaleMessageCode(),
                                                null,
                                                baseException.getMessage(),
                                                locale
                                        )
                                )
                                .build()
                );
    }
}
