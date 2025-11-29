package com.project.users_microservice.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

// c'est la classe principale de configuration de la sécurité avec Spring Security
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    AuthenticationManager authenticationManager;

    /*ce bean dit à Spring Security
    - comment gérer les sessions,
    - quels endpoints sont publics/protégés,
    - quels filtres utiliser (JWTAuthenticationFilter, JWTAuthorizationFilter),
    - et comment gérer CORS.
    */
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //pour dire à Spring Security de ne pas créer de session HTTP (stateless), donc chaque requête doit être autonome -> donc JWT oblogatoire pour s'authentifier
            .csrf(csrf -> csrf.disable()) //le CSRF est surtout utile pour les applis web avec session (formulaires, cookies de session). Donc comme on est en JWT Stateless, on le désactive.
            .cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                    CorsConfiguration cors = new CorsConfiguration();
                    cors.setAllowedOrigins(Collections.singletonList("http://localhost:4200")); //pour autoriser plusieurs origines, utiliser Arrays.asList("http://localhost:4200", "http://example.com")
                    cors.setAllowedMethods(Collections.singletonList("*")); //pour autoriser tous les verbes HTTP (GET, POST, etc.), attention en production à restreindre cela
                    cors.setAllowedHeaders(Collections.singletonList("*"));
                    cors.setExposedHeaders(Collections.singletonList("Authorization")); //Très important car ça permet au front de voir le header Authorization dans la réponse sinon le navigateur le masque côté JS.
                    return cors;
                } //Grâce à ça, Angular pourra récupérer le JWT renvoyé dans le header Authorization après /login.
            }))

            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/login", "/register", "/checkToken").permitAll() //autoriser sans authentication; permitAll() ne veut pas dire pas de filtre, ça veut dire pas besoin d'utilisateur authentifié.
                .anyRequest().authenticated()) //authentication obligatoire, donc JWT nécessaire.
            .addFilterBefore(new JWTAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)//ajout du filtre d'authentification avant celui de Spring Security
            .addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class); //ajout du filtre d'autorisation avant celui de Spring Security, pour recupérer le token à chaque requête, le decoder et créer l'objet Authentication
            return http.build();
        
    }

}
