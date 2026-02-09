package com.reactive.demo.users.service;


import com.reactive.demo.users.data.UserEntity;
import com.reactive.demo.users.data.UserRepository;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServiceImpl implements  AuthenticationService {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthenticationServiceImpl(ReactiveAuthenticationManager reactiveAuthenticationManager, UserRepository userRepository, JwtService jwtService) {
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }


//    purpose to accept username and password and pass them to reactive authenticationmanager
//    once manager has user, pass it will be able to validate and let us know it is succesful or not



    @Override
    public Mono<Map<String, String>>   authenticate(String username, String password) {
        return reactiveAuthenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password))
                .then(getUserDetails(username))
                .map(this::createAuthResponse);

     /*
    if authenticate method is succesfull it will pass authentication object and if not it will throw exception.
    we are not using the authenticationboject and returning so calling userRepo by username, for succesfull scenario
    and creating our custom response.

    it will return  userId so that client application can use it for further subsequent http
    and jwt token so that those http request can be authorized by spring security
     */

    }

    private Map<String, String> createAuthResponse(UserEntity userEntity){
        Map<String, String> result = new HashMap<>();
        result.put("userId", userEntity.getId().toString());
        result.put("token",jwtService.generateJwt(userEntity.getId().toString()));
        return result;
    }

    private Mono<UserEntity> getUserDetails(String username){
        return userRepository.findByEmail(username);
    }
}


/*
then operator is used to chain operation sequentially ignoring the result of previous opertaion.

*/