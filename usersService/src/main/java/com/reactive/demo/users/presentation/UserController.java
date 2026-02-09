package com.reactive.demo.users.presentation;


import com.reactive.demo.users.presentation.model.CreateUserRequest;
import com.reactive.demo.users.presentation.model.UserRest;
import com.reactive.demo.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public Mono<ResponseEntity<UserRest>> createUser(@RequestBody @Valid Mono<CreateUserRequest> createUserRequest) {
        return  userService.createUser(createUserRequest).map(userRest -> ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/users/"+userRest.getId()))  //find in location header
                .body(userRest));

    }


    @GetMapping("/{userId}")
//    @PreAuthorize("authentication.principal.equals(#userId.toString()) or hasRole('ROLE_ADMIN')") //    pricipal is userId
    @PostAuthorize("returnObject.body!=null and (returnObject.body.id.toString().equals(authentication.principal))")
    public Mono<ResponseEntity<UserRest>> getUser(@PathVariable("userId") UUID userId,
                                                  @RequestParam(name="include", required = false)String include,
                                                  @RequestHeader(name="Authorization") String jwt){
        return userService.getUserById(userId, include, jwt)
                .map(userRest -> ResponseEntity.status(HttpStatus.OK).body(userRest))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }


    @GetMapping
    public Flux<UserRest> getUsers(
            @RequestParam(value="offset", defaultValue = "0") int offset,
            @RequestParam(value="limit" , defaultValue ="50") int limit
    ){
        return  userService.findAll(offset,limit);
    }



    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UserRest> streamUsers(){
        return userService.streamUsers();
    }

}


/*

        imperative code for create User
        UserRest userRest = new UserRest();

        return createUserRequest.map(request -> new UserRest(UUID.randomUUID(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail())
        ).map(userRest -> ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/users/"+userRest.getId()))  //find in header (location field)
                .body(userRest));
return Mono.just("OK");

to support fully nonblocking reading or streaming of data between client applciation. input and o/p shoule be wrap in mono or flux
Mono can be used for  handle single data that is not available right away.
the request body can be processed as it arrived rather than waiting for the entire body to be recieved/

**in most cases we dont use reactive type in request especailly when we need to query string parameter

 preauthorize
i want to check if currently authenticated user is allowed to acces information of this particular user or not
can use it for operation like update and delete


 */
