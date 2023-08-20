package com.jtudy.education.config.exception;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    String exceptionStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return stackTrace;
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity handleCustomException(CustomException e) {
        logger.error(exceptionStackTrace(e));
        return ExceptionResponseEntity.toResponseEntity(e.getExceptionCode());
    }

}
