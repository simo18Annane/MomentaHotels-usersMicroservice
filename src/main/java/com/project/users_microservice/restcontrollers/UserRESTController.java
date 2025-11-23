package com.project.users_microservice.restcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.users_microservice.entities.User;
import com.project.users_microservice.register.RegistrationRequest;
import com.project.users_microservice.services.UserService;

@RestController
@CrossOrigin(origins = "*") // Allow requests from any origin
public class UserRESTController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody RegistrationRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/checkToken")
    public User checkEmail(@RequestBody String token) {
        return userService.validateToken(token);
    }

}
