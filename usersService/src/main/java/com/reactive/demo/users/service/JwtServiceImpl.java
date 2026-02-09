package com.reactive.demo.users.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtServiceImpl  implements  JwtService{


    private final Environment environment;

    public JwtServiceImpl(Environment environment) {
        this.environment = environment;
    }


    @Override
    public String generateJwt(String subject) {
        return Jwts
                .builder()
                .subject(subject)   //subject claim
                .issuedAt(new Date(System.currentTimeMillis()))  //issueat claim
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))  //expiration claim
                .signWith(getSigninKey())
                .compact();

    }

    @Override
    public Mono<Boolean> validateJwt(String token) {
        return Mono.just(token)
                .map(this::parseToken) //if token is not parsed we will parsed then we will get claims object
                .map(claims -> !claims.getExpiration().before(new Date()))
                .onErrorReturn(false);
    }

    @Override
    public String extractTokenSubject(String token) {
        return parseToken(token).getSubject();
    }

    private Claims parseToken(String token){
        return Jwts.parser()
                .verifyWith(getSigninKey())  //verifies the signature key
                .build()
                .parseSignedClaims(token)//to parse its token and extract claims.
                .getPayload();
    }

    private SecretKey getSigninKey(){
        return Optional.ofNullable(environment.getProperty("token.secret"))
                .map(tokenSecret-> tokenSecret.getBytes())
                .map(tokenSecretBytes-> Keys.hmacShaKeyFor(tokenSecretBytes))
                .orElseThrow(()-> new IllegalArgumentException("token.secret must be configured in the application.properties"));
    }



}
