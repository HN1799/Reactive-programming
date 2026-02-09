package com.reactive.demo.users.data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, UUID> {
//    findAll method will give all the details but probelm if there is a large number of data
//    at that time we can use pagination but there is no bydefault method present in ReactiveCrudRepository

//    spring data query method
    Flux<UserEntity>  findAllBy(Pageable pageable);


    Mono<UserEntity> findByEmail(String username);
}
