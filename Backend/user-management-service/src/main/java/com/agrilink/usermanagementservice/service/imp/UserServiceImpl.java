
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
            CreateProfileRequest request
    ) {

        if (repository.existsByKeycloakId(keycloakId)) {
            throw new ProfileAlreadyExistsException(
                    "Un profil existe deja pour ce compte"
            );
        }

        if (!SELF_ASSIGNABLE.contains(request.userRole())) {
            throw new ForbiddenRoleException(
                    "Ce role ne peut pas etre choisi a l'inscription"
            );
        }

        User user = new User(
                keycloakId,
                request.firstName().trim(),
                request.lastName().trim(),
                request.phone(),
                request.address(),
                request.userRole()
        );

        return UserMapper.toResponse(repository.save(user));
    }

    // ==========================================
    // CREATION DES RESPONSABLES PAR ADMIN
    // ==========================================

    @Override
    public UserResponse createManagedUser(
            AdminCreateUserRequest request
    ) {

        if (!ADMIN_ASSIGNABLE.contains(request.userRole())) {
            throw new ForbiddenRoleException(
                    "ADMIN peut creer uniquement "
                            + "RESPONSABLE_COOPERATIVE ou RESPONSABLE_QUALITE"
            );
        }

        // Creer le compte dans Keycloak
        String keycloakId = keycloakAdminService.createUser(
                request.username().trim(),
                request.email().trim(),
                request.firstName().trim(),
                request.lastName().trim(),
                request.password()
        );

        try {

            // Attribuer le Realm Role dans Keycloak
            keycloakAdminService.assignRealmRole(
                    keycloakId,
                    request.userRole().name()
            );

            // Creer le profil dans H2
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

            // Compensation : supprimer le compte Keycloak
            // si l'attribution du role ou la sauvegarde H2 echoue

            try {
                keycloakAdminService.deleteUser(keycloakId);
            } catch (RuntimeException cleanupException) {
                e.addSuppressed(cleanupException);
            }

            throw e;
        }
    }

    // ==========================================
    // PROFIL PERSONNEL
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyProfile(String keycloakId) {

        return UserMapper.toResponse(
                findByKeycloakId(keycloakId)
        );
    }

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
    // ADMINISTRATION
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

    // SUPPRESSION D'UN UTILISATEUR PAR ADMIN
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

            if (role != null) {
                predicates.add(
                        cb.equal(root.get("userRole"), role)
                );
            }

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
    // STATISTIQUES
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
    // COMMUNICATION AVEC LES AUTRES MS
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public UserSummaryResponse getSummary(String keycloakId) {

        return UserMapper.toSummary(
                findByKeycloakId(keycloakId)
        );
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
