package com.reactive.demo.users.service;

import com.reactive.demo.users.data.UserEntity;
import com.reactive.demo.users.data.UserRepository;
import com.reactive.demo.users.presentation.model.AlbumRest;
import com.reactive.demo.users.presentation.model.CreateUserRequest;
import com.reactive.demo.users.presentation.model.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock // vs MockitoBean //mock is used for unit testing //mockitobean is specific to springboot used for integration testing
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private WebClient webClient;


    private Sinks.Many<UserRest> usersSink;  //do want to verfiy if event was emitted correctly im not mocking sink

    @BeforeEach
    void setUp(){
        usersSink = Sinks.many().multicast().onBackpressureBuffer();
        userService = new UserServiceImpl(userRepository,passwordEncoder, usersSink,webClient   );
    }
    private UserServiceImpl userService;





    @Test
    void testCreateUser_withValidResult_returnsCreatedUserDetails() {
        //Arrange
        CreateUserRequest validRequest = new CreateUserRequest(
                "Sergey",
                "Kargopolov",
                "user@example.com",
                "123456789"
        );
        UserEntity savedEntity = new UserEntity();
        savedEntity.setPassword(validRequest.getPassword());
        savedEntity.setEmail(validRequest.getEmail());
        savedEntity.setFirstName(validRequest.getFirstName());
        savedEntity.setLastName(validRequest.getLastName());
        savedEntity.setId(UUID.randomUUID());


        Mockito.when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        Mockito.when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(savedEntity));

        //Act

        Mono<UserRest> result = userService.createUser(Mono.just(validRequest));

        //Assert
        StepVerifier                //test utility from project react desgined to test reactive streams //allows to test without blocking
                .create(result)     //subscribes to mono of UserRest that is returned by createUser
                .expectNextMatches(userRest-> userRest.getId().equals(savedEntity.getId()) &&
                        userRest.getFirstName().equals(savedEntity.getFirstName()))
                .verifyComplete();
        verify(userRepository,times(1)).save(any(UserEntity.class));

//        UserRest userRest = result.block();  //to test in blcoking way
//        assertEquals(savedEntity.getLastName(), userRest.getLastName());


    }

    @Test
    void testCreateUser_withValidRequest_EmitsEventToSink() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "Sergey",
                "Kargopolov",
                "test@test.com",
                "123456789");

        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(UUID.randomUUID());
        savedEntity.setFirstName(request.getFirstName());
        savedEntity.setLastName(request.getLastName());
        savedEntity.setEmail(request.getEmail());
        savedEntity.setPassword(request.getPassword());

        Mockito.when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        Mockito.when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(savedEntity));

        // Subscribe to the sink before triggering the service call.
        Flux<UserRest> sinkFlux = usersSink.asFlux();

        // Act and Assert
        StepVerifier.create(userService.createUser(Mono.just(request))
                        .thenMany(usersSink.asFlux().take(1)))
                .expectNextMatches(userRest -> userRest.getId().equals(savedEntity.getId()) &&
                        userRest.getFirstName().equals(savedEntity.getFirstName()) &&
                        userRest.getLastName().equals(savedEntity.getLastName()) &&
                        userRest.getEmail().equals(savedEntity.getEmail()))
                .verifyComplete();
    }

    @Test
    void testGetUserById_WithExistingUser_ReturnsUserRest() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFirstName("Sergey");
        userEntity.setLastName("Kargopolov");
        userEntity.setEmail("test@test.com");

        Mockito.when(userRepository.findById(userId)).thenReturn(Mono.just(userEntity));


        // Act
        Mono<UserRest> result = userService.getUserById(userId, null, "jwt-token");

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(userRest -> userRest.getId().equals(userId) &&
                        userRest.getFirstName().equals(userEntity.getFirstName()) &&
                        userRest.getLastName().equals(userEntity.getLastName()) &&
                        userRest.getEmail().equals(userEntity.getEmail()) &&
                        userRest.getAlbumRestList() == null)
                .verifyComplete();
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testGetUserById_WithIncludeAlbums_ReturnsAlbums() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String jwt = "valid-jwt";

        // 1. Setup UserEntity
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFirstName("Sergey");
        userEntity.setLastName("Kargopolov");
        userEntity.setEmail("test@test.com");
        userEntity.setPassword("encodedPass");

        // 2. Mock repository response
        Mockito.when(userRepository.findById(userId)).thenReturn(Mono.just(userEntity));

        // 3. Mock WebClient response with albums
        WebClient.RequestHeadersUriSpec getSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

        Mockito.when(webClient.get()).thenReturn(getSpec);
        Mockito.when(getSpec.uri(any(Function.class))).thenReturn(headersSpec);
        Mockito.when(headersSpec.header(eq("Authorization"), eq(jwt))).thenReturn(headersSpec);
        Mockito.when(headersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        // Explicitly return test albums
        AlbumRest album1 = new AlbumRest("album1", "Summer Vacation");
        AlbumRest album2 = new AlbumRest("album2", "Family Reunion");
        Mockito.when(responseSpec.bodyToFlux(AlbumRest.class)).thenReturn(Flux.just(album1, album2));

        // Act
        Mono<UserRest> result = userService.getUserById(userId, "albums", jwt);

        // Assert: Verify albums are present
        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    // Verify user details
                    assertEquals(userId, user.getId(), "User ID mismatch");
                    assertEquals(userEntity.getFirstName(), user.getFirstName(), "First name mismatch");
                    assertEquals(userEntity.getLastName(), user.getLastName(), "Last name mismatch");
                    assertEquals(userEntity.getEmail(), user.getEmail(), "Email mismatch");

                    // Verify albums
                    assertNotNull(user.getAlbumRestList(), "Albums list should not be null");
                    assertEquals(2, user.getAlbumRestList().size(), "Incorrect number of albums");
                    assertEquals("Summer Vacation", user.getAlbumRestList().get(0).getTitle());
                    assertEquals("Family Reunion", user.getAlbumRestList().get(1).getTitle());
                    return true;
                })
                .verifyComplete();

        // Verify repository call
        verify(userRepository).findById(userId);
    }

}