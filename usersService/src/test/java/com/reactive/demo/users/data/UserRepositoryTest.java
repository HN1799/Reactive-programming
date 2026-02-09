package com.reactive.demo.users.data;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // all test methods will share this  one test instance only
class UserRepositoryTest {

    @Autowired
    private DatabaseClient databaseClient;//allows us to intreact with database in non blocking way

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void setUp() {
        UserEntity userEntity = new UserEntity(UUID.randomUUID(), "Jhon", "Doe", "Jhon.Doe@Gmail.com", "1234123");
        UserEntity userEntity2 = new UserEntity(UUID.randomUUID(), "himanshu", "DoNahae", "himanshu.nahak@Gmail.com", "1234123");

        String insertSql = "INSERT INTO users(id,first_name,last_name,email,password) VALUES (:id, :firstName, :lastName, :email, :password)";
//       use database client to execute sql query that will insert users in our database table but in a non blocking way
        Flux.just(userEntity, userEntity2)   //flux.just so that user can be process one by one
                .concatMap(user -> databaseClient.sql(insertSql) //to squentially execute query for each user
                        .bind("id", user.getId())
                        .bind("firstName", user.getFirstName())  //bind method to safely replace placeholders
                        .bind("lastName", user.getLastName())
                        .bind("email", user.getEmail())
                        .bind("password", user.getPassword())
                        .fetch()   //run query in the database
                        .rowsUpdated()) //return number of rows affected
                .then()  // to make sure operation waits until all inserts are done.
                .as(StepVerifier::create)//converts react stream into testable form
                .verifyComplete(); //subscribes to reactive stream and checks stream finsihes normally
    }

    @AfterAll
    void tearDown() {
        databaseClient.sql("TRUNCATE TABLE users")
                .then()
                .as(StepVerifier::create)// it will transform mono into step verifier instance and step verfier is what will allow us to check
//                whether mono  completes succesfully or not
                .verifyComplete();
    }

    @Test
    void testFindByEmail_WithEmailThatExists_ReturnsMatchingUser() {
        //Arrange
        String emailToFind = "Jhon.Doe@Gmail.com";

        //Act and Assert
        StepVerifier.create(userRepository.findByEmail(emailToFind))
                .expectNextMatches(user -> user.getEmail().equals(emailToFind))
                .verifyComplete();
    }

    @Test
    void testFindByEmail_WithEmailThatDoesNotExist_ReturnsEmptyMono() {
        // Arrange
        String nonExistentEmail = "nonexistent@example.com";


        // Act & Assert
        StepVerifier.create(userRepository.findByEmail(nonExistentEmail))
                .expectNextCount(0)  //This tells StepVerifier that we expect zero results.
                .verifyComplete();

    }

    @Test
    void testFindAllBy_WithValidPageable_ReturnsPaginatedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2); // First page, page size = 2
        // Act & Assert
        StepVerifier.create(userRepository.findAllBy(pageable))
                .expectNextCount(2) // Expect exactly 2 items on the first page
                .verifyComplete();



        // Act & Assert
    }

    @Test
    void testFindAllBy_WithNonExistentPage_ReturnsEmptyFlux() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 2); // Second page, page size = 2 (no data exists here)

        // Act & Assert
        StepVerifier.create(userRepository.findAllBy(pageable))
                .expectNextCount(0) // Expect no items on the second page
                .expectComplete()
                .verify();
    }

    @Test
    void testSave_whenExistingEmailProvided_shouldFail() {
        UserEntity invalidUser = new UserEntity(null, "Sergey", "Kargopolov", "Jhon.Doe@Gmail.com", "password");

        userRepository.save(invalidUser)
                .as(StepVerifier::create)
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    void testSave_whenValidUserProvided_shouldSucceed() {
        // Arrange
        UserEntity validUser = new UserEntity(null, "Sergey", "Kargopolov", "test@test.com", "password123");

        // Act & Assert
        userRepository.save(validUser)
                .as(StepVerifier::create)
                .expectNextMatches(savedUser -> {
                    return savedUser.getId() != null
                            && savedUser.getFirstName().equals(validUser.getFirstName())
                            && savedUser.getLastName().equals(validUser.getLastName())
                            && savedUser.getEmail().equals(validUser.getEmail());
                })
                .verifyComplete();
    }


}