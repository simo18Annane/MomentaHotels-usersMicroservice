package com.project.users_microservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.users_microservice.entities.VerifToken;

public interface VerifTokenRepository extends JpaRepository<VerifToken, Long> {
    
    VerifToken findByToken(String token);

}
