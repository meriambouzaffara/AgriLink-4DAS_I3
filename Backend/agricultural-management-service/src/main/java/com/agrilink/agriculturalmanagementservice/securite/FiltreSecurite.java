package com.agrilink.agriculturalmanagementservice.securite;

import com.agrilink.agriculturalmanagementservice.dto.UtilisateurDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtre HTTP interceptant les en-têtes d'authentification transmis par la passerelle API Gateway
 * (ou envoyés via Postman lors des tests) pour initialiser le contexte de sécurité local.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FiltreSecurite extends OncePerRequestFilter {

    public static final String ENTETE_UTILISATEUR_ID = "X-User-Id";
    public static final String ENTETE_UTILISATEUR_ROLE = "X-User-Role";
    public static final String ENTETE_UTILISATEUR_ID_ALT = "X-Utilisateur-Id";
    public static final String ENTETE_UTILISATEUR_ROLE_ALT = "X-Utilisateur-Role";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String userIdHeader = request.getHeader(ENTETE_UTILISATEUR_ID);
            if (userIdHeader == null || userIdHeader.isBlank()) {
                userIdHeader = request.getHeader(ENTETE_UTILISATEUR_ID_ALT);
            }

            String userRoleHeader = request.getHeader(ENTETE_UTILISATEUR_ROLE);
            if (userRoleHeader == null || userRoleHeader.isBlank()) {
                userRoleHeader = request.getHeader(ENTETE_UTILISATEUR_ROLE_ALT);
            }

            if (userIdHeader != null && !userIdHeader.isBlank() && userRoleHeader != null && !userRoleHeader.isBlank()) {
                try {
                    UUID userId = UUID.fromString(userIdHeader.trim());
                    UtilisateurDTO utilisateur = new UtilisateurDTO(userId, userRoleHeader.trim());
                    ContexteSecurite.definirUtilisateurConnecte(utilisateur);
                } catch (IllegalArgumentException ignored) {
                    // Si l'UUID est invalide, aucun utilisateur n'est positionné
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            ContexteSecurite.reinitialiser();
        }
    }
}
