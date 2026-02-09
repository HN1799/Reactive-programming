package com.reactive.demo.users.presentation.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "FirstName cannot be empty")
    @Size(min = 2, max =50, message = "First name cannot be shorter than 2 and longer than 50 characters")
    private String firstName;

    @NotBlank(message = "lastName cannot be empty")
    @Size(min = 2, max =50, message = "last name cannot be shorter than 2 and longer than 50 characters")
    private String lastName;

    @NotBlank(message="email cannot be empty")
    @Email(message = "please enter a valid email address")
    private String email;

    @NotBlank(message="password cannot be empty")
    @Size(min = 8, max =16, message = "password cannot be shorter than 8 and longer than 16 character ")
    private String password;


}
