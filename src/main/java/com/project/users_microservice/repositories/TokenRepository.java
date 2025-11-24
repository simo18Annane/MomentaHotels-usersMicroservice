package com.project.users_microservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.users_microservice.entities.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
    
    Token findByCode(String code);

}
