package com.reactive.demo.users.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="USERS")
public class UserEntity {

    @Id
    private UUID id;

    @Column("FIRST_NAME")
    private String firstName;


    @Column("LAST_NAME")
    private String lastName;

//    @Column("email")
    private String email;

//    @Column("password")
    private String password;

}
