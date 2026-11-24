package com.agrilink.agriculturalmanagementservice.securite;

import com.agrilink.agriculturalmanagementservice.dto.UtilisateurDTO;

/**
 * Gestionnaire de thread-local pour conserver les informations
 * de l'utilisateur connecté durant le cycle de vie de la requête.
 */
public final class ContexteSecurite {

    private static final ThreadLocal<UtilisateurDTO> UTILISATEUR_CONNECTE = new ThreadLocal<>();

    private ContexteSecurite() {
    }

    public static void definirUtilisateurConnecte(UtilisateurDTO utilisateur) {
        UTILISATEUR_CONNECTE.set(utilisateur);
    }

    public static UtilisateurDTO obtenirUtilisateurConnecte() {
        return UTILISATEUR_CONNECTE.get();
    }

    public static void reinitialiser() {
        UTILISATEUR_CONNECTE.remove();
    }
}
