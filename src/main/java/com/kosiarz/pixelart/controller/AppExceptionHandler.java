package com.kosiarz.pixelart.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class AppExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AppExceptionHandler.class);

    @ExceptionHandler(MultipartException.class)
    public ProblemDetail handleMultipartException(MultipartException ex) {
        log.error("Invalid Multipart Request: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Invalid Multipart Request");
        return problemDetail;
    }
}
