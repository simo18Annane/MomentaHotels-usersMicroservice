package com.project.users_microservice.security;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//OncePerRequestFilter garantit que le filtre est exécuté une seule fois par requête HTTP, parfait pour un filtre d'autorisation stateless (pas de session serveur).
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        //On récupère le JWT dans l'en-tête Authorization
        String jwt = request.getHeader("Authorization");
        if (jwt == null || !jwt.startsWith(SecParams.PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        //On vérifie le JWT et on decode les informations
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecParams.SECRET)).build();
        jwt = jwt.substring(SecParams.PREFIX.length()); //on enlève le préfixe "Bearer "

        DecodedJWT decodedJWT = verifier.verify(jwt); //si le token n'est pas valide, une exception est lancée ici et la requête est bloquée
        String username = decodedJWT.getSubject(); //on récupère le nom d'utilisateur(email)
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class); //on récupère la liste des rôles qu'on avait mise lors de la génération du token

        //construction de l'objet Authentication avec les infos extraites du token
        Collection <GrantedAuthority> authorities = new java.util.ArrayList<>(); //Spring Security utilise des GrantedAuthority pour représenter les rôles, il n'utilise pas des simples String

        for (String r : roles) {
            authorities.add(new SimpleGrantedAuthority(r)); //on convertit chaque rôle String en SimpleGrantedAuthority
        }

        //Injection de l'objet Authentication dans le contexte de sécurité de Spring Security
        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(username, null, authorities); //on crée l'objet Authentication avec le nom d'utilisateur, pas de mot de passe (null) et les rôles
        SecurityContextHolder.getContext().setAuthentication(user); //à partir de là, Spring Security sait que l'utilisateur est authentifié pour cette requête
        filterChain.doFilter(request, response); //on passe la requête au filtre suivant de la chaîne
        
    }

}

/*
Il est exécuté à chaque requête (sauf /login qui est gérée par l'autre filtre JWTAuthenticationFilter)
Il récupère le JWT dans l'en-tête Authorization, le vérifie, extrait le nom d'utilisateur et les rôles, puis crée un objet Authentication qu'il place dans le contexte de sécurité de Spring Security.
Grâce à cela, les autres parties de l'application peuvent savoir quel utilisateur fait la requête et quels sont ses rôles.
*/