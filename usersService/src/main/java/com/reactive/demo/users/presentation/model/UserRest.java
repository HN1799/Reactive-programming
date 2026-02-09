package com.reactive.demo.users.presentation.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
//dto
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRest {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AlbumRest> albumRestList;

}
