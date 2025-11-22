package com.project.users_microservice.services;

import com.project.users_microservice.entities.User;

public interface UserService {

    User createUser(User user);
    User updateUser(User user);
    User getUserById(Long id);
    User getUserByEmail(String email);

}
