package com.reactive.demo.users.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
public class WebClientConfig {

    @Value("${base-uri}")
    private String baseUri;

    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .baseUrl(baseUri)   //this way all http request that this web clinet will make will use this as base domain
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)// for single header
                .defaultHeaders( httpHeaders -> {  //for multiple headers
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
                })
                .build();

    }
}
