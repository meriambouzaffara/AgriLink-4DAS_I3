package com.agrilink.usermanagementservice.security;

import com.agrilink.usermanagementservice.exception.UnauthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CurrentUser {

    /** Identifiant Keycloak (claim "sub") de l'utilisateur connecte. */
    public String keycloakId() {
        return jwt().getToken().getSubject();
    }

    /** Roles Keycloak du jeton, sans le prefixe ROLE_ (ex. "CLIENT", "ADMIN"). */
    public Set<String> roles() {
        return jwt().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .collect(Collectors.toSet());
    }

    private JwtAuthenticationToken jwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken token) {
            return token;
        }
        throw new UnauthenticatedException("Authentification requise");
    }
}