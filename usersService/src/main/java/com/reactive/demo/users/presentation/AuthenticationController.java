package com.reactive.demo.users.presentation;

import com.reactive.demo.users.presentation.model.AuthenticationRequest;
import com.reactive.demo.users.service.AuthenticationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@RequestBody Mono<AuthenticationRequest> authenticationRequestMono){

        return authenticationRequestMono
                .flatMap(authenticationRequest ->
                authenticationService.authenticate(authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()))
                .map(authenticationResultmap-> ResponseEntity.ok()
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+
                                authenticationResultmap.get("token"))
                        .header("UserId", authenticationResultmap.get("userId"))
                        .build());
//        handled in global exception handler class
//                .onErrorReturn(BadCredentialsException.class,    //operator to handle error in reactive stream
//                        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials"));
    }




}
