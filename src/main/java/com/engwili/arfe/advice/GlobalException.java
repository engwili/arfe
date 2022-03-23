package com.engwili.arfe.advice;

import com.engwili.arfe.exception.ArfeException;
import com.engwili.arfe.exception.ArfeExceptionDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class GlobalException {

    @ExceptionHandler(value = ArfeException.class)
    public ResponseEntity<ArfeExceptionDto> manageException(ArfeException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new ArfeExceptionDto(ex.getMessage()));
    }
}