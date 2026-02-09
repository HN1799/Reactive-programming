package com.reactive.demo.users.service;

import com.reactive.demo.users.data.UserEntity;
import com.reactive.demo.users.data.UserRepository;
import com.reactive.demo.users.presentation.model.AlbumRest;
import com.reactive.demo.users.presentation.model.CreateUserRequest;
import com.reactive.demo.users.presentation.model.UserRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayDeque;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final Sinks.Many<UserRest> usersSinks ;

    private final WebClient webClient;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, Sinks.Many<UserRest> usersSinks, WebClient webClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.usersSinks = usersSinks;
        this.webClient = webClient;
    }


    @Override
    public Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequestMono) {

        return createUserRequestMono
                .flatMap(this::convertToEntity)
                .flatMap(userRepository::save)  // save method will throw error here. need to handle
                .mapNotNull(this::convertToRest)
                .doOnSuccess(savedUsers-> usersSinks.tryEmitNext(savedUsers));  //create a event, we try to emit the user sync.
//        all subscirber who subscribe to this sink will get the updates
}

/*
    reason to use flatmap instead of map because save method will return mono of user entity and i use map then it
    will return result wrapped into another mono.
    so to flatten nesteed mono  we use flatmap
 */


    @Override
    public Mono<UserRest> getUserById(UUID uuid, String include, String jwt) {
        return userRepository
                .findById(uuid)  //if the return emtpy this will return emplty mono and next line will not execute
                .mapNotNull(this::convertToRest)
                .flatMap(userRest -> {
                    if(include!=null &&include.contains("albums"))
                    {
                        //fetch user's photo albums and add them to user object
                        return includeUserAlbums(userRest, jwt);
                    }
                    return  Mono.just(userRest);
                });
    }

    private Mono<UserRest> includeUserAlbums(UserRest userRest, String jwt) {

        return  webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .port(8084)
                        .path("/albums")
                        .queryParam("userId", userRest.getId())
                        .build())
                .header("Authorization", jwt)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return Mono.error(new RuntimeException("Albums not found  for user"));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    return  Mono.error(new RuntimeException("Server error while facing albums"));
                })
                .bodyToFlux(AlbumRest.class)// to convert response body to flux of albums object
                .collectList()//to get the list of albums from flux and to add it to User object
                .map(albumRests -> {
                    userRest.setAlbumRestList(albumRests);
                    return  userRest;
                })
                .onErrorResume(e-> {  // when error happens it provides fallback publisher
                    logger.error("Error Fetching albums: "+ e);
                    return  Mono.just(userRest);
                });
    }

    @Override
    public Flux<UserRest> findAll(int page, int limit) {
        if (page > 0) page = page - 1;//to start with index 0
        Pageable pageable = PageRequest.of(page, limit);


        return userRepository.findAllBy(pageable).map(userEntity -> convertToRest(userEntity));
    }

//   as soon as new user is created this method will need to emit user details of newly created user
    @Override
    public Flux<UserRest> streamUsers() {
        return usersSinks.asFlux()
                .publish()  //make this flux into hot source, so that it can have multiple subscriber.
                .autoConnect(1);
    }


    private Mono<UserEntity> convertToEntity(CreateUserRequest createUserRequest) {
        return Mono.fromCallable(() -> {
            UserEntity userEntity = new UserEntity();
            BeanUtils.copyProperties(createUserRequest, userEntity);
            userEntity.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
            return userEntity;
        }).subscribeOn(Schedulers.boundedElastic()); // ???

    }

   /*     password encoding is potentially blocking operation for security reason it is designed to be complex and cpu intensive work
        it is not quick and block thread until it completes.
        to make above code non blocking we use special operator fromCallable
        fromCallable is used to wrap potentially blocking operation like passording encoding. so that it will run the code lazily when
        someone relly required.
        by combining fromCallable and subcribeOn we make sure that this cpu extensive work like password encoding is executed
        on a separate thread from a pool that is specifically designed for such blocking task
        as now it return mono we need to update method createUsers
    */


    private UserRest convertToRest(UserEntity userEntity) {
        UserRest userRest = new UserRest();
        BeanUtils.copyProperties(userEntity, userRest);
        return userRest;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {

        return userRepository.findByEmail(username)  // map operator to convert entity to userdetails object
                .map(userEntity -> User
                        .withUsername(userEntity.getEmail())
                        .password(userEntity.getPassword())
                        .authorities(new ArrayDeque<>())
                        .build());
    }




}

/*





 */
