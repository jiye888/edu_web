package com.jtudy.education.config.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ExceptionResponseEntity {
    private int status;
    private String message;
    private String exceptionCode;

    public static ResponseEntity toResponseEntity(ExceptionCode e) {
        ExceptionResponseEntity body = ExceptionResponseEntity.builder().status(e.getStatus()).exceptionCode(e.name()).message(e.getMessage()).build();
        return ResponseEntity.status(e.getStatus()).contentType(MediaType.APPLICATION_JSON).body(body);
    }


}
