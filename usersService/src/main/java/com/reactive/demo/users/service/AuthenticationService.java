package com.reactive.demo.users.service;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuthenticationService {

//    if user Authentication is succesfull it will return hashmap that contain
//    jwt acces token and   userId
    Mono<Map<String, String>>  authenticate(String username, String password);
}
