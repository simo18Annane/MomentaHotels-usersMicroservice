package com.project.users_microservice.services;

import java.util.Calendar;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.users_microservice.entities.User;
import com.project.users_microservice.entities.VerifToken;
import com.project.users_microservice.exceptions.EmailAlreadyExistsException;
import com.project.users_microservice.exceptions.ExpiredTokenException;
import com.project.users_microservice.exceptions.InvalidTokenException;
import com.project.users_microservice.register.EmailSender;
import com.project.users_microservice.register.RegistrationRequest;
import com.project.users_microservice.repositories.UserRepository;
import com.project.users_microservice.repositories.VerifTokenRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    VerifTokenRepository verifTokenRepository;

    @Autowired
    EmailSender emailSender;

    @Override
    public User registerUser(RegistrationRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if(optionalUser.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEnabled(false);
        newUser.setFirstname(request.getFirstname());
        newUser.setLastname(request.getLastname());
        newUser.setCity(request.getCity());
        newUser.setCountry(request.getCountry());
        newUser.setRole(User.RoleType.CUSTOMER);
        User savedUser = userRepository.save(newUser);

        String token = generateToken();
        VerifToken verifToken = new VerifToken(token, savedUser);
        verifTokenRepository.save(verifToken);

        String emailBody = "<p>Dear " + savedUser.getFirstname() + ",</p>"
                + "<p>Thank you for registering. Please use the following token to verify your email address:</p>"
                + "<h3>" + token + "</h3>"
                + "<p>Best regards,<br/>Momenta Hotels Team</p>";

        emailSender.sendEmail(savedUser.getEmail(), emailBody);
        
        return savedUser;
    }

    @Override
    public User updateUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public User validateToken(String token) {
        VerifToken verifToken = verifTokenRepository.findByToken(token);
        if (verifToken == null) {
            throw new InvalidTokenException("Invalid Token");
        }

        User user = verifToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((verifToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            verifTokenRepository.delete(verifToken);
            throw new ExpiredTokenException("Token has expired");
        }

        user.setEnabled(true);
        return userRepository.save(user);
    }

    public String generateToken() {
        Random random = new Random();
        Integer token = 100000 + random.nextInt(900000);
        return token.toString();
    }

}
