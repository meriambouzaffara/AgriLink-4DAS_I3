
package com.agrilink.usermanagementservice.service.imp;

import com.agrilink.usermanagementservice.dto.request.AdminCreateUserRequest;
import com.agrilink.usermanagementservice.dto.request.CreateProfileRequest;
import com.agrilink.usermanagementservice.dto.request.UpdateProfileRequest;

import com.agrilink.usermanagementservice.dto.response.UserResponse;
import com.agrilink.usermanagementservice.dto.response.UserStatisticsResponse;
import com.agrilink.usermanagementservice.dto.response.UserSummaryResponse;

import com.agrilink.usermanagementservice.entity.User;
import com.agrilink.usermanagementservice.enumeration.UserRole;

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
