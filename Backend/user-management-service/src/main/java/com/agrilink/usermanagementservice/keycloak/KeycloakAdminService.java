
package com.agrilink.usermanagementservice.keycloak;

import com.agrilink.usermanagementservice.exception.KeycloakException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakAdminService {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {};

    private final KeycloakProperties properties;
    private final RestClient restClient;

    public KeycloakAdminService(
            KeycloakProperties properties,
            RestClient.Builder builder
    ) {
        this.properties = properties;

        this.restClient = builder
                .baseUrl(properties.baseUrl())
                .build();
    }

    /**
     * Obtient un access token pour le compte technique
     * en utilisant le flux OAuth2 client_credentials.
     */
    private String getAdminToken() {

        if (properties.clientSecret() == null ||
                properties.clientSecret().isBlank()) {
            throw new KeycloakException(
                    "Le secret du client Keycloak n'est pas configure."
            );
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("grant_type", "client_credentials");
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());

        try {
            Map<String, Object> response = restClient.post()
                    .uri(
                            "/realms/{realm}/protocol/openid-connect/token",
                            properties.realm()
                    )
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(MAP_TYPE);

            if (response == null || response.get("access_token") == null) {
                throw new KeycloakException(
                        "Keycloak n'a pas retourne de token d'administration."
                );
            }

            return String.valueOf(response.get("access_token"));

        } catch (RestClientException e) {
            throw new KeycloakException(
                    "Impossible d'obtenir un token d'administration Keycloak.",
                    e
            );
        }
    }

    /**
     * Cree un utilisateur dans Keycloak.
     * Retourne son identifiant Keycloak (keycloakId).
     */
    public String createUser(
            String username,
            String email,
            String firstName,
            String lastName,
            String password
    ) {

        Map<String, Object> body = Map.of(
                "username", username,
                "email", email,
                "firstName", firstName,
                "lastName", lastName,
                "enabled", true,
                "emailVerified", false,
                "credentials", List.of(
                        Map.of(
                                "type", "password",
                                "value", password,
                                "temporary", false
                        )
                )
        );

        String token = getAdminToken();

        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri("/admin/realms/{realm}/users", properties.realm())
                    .headers(headers -> headers.setBearerAuth(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            URI location = response.getHeaders().getLocation();

            if (location == null || location.getPath() == null) {
                throw new KeycloakException(
                        "Le compte a ete cree, mais son identifiant est introuvable."
                );
            }

            String path = location.getPath();
            String userId = path.substring(path.lastIndexOf('/') + 1);

            if (userId.isBlank()) {
                throw new KeycloakException(
                        "Identifiant Keycloak invalide."
                );
            }

            return userId;

        } catch (HttpClientErrorException.Conflict e) {
            throw new KeycloakException(
                    "Un compte avec ce username ou cet email existe deja dans Keycloak.",
                    e
            );

        } catch (RestClientException e) {
            throw new KeycloakException(
                    "Erreur lors de la creation du compte Keycloak.",
                    e
            );
        }
    }

    /**
     * Attribue un role Realm existant a un utilisateur Keycloak.
     * Exemple : CLIENT, AGRICULTEUR, TRANSPORTEUR.
     */
    public void assignRealmRole(String userId, String roleName) {

        String token = getAdminToken();

        try {
            Map<String, Object> role = restClient.get()
                    .uri(
                            "/admin/realms/{realm}/roles/{roleName}",
                            properties.realm(),
                            roleName
                    )
                    .headers(headers -> headers.setBearerAuth(token))
                    .retrieve()
                    .body(MAP_TYPE);

            if (role == null || role.get("id") == null) {
                throw new KeycloakException(
                        "Le role " + roleName + " n'existe pas dans Keycloak."
                );
            }

            restClient.post()
                    .uri(
                            "/admin/realms/{realm}/users/{userId}/role-mappings/realm",
                            properties.realm(),
                            userId
                    )
                    .headers(headers -> headers.setBearerAuth(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(List.of(role))
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpClientErrorException.NotFound e) {
            throw new KeycloakException(
                    "Utilisateur ou role introuvable dans Keycloak.",
                    e
            );

        } catch (RestClientException e) {
            throw new KeycloakException(
                    "Impossible d'attribuer le role " + roleName + ".",
                    e
            );
        }
    }

    /**
     * Supprime un utilisateur de Keycloak.
     */
    public void deleteUser(String userId) {

        String token = getAdminToken();

        try {
            restClient.delete()
                    .uri(
                            "/admin/realms/{realm}/users/{userId}",
                            properties.realm(),
                            userId
                    )
                    .headers(headers -> headers.setBearerAuth(token))
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpClientErrorException.NotFound e) {
            // L'utilisateur n'existe deja plus : aucune action necessaire.

        } catch (RestClientException e) {
            throw new KeycloakException(
                    "Impossible de supprimer le compte Keycloak.",
                    e
            );
        }
    }

    /**
     * Active ou desactive un compte utilisateur dans Keycloak
     * en mettant a jour le champ "enabled".
     *
     * @param userId  Identifiant Keycloak de l'utilisateur.
     * @param enabled true pour activer, false pour desactiver.
     * @throws KeycloakException si l'utilisateur est introuvable dans Keycloak
     *                           ou si Keycloak est inaccessible.
     */
    public void setUserEnabled(String userId, boolean enabled) {

        String token = getAdminToken();

        Map<String, Object> body = Map.of("enabled", enabled);

        try {
            restClient.put()
                    .uri(
                            "/admin/realms/{realm}/users/{userId}",
                            properties.realm(),
                            userId
                    )
                    .headers(headers -> headers.setBearerAuth(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpClientErrorException.NotFound e) {
            throw new KeycloakException(
                    "Utilisateur introuvable dans Keycloak (id=" + userId + ").",
                    e
            );

        } catch (RestClientException e) {
            throw new KeycloakException(
                    "Impossible de mettre a jour le statut du compte Keycloak.",
                    e
            );
        }
    }
}
