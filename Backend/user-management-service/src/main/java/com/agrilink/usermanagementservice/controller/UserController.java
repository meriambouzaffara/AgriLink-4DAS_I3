
package com.agrilink.usermanagementservice.controller;

import com.agrilink.usermanagementservice.dto.request.AdminCreateUserRequest;
import com.agrilink.usermanagementservice.dto.request.CreateProfileRequest;
import com.agrilink.usermanagementservice.dto.request.UpdateProfileRequest;

import com.agrilink.usermanagementservice.dto.response.PageResponse;
import com.agrilink.usermanagementservice.dto.response.UserResponse;
import com.agrilink.usermanagementservice.dto.response.UserStatisticsResponse;
import com.agrilink.usermanagementservice.dto.response.UserSummaryResponse;

import com.agrilink.usermanagementservice.enumeration.UserRole;
import com.agrilink.usermanagementservice.security.CurrentUser;
import com.agrilink.usermanagementservice.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Gestion des profils utilisateurs AgriLink")
public class UserController {

    private final UserService service;
    private final CurrentUser currentUser;

    public UserController(
            UserService service,
            CurrentUser currentUser
    ) {
        this.service = service;
        this.currentUser = currentUser;
    }

    // ==========================================
    // UTILISATEUR AUTHENTIFIE
    // ==========================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createMyProfile(
            @Valid @RequestBody CreateProfileRequest request
    ) {
        return service.createMyProfile(
                currentUser.keycloakId(),
                currentUser.roles(),
                request
        );
    }

    @GetMapping("/me")
    public UserResponse getMyProfile() {

        return service.getMyProfile(
                currentUser.keycloakId()
        );
    }

    @PutMapping("/me")
    public UserResponse updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return service.updateMyProfile(
                currentUser.keycloakId(),
                request
        );
    }

    // ==========================================
    // COMMUNICATION AVEC LES AUTRES MS
    // ==========================================

    @GetMapping("/keycloak/{keycloakId}")
    public UserSummaryResponse getSummary(
            @PathVariable String keycloakId
    ) {
        return service.getSummary(keycloakId);
    }

    // ==========================================
    // ADMINISTRATION
    // ==========================================

    // Creer un responsable
    @PostMapping("/admin/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createManagedUser(
            @Valid @RequestBody AdminCreateUserRequest request
    ) {
        return service.createManagedUser(request);
    }

    // Lister et rechercher les utilisateurs
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        return PageResponse.from(
                service.search(
                        name,
                        role,
                        PageRequest.of(
                                safePage,
                                safeSize,
                                Sort.by("lastName").ascending()
                        )
                )
        );
    }

    // Statistiques
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public UserStatisticsResponse statistics() {

        return service.statistics();
    }

    // Consulter un utilisateur
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getById(
            @PathVariable Long id
    ) {
        return service.getById(id);
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(
            @PathVariable Long id
    ) {
        service.deleteUser(
                id,
                currentUser.keycloakId()
        );
    }

    // Activer un utilisateur
    @Operation(
            summary = "Activer un profil utilisateur",
            description = "Active le profil utilisateur dans H2 et le compte correspondant dans Keycloak. "
                    + "Retourne une erreur 400 si le profil est deja actif. "
                    + "Necessite le role ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil active avec succes"),
            @ApiResponse(responseCode = "400", description = "Profil deja actif ou operation invalide",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Non authentifie",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Role ADMIN requis",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "502", description = "Echec de synchronisation avec Keycloak",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse activateUser(
            @Parameter(description = "Identifiant interne (H2) de l'utilisateur", required = true)
            @PathVariable Long id
    ) {
        return service.activateUser(id, currentUser.keycloakId());
    }

    // Desactiver un utilisateur
    @Operation(
            summary = "Desactiver un profil utilisateur",
            description = "Desactive le profil utilisateur dans H2 et le compte correspondant dans Keycloak. "
                    + "Retourne une erreur 400 si le profil est deja inactif. "
                    + "L'ADMIN ne peut pas desactiver son propre compte. "
                    + "Necessite le role ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil desactive avec succes"),
            @ApiResponse(responseCode = "400", description = "Profil deja inactif ou operation invalide",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Non authentifie",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Role ADMIN requis",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "502", description = "Echec de synchronisation avec Keycloak",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse deactivateUser(
            @Parameter(description = "Identifiant interne (H2) de l'utilisateur", required = true)
            @PathVariable Long id
    ) {
        return service.deactivateUser(id, currentUser.keycloakId());
    }
}
