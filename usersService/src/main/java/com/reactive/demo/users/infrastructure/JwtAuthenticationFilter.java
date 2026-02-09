package com.reactive.demo.users.infrastructure;

import com.reactive.demo.users.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

/*
this class is responsible for intercepting incoming http request
and performing json web token authentication

this filter interface is specifically designed for reactive web application in spring web flux
non blocking processing of interface
*/
public class JwtAuthenticationFilter implements WebFilter {


    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token= extractToken(exchange);
        if(token==null) return chain.filter(exchange);//just passing the request to next filter chain wihout any processing

        return validateToken(token)
                .flatMap(isValid-> isValid? authenticationAndContinue(token,exchange,chain )
                        :handleInvalidToken(exchange));
    }
// this method authenticate user based on the token subject and it allows http request to proceed through the filter chain
// with updated security context
    private Mono<Void> authenticationAndContinue(String token, ServerWebExchange exchange, WebFilterChain chain) {

        return Mono.just( jwtService.extractTokenSubject(token))
                .flatMap(subject ->{
                    Authentication auth = new UsernamePasswordAuthenticationToken(subject, null,
                            Collections.emptyList());//passing emptylist of authorities

                    return  chain
                            .filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)); //to add auth obj to spring context
                });

    }

    private   Mono<Void> handleInvalidToken(ServerWebExchange exchange){
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();  //this will end processing of this request
    }


    private String extractToken(ServerWebExchange exchange){
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")){
            return  authorizationHeader.substring(7).trim();
        }
        return  null;
    }

    private Mono<Boolean> validateToken( String token){
        return jwtService.validateJwt(token);
    }
}

/*
ServerWebExchange this provide us with the access of HTTp request and HTTp response objects.
webfilterchain it allows this request to continue to the next filter and chain.
 */