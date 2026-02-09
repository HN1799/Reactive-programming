package com.reactive.demo.users.service;

import com.reactive.demo.users.presentation.model.CreateUserRequest;
import com.reactive.demo.users.presentation.model.UserRest;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService extends ReactiveUserDetailsService {

    Mono<UserRest> createUser(Mono<CreateUserRequest>  createUserRequestMono);
    Mono<UserRest>  getUserById(UUID uuid, String include, String jwt);
    Flux<UserRest> findAll(int page, int limit);
    Flux<UserRest> streamUsers();
}

/*
ReactiveUserDetailsService-
by extending this telling springboot that this UserService will handle loading of user details for authentication purpose
need to implement findByUsername to help spring framwork to locate user details
 */
