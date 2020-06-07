package com.fsm.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fsm.resource.ErrorResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class FsmControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(FsmControllerAdvice.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResource> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ErrorResource.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResource> handleJsonProcessingException(JsonProcessingException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ErrorResource.builder().message(ex.getMessage()).build());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResource> handleNoSuchElementException(NoSuchElementException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResource.builder().message(ex.getMessage()).build());
    }
}
