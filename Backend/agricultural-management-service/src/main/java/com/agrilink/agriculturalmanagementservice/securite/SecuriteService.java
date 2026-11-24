package com.agrilink.agriculturalmanagementservice.securite;

import com.agrilink.agriculturalmanagementservice.dto.UtilisateurDTO;
import com.agrilink.agriculturalmanagementservice.exception.AccesInterditException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecuriteService {

    public static final String ROLE_AGRICULTEUR = "AGRICULTEUR";

    /**
     * Récupère l'utilisateur connecté depuis le contexte de sécurité.
     *
     * @return UtilisateurDTO
     * @throws AccesInterditException si aucun utilisateur n'est authentifié
     */
    public UtilisateurDTO obtenirUtilisateurConnecte() {
        UtilisateurDTO utilisateur = ContexteSecurite.obtenirUtilisateurConnecte();
        if (utilisateur == null) {
            throw new AccesInterditException("Accès refusé : aucun utilisateur authentifié dans la requête");
        }
        return utilisateur;
    }

    /**
     * Vérifie que l'utilisateur connecté possède bien le rôle AGRICULTEUR.
     */
    public void validerRoleAgriculteur() {
        UtilisateurDTO utilisateur = obtenirUtilisateurConnecte();
        String role = utilisateur.role();
        if (role == null) {
            throw new AccesInterditException("Accès refusé : rôle utilisateur non défini");
        }

        String roleNormalise = role.toUpperCase().replace("ROLE_", "");
        if (!ROLE_AGRICULTEUR.equalsIgnoreCase(roleNormalise)) {
            throw new AccesInterditException("Accès refusé : rôle AGRICULTEUR requis pour cette opération");
        }
    }

    /**
     * Vérifie que l'utilisateur connecté est un AGRICULTEUR et qu'il est bien
     * le propriétaire de la ressource concernée.
     *
     * @param agriculteurIdAttendu l'identifiant de l'agriculteur propriétaire de la ressource
     */
    public void validerAccesAgriculteur(UUID agriculteurIdAttendu) {
        validerRoleAgriculteur();
        UtilisateurDTO utilisateur = obtenirUtilisateurConnecte();
        if (agriculteurIdAttendu == null || !agriculteurIdAttendu.equals(utilisateur.id())) {
            throw new AccesInterditException("Accès refusé : vous ne pouvez manipuler que vos propres ressources");
        }
    }
}
