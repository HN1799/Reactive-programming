package com.reactive.demo.users.infrastructure;


import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ErrorResponse> handleDuplicateKeyException (DuplicateKeyException exception){
        return Mono.just(ErrorResponse.builder(exception, HttpStatus.CONFLICT, exception.getMessage()).build());
    }
// to catch bean validation error
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ErrorResponse> handleWebExchangeBindException (WebExchangeBindException exception){
        // here we will have all valdiation error messages ready to sent backk and give a string containing
//        all validation error messages ready to  be sent back
        String errorMessage = exception.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return Mono.just(ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST,errorMessage).build());
    }

    @ExceptionHandler(Exception.class)
    public Mono<ErrorResponse> handleGeneralException (Exception exception){
        return Mono.just(ErrorResponse.builder(exception, HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()).build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ErrorResponse> handleBadCredentialsException(BadCredentialsException exception){
        return Mono.just(ErrorResponse.builder(exception, HttpStatus.UNAUTHORIZED, exception.getMessage()).build());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public  Mono<ErrorResponse> authorizationDeniedException(AuthorizationDeniedException exception){
        return Mono.just(ErrorResponse.builder(exception, HttpStatus.FORBIDDEN, exception.getMessage()).build());
    }
}
