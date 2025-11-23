package com.project.users_microservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //le unique = true fait normalement créer un index unique sur la colonne email
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private boolean enabled; //boolean primitif, valeurs possibles: true ou false; Boolean objet, valeurs possibles: true, false ou null
    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String country;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private RoleType role = RoleType.CUSTOMER;

    public enum RoleType{
        ADMIN, EMPLOYEE, CUSTOMER
    }

}
