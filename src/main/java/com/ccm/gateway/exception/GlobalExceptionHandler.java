package com.ccm.gateway.exception;

import com.ccm.gateway.pojo.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.ConnectException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
        return new ResponseEntity<>(ExceptionResponse
                .builder().errorCode(HttpStatus.NOT_FOUND).errorMessage(ex.getMessage()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<ExceptionResponse> handleConnectionException(ConnectException ex) {
        return new ResponseEntity<>(ExceptionResponse
                .builder().errorCode(HttpStatus.SERVICE_UNAVAILABLE).errorMessage(ex.getMessage()).build(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ExceptionResponse> handleClientError(HttpClientErrorException ex) {
        return new ResponseEntity<>(ExceptionResponse
                .builder()
                .errorCode((HttpStatus) ex.getStatusCode())
                .errorMessage(ex.getMessage())
                .build(), ex.getStatusCode());
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ExceptionResponse> handleWebClientException(WebClientRequestException ex) {
        return new ResponseEntity<>(ExceptionResponse
                .builder()
                .errorCode(HttpStatus.SERVICE_UNAVAILABLE)
                .errorMessage("Service temporarily unavailable")
                .build(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(FallbackException.class)
    public ResponseEntity<ExceptionResponse> handleFallback(FallbackException ex) {
        ExceptionResponse response = ExceptionResponse.builder()
                .errorCode(HttpStatus.SERVICE_UNAVAILABLE)
                .errorMessage("Fallback triggered: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
}

