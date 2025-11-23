package com.project.users_microservice.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String city;
    private String country;

}
