
package com.agrilink.usermanagementservice.service.imp;

import com.agrilink.usermanagementservice.dto.request.AdminCreateUserRequest;
import com.agrilink.usermanagementservice.dto.request.CreateProfileRequest;
import com.agrilink.usermanagementservice.dto.request.UpdateProfileRequest;

import com.agrilink.usermanagementservice.dto.response.UserResponse;
import com.agrilink.usermanagementservice.dto.response.UserStatisticsResponse;
import com.agrilink.usermanagementservice.dto.response.UserSummaryResponse;

import com.agrilink.usermanagementservice.entity.User;
import com.agrilink.usermanagementservice.enumeration.UserRole;
import com.agrilink.usermanagementservice.enumeration.UserStatus;

import com.agrilink.usermanagementservice.exception.ForbiddenRoleException;
import com.agrilink.usermanagementservice.exception.ProfileAlreadyExistsException;
import com.agrilink.usermanagementservice.exception.UserNotFoundException;

import com.agrilink.usermanagementservice.keycloak.KeycloakAdminService;
import com.agrilink.usermanagementservice.mapper.UserMapper;
import com.agrilink.usermanagementservice.repository.UserRepository;
import com.agrilink.usermanagementservice.service.UserService;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    // Roles autorises pour l'inscription personnelle
    private static final Set<UserRole> SELF_ASSIGNABLE =
            EnumSet.of(
                    UserRole.CLIENT,
                    UserRole.AGRICULTEUR,
                    UserRole.TRANSPORTEUR
            );

    // Roles que seul ADMIN peut creer
    private static final Set<UserRole> ADMIN_ASSIGNABLE =
            EnumSet.of(
                    UserRole.RESPONSABLE_COOPERATIVE,
                    UserRole.RESPONSABLE_QUALITE
            );

    private final UserRepository repository;
    private final KeycloakAdminService keycloakAdminService;

    // Constructeur
    public UserServiceImpl(
            UserRepository repository,
            KeycloakAdminService keycloakAdminService
    ) {
        this.repository = repository;
        this.keycloakAdminService = keycloakAdminService;
    }

    // ==========================================
    // INSCRIPTION PERSONNELLE
    // ==========================================

    @Override
    public UserResponse createMyProfile(
            String keycloakId,
            Set<String> callerRoles,
            CreateProfileRequest request
    ) {

        // 1. Verifier si le profil existe deja
        if (repository.existsByKeycloakId(keycloakId)) {
            throw new ProfileAlreadyExistsException(
                    "Un profil existe deja pour ce compte"
            );
        }

        // 2. Recuperer le role demande
        UserRole requestedRole = request.userRole();

        // 3. Verifier que le role est autorise pour l'inscription
        if (requestedRole == null ||
                !SELF_ASSIGNABLE.contains(requestedRole)) {

            throw new ForbiddenRoleException(
                    "Ce role ne peut pas etre choisi a l'inscription"
            );
        }

        // 4. Verifier que le role est present dans le JWT Keycloak
        if (!hasRole(callerRoles, requestedRole)) {

            throw new ForbiddenRoleException(
                    "Le role choisi ne correspond pas aux roles "
                            + "attribues a votre compte Keycloak"
            );
        }

        // 5. Creer le profil dans H2
        User user = new User(
                keycloakId,
                request.firstName().trim(),
                request.lastName().trim(),
                request.phone(),
                request.address(),
                requestedRole
        );

        return UserMapper.toResponse(
                repository.save(user)
        );
    }

    // Verifier le role du JWT
    private boolean hasRole(
            Set<String> callerRoles,
            UserRole requestedRole
    ) {

        if (callerRoles == null || requestedRole == null) {
            return false;
        }

        String expectedRole = requestedRole.name();

        return callerRoles.stream()
                .filter(role -> role != null)
                .map(role -> role.startsWith("ROLE_")
                        ? role.substring(5)
                        : role)
                .anyMatch(role ->
                        role.equalsIgnoreCase(expectedRole)
                );
    }

    // ==========================================
    // CREATION DES RESPONSABLES PAR ADMIN
    // ==========================================

    @Override
    public UserResponse createManagedUser(
            AdminCreateUserRequest request
    ) {

        // Seuls ces deux roles peuvent etre crees par ADMIN
        if (request.userRole() == null ||
                !ADMIN_ASSIGNABLE.contains(request.userRole())) {

            throw new ForbiddenRoleException(
                    "ADMIN peut creer uniquement "
                            + "RESPONSABLE_COOPERATIVE ou RESPONSABLE_QUALITE"
            );
        }

        // 1. Creer le compte dans Keycloak
        String keycloakId = keycloakAdminService.createUser(
                request.username().trim(),
                request.email().trim(),
                request.firstName().trim(),
                request.lastName().trim(),
                request.password()
        );

        try {

            // 2. Attribuer le Realm Role dans Keycloak
            keycloakAdminService.assignRealmRole(
                    keycloakId,
                    request.userRole().name()
            );

            // 3. Creer le profil dans H2
            User user = new User(
                    keycloakId,
                    request.firstName().trim(),
                    request.lastName().trim(),
                    request.phone(),
                    request.address(),
                    request.userRole()
            );

            User savedUser = repository.saveAndFlush(user);

            return UserMapper.toResponse(savedUser);

        } catch (RuntimeException e) {

            // Compensation :
            // supprimer le compte Keycloak si la sauvegarde H2 echoue
            try {
                keycloakAdminService.deleteUser(keycloakId);

            } catch (RuntimeException cleanupException) {
                e.addSuppressed(cleanupException);
            }

            throw e;
        }
    }

    // ==========================================
    // CONSULTATION DU PROFIL PERSONNEL
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyProfile(String keycloakId) {

        User user = findByKeycloakId(keycloakId);

        return UserMapper.toResponse(user);
    }

    // ==========================================
    // MODIFICATION DU PROFIL PERSONNEL
    // ==========================================

    @Override
    public UserResponse updateMyProfile(
            String keycloakId,
            UpdateProfileRequest request
    ) {

        User user = findByKeycloakId(keycloakId);

        user.updatePersonalInfo(
                request.firstName().trim(),
                request.lastName().trim(),
                request.phone(),
                request.address()
        );

        return UserMapper.toResponse(
                repository.saveAndFlush(user)
        );
    }

    // ==========================================
    // CONSULTER UN UTILISATEUR PAR ID
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Utilisateur introuvable : " + id
                        )
                );

        return UserMapper.toResponse(user);
    }

    // ==========================================
    // SUPPRESSION D'UN UTILISATEUR PAR ADMIN
    // ==========================================

    @Override
    public void deleteUser(
            Long id,
            String currentAdminKeycloakId
    ) {

        // 1. Chercher le profil dans H2
        User user = repository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Utilisateur introuvable : " + id
                        )
                );

        // 2. Empecher ADMIN de supprimer son propre compte
        if (user.getKeycloakId().equals(currentAdminKeycloakId)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vous ne pouvez pas supprimer votre propre compte"
            );
        }

        // 3. Supprimer le compte dans Keycloak
        keycloakAdminService.deleteUser(user.getKeycloakId());

        // 4. Supprimer le profil dans H2
        repository.delete(user);
        repository.flush();
    }

    // ==========================================
    // ACTIVATION D'UN UTILISATEUR PAR ADMIN
    // ==========================================

    @Override
    public UserResponse activateUser(Long id, String currentAdminKeycloakId) {

        // 1. Verifier que l'utilisateur existe dans H2
        User user = repository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Utilisateur introuvable : " + id
                        )
                );

        // 2. Proteger l'ADMIN contre la modification de son propre statut
        if (user.getKeycloakId().equals(currentAdminKeycloakId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vous ne pouvez pas modifier le statut de votre propre compte"
            );
        }

        // 3. Verifier que le statut actuel est INACTIVE
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le profil est deja actif"
            );
        }

        // 4. Activer le compte dans Keycloak (en premier)
        //    Si Keycloak echoue, H2 n'est pas modifie.
        keycloakAdminService.setUserEnabled(user.getKeycloakId(), true);

        // 5. Mettre a jour le statut dans H2 apres le succes Keycloak
        try {
            user.activate();
            User saved = repository.saveAndFlush(user);
            return UserMapper.toResponse(saved);

        } catch (RuntimeException dbException) {
            // Keycloak a ete active mais H2 a echoue : inconsistance detectee.
            // On tente de retablir Keycloak a son etat precedent (compensation).
            try {
                keycloakAdminService.setUserEnabled(user.getKeycloakId(), false);
            } catch (RuntimeException compensationException) {
                dbException.addSuppressed(compensationException);
            }
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Le compte Keycloak a ete active mais la mise a jour "
                            + "du profil local a echoue. "
                            + "Une compensation a ete tentee. "
                            + "Veuillez contacter l'administrateur.",
                    dbException
            );
        }
    }

    // ==========================================
    // DESACTIVATION D'UN UTILISATEUR PAR ADMIN
    // ==========================================

    @Override
    public UserResponse deactivateUser(Long id, String currentAdminKeycloakId) {

        // 1. Verifier que l'utilisateur existe dans H2
        User user = repository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Utilisateur introuvable : " + id
                        )
                );

        // 2. Proteger l'ADMIN contre la desactivation de son propre compte
        if (user.getKeycloakId().equals(currentAdminKeycloakId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vous ne pouvez pas desactiver votre propre compte"
            );
        }

        // 3. Verifier que le statut actuel est ACTIVE
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le profil est deja inactif"
            );
        }

        // 4. Desactiver le compte dans Keycloak (en premier)
        //    Si Keycloak echoue, H2 n'est pas modifie.
        keycloakAdminService.setUserEnabled(user.getKeycloakId(), false);

        // 5. Mettre a jour le statut dans H2 apres le succes Keycloak
        try {
            user.deactivate();
            User saved = repository.saveAndFlush(user);
            return UserMapper.toResponse(saved);

        } catch (RuntimeException dbException) {
            // Keycloak a ete desactive mais H2 a echoue : inconsistance detectee.
            // On tente de retablir Keycloak a son etat precedent (compensation).
            try {
                keycloakAdminService.setUserEnabled(user.getKeycloakId(), true);
            } catch (RuntimeException compensationException) {
                dbException.addSuppressed(compensationException);
            }
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Le compte Keycloak a ete desactive mais la mise a jour "
                            + "du profil local a echoue. "
                            + "Une compensation a ete tentee. "
                            + "Veuillez contacter l'administrateur.",
                    dbException
            );
        }
    }

    // ==========================================
    // RECHERCHE DES UTILISATEURS
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> search(
            String name,
            UserRole role,
            Pageable pageable
    ) {

        String term = (name == null || name.isBlank())
                ? null
                : name.trim().toLowerCase();

        Specification<User> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Filtrage par role
            if (role != null) {
                predicates.add(
                        cb.equal(root.get("userRole"), role)
                );
            }

            // Recherche par nom ou prenom
            if (term != null) {

                String like = "%" + term + "%";

                predicates.add(
                        cb.or(
                                cb.like(
                                        cb.lower(root.get("firstName")),
                                        like
                                ),
                                cb.like(
                                        cb.lower(root.get("lastName")),
                                        like
                                )
                        )
                );
            }

            return cb.and(
                    predicates.toArray(new Predicate[0])
            );
        };

        return repository.findAll(spec, pageable)
                .map(UserMapper::toResponse);
    }

    // ==========================================
    // STATISTIQUES DES UTILISATEURS
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public UserStatisticsResponse statistics() {

        Map<UserRole, Long> byRole =
                new EnumMap<>(UserRole.class);

        for (UserRole role : UserRole.values()) {

            byRole.put(
                    role,
                    repository.countByUserRole(role)
            );
        }

        return new UserStatisticsResponse(
                repository.count(),
                byRole
        );
    }

    // ==========================================
    // COMMUNICATION AVEC LES AUTRES MICROSERVICES
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public UserSummaryResponse getSummary(String keycloakId) {

        User user = findByKeycloakId(keycloakId);

        return UserMapper.toSummary(user);
    }

    // ==========================================
    // METHODE PRIVEE
    // ==========================================

    private User findByKeycloakId(String keycloakId) {

        return repository.findByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Profil introuvable pour ce compte"
                        )
                );
    }
}
