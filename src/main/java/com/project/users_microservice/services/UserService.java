package com.project.users_microservice.services;

import com.project.users_microservice.entities.User;
import com.project.users_microservice.register.RegistrationRequest;

public interface UserService {

    User registerUser(RegistrationRequest request);
    User updateUser(User user);
    User getUserById(Long id);
    User getUserByEmail(String email);
    User validateToken(String token);

}
