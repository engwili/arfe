package com.engwili.arfe.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@ToString
public class ArfeException extends RuntimeException {

    private HttpStatus status;
    private String message;

}
