package com.reactive.demo.users.presentation;

import com.reactive.demo.users.infrastructure.TestSecurityConfig;
import com.reactive.demo.users.presentation.model.CreateUserRequest;
import com.reactive.demo.users.presentation.model.UserRest;
import com.reactive.demo.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(UserController.class) //to test our application without loading entire application
// above annotaiton will setup a minimal spring context for web flux test
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @MockitoBean //create a mock bean
    private UserService userService;

    @Autowired
    private WebTestClient webTestClient; //it uses web client internally

    @Test
    void testCreateUser_withValidRequest_returnCreatedStatusAndUserDetails(){
        //Arrange
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setFirstName("Himanshu");
        createUserRequest.setLastName("asdfasdf");
        createUserRequest.setEmail("1799himanshu@gmail.com");
        createUserRequest.setPassword("q23412341234");


        UUID userId = UUID.randomUUID();
        String expectedLocation = "/users/"+ userId;
        UserRest expectUserRest = new UserRest( );
        expectUserRest.setFirstName("Himanshu");
        expectUserRest.setLastName("asdfasdf");
        expectUserRest.setEmail("1799himanshu@gmail.com");
        expectUserRest.setId(userId);
        expectUserRest.setAlbumRestList(null);

        when(userService.createUser(Mockito.<Mono<CreateUserRequest>>any())).thenReturn(Mono.just(expectUserRest));


        //Act
        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createUserRequest)
                .exchange()
                .expectStatus().isCreated()  //check if httpstatus code is 201
                .expectHeader().location(expectedLocation)
                .expectBody(UserRest.class)  //to test the responsebody is of type UserRest
                .value(response->{
                    assertEquals(expectUserRest.getId(), response.getId());
                    assertEquals(expectUserRest.getFirstName(), response.getFirstName());
                    assertEquals(expectUserRest.getLastName(),response.getLastName());
                });


        //Assert
        verify(userService, times(1)).createUser(Mockito.<Mono<CreateUserRequest>>any());
    }


    @Test
    void testCreateUser_whenServiceThrowsException_returnsInternalServerErrorWithExpectedStructure() {
        // Arrange
        CreateUserRequest validRequest = new CreateUserRequest(
                "Sergey",
                "Kargopolov",
                "user@example.com",
                "123456789"
        );

        when(userService.createUser(any())).thenReturn(Mono.error(new RuntimeException("Service error")));

        // Act & Assert
        webTestClient
                .post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.instance").isEqualTo("/users")
                .jsonPath("$.status").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .jsonPath("$.detail").isEqualTo("Service error");

        verify(userService, times(1)).createUser(any());
    }


  
}