package com.reactive.demo.users.service;

import reactor.core.publisher.Mono;

public interface JwtService {
    String generateJwt(String subject);  //subject= userId
    Mono<Boolean> validateJwt(String token);
    String extractTokenSubject(String token);
}
