package com.reactive.demo.users.infrastructure;

import com.reactive.demo.users.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity //marker annotaiton which enable spring security in web Flux application
//it is used to import web flux security configuration and setup some requried beans for security in web.flux enviroment
@EnableReactiveMethodSecurity
public class WebSecurity {


    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                  ReactiveAuthenticationManager reactiveAuthenticationManager,  //injecting bean creaed in  AuthenticationManagerConfig.class
                                                  JwtService jwtService) {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
        return http.authorizeExchange(exchanes -> exchanes
                        .pathMatchers(HttpMethod.POST, "/users").permitAll()
                        .pathMatchers(HttpMethod.POST, "/login").permitAll()
                        .pathMatchers(HttpMethod.GET, "/users/stream").permitAll()
                        .anyExchange().authenticated())
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(reactiveAuthenticationManager)
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION) //register our jwtAuthfilter
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) //to make this application stateless
                .build();
    }




    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    private CorsConfigurationSource  corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }


}

/*

this method we will configure security for our incoming request for our web flux applicatoin.
security filter chain is a collection of spring security filters that are applied to incomign http request .
this is why return this is  SecurityWebFilterChain. because the java object we  create and configure in this method will be configured as
securityFilterChain
Each filter in this chain is having specific security related responsibilites like for example user authentication, authorization and
session management.
this method recives ServerHttpSecurity as method argument
this class is specifically designed for configuring security in spring web application. for reactive application.

disabling basic authentication , as when enabled it allows client application to send login credentials like username, password  in
authorization header of each http request but in this application we created custom login api to authenticate and to get acces token.

csrf is a security vulnerability that tricks user into performin action on the website without thier consent. to prevent this website uses
a unique token with each request and this helps to make sure that the request is legitimate and is not forged by attacker.
in restful and stateless microservices, csrf protection is disabled  because it is designed for stateful session.
if you are using cookie based authentication u might not disable csrf method
but as we are building stateless and restful services we have to use json web token and disable csrf token.

securityContextRepository is responsible for persisting spring security context between request by default, spring security uses
websessionserversecuitycontextrepository and this does store security context in web session.


 */


