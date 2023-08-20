package com.jtudy.education.config.exception;

import com.google.common.net.HttpHeaders;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.thymeleaf.exceptions.TemplateInputException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static String exceptionStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return stackTrace;
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity handleNullPointerException(NullPointerException e){
        logger.error(exceptionStackTrace(e));
        return ResponseEntity.status(500).body("null pointer exception");
    }

    @ExceptionHandler(RedisConnectionException.class)
    protected ResponseEntity handleRedisConnectionException(RedisConnectionException e) {
        logger.error(exceptionStackTrace(e));
        return ResponseEntity.status(503).body("redis connection exception");
    }

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity handleSqlException(SQLException e) {
        logger.error(exceptionStackTrace(e));
        logger.error(e.getSQLState());
        return ResponseEntity.status(503).body("sql exception");
    }

    @ExceptionHandler(TemplateInputException.class)
    protected ResponseEntity handleTemplateInputException(TemplateInputException e) {
        logger.warn(exceptionStackTrace(e));
        return ResponseEntity.status(303).header(HttpHeaders.LOCATION, "/academy/main").body("template input exception");
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity handleAllException(Exception e) {
        logger.error(exceptionStackTrace(e));
        return ResponseEntity.status(500).body(e.getClass());
    }
}
