package com.project.users_microservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.users_microservice.entities.User;
import com.project.users_microservice.services.UserService;

@Service
public class UserLoginService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        String email = username;
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Email not found"); //lance une exception si l'utilisateur n'existe pas
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name()); //Spring Security attend que les rôles commencent par "ROLE_", par convention

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, java.util.Collections.singletonList(authority));
        //true pour accountNonExpired, credentialsNonExpired, accountNonLocked
        //si isEnabled() est false, Spring Security refusera l'authentification (compte désactivé) et lancera une DisabledException
    }

}
/*
Cette classe implémente UserDetailsService, un composant clé de Spring Security pour charger les détails d'un utilisateur lors de l'authentification.
La méthode loadUserByUsername récupère un utilisateur par son email, vérifie son existence, crée une GrantedAuthority basée sur son rôle, et retourne un objet UserDetails contenant les informations nécessaires pour l'authentification.
C'est elle qui est appelée quand, dans JWTAuthenticationFilter, on fait appel à authenticationManager.authenticate() pour vérifier les identifiants de l'utilisateur (new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())).
Spring va dire : "ok, je vais appeler loadUserByUsername avec l'email pour récupérer les infos de l'utilisateur"
*/

/*
1- Le front envoie une requête POST à /login avec le corps JSON contenant l'email et le mot de passe de l'utilisateur.
2- Le filtre JWTAuthenticationFilter intercepte cette requête et appelle attemptAuthentication().
- attemptAuthentication() extrait les informations d'identification du corps de la requête, crée un UsernamePasswordAuthenticationToken, et appelle authenticationManager.authenticate() pour vérifier les identifiants.
3- Spring Security utilise UserLoginService (implémentation de UserDetailsService) pour charger les détails de l'utilisateur via loadUserByUsername().
4- Si les identifiants sont corrects, successfulAuthentication() est appelé, générant un JWT avec les informations de l'utilisateur et le renvoyant dans l'en-tête de la réponse.
5- Pour les requêtes suivantes, le front inclut le JWT dans l'en-tête Authorization.
6- Le filtre JWTAuthorizationFilter intercepte chaque requête, extrait et vérifie le JWT, et si valide, crée un objet Authentication qu'il place dans le contexte de sécurité de Spring Security.
7- Spring Security utilise cet objet Authentication pour autoriser l'accès aux ressources protégées en fonction des rôles de l'utilisateur.
*/
