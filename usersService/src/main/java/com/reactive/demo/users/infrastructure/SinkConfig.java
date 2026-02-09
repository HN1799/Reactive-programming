package com.reactive.demo.users.infrastructure;


import com.reactive.demo.users.presentation.model.UserRest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class SinkConfig {

    @Bean
    public Sinks.Many<UserRest> userSinks(){
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}

/*
many means it will emit multiple times
multicast makes able to send to multiple subscriber
onbackpressureBuffer- this creates a special continer to handle back pressure.

*/
