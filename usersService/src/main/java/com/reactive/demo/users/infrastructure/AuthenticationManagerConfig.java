package com.reactive.demo.users.infrastructure;

// imp: it is responsible for validating authentication requests
//and it is a component that actually performs user authentication
// it compared user login credentails with the ones stored in db
// it uses reactiveUserDetailsService which we created earlier to fetch user details and it uses password encoder to securely
//compare password
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthenticationManagerConfig {

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService reactiveUserDetailsService,
                                                                       PasswordEncoder passwordEncoder){
        UserDetailsRepositoryReactiveAuthenticationManager reactiveAuthenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);//use findbyEmail

        reactiveAuthenticationManager.setPasswordEncoder(passwordEncoder);// use to encode userpassword and check if it is equal.
        return  reactiveAuthenticationManager;
    }

/*
    we impleted ReactiveUserDetailsService in UserServiceImpl class. it will uses that in line 21.


    UserServiceImpl provides findByUsername() which Spring Security uses to load user details.
    Passwords in the database are stored in encrypted form.
    ReactiveAuthenticationManager uses the configured PasswordEncoder to encode the incoming login password.
    The encoded password is then compared with the stored encrypted password.
    This ensures secure authentication without plain-text password comparison.

    the method is annoated with bean annoation i.e mean ReactiveAuthenticationManager with our custom configuration is
    available for dependency injection in  other spring components so injecting in websecurity class
 */
}
