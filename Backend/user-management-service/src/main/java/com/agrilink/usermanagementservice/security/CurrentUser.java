package com.agrilink.usermanagementservice.security;

import com.agrilink.usermanagementservice.exception.UnauthenticatedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    private final HttpServletRequest request;

    // TEMPORAIRE : permet de tester avant l'installation de Keycloak.
    // A SUPPRIMER (propriete et bloc "devMode") a l'etape Keycloak.
    @Value("${app.security.dev-mode:false}")
    private boolean devMode;

    public CurrentUser(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Retourne l'identifiant Keycloak (le claim "sub")
     * de l'utilisateur connecté.
     */
    public String keycloakId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken token) {
            return token.getToken().getSubject();
        }
        if (devMode) {   // TEMPORAIRE
            String header = request.getHeader("X-Dev-Keycloak-Id");
            if (header != null && !header.isBlank()) {
                return header.trim();
            }
        }
        throw new UnauthenticatedException("Authentification requise");
    }
}