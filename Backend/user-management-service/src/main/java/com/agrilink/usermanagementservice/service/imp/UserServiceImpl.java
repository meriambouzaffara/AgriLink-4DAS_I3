package com.agrilink.usermanagementservice.service.imp;

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
import com.agrilink.usermanagementservice.mapper.UserMapper;
import com.agrilink.usermanagementservice.repository.UserRepository;
import com.agrilink.usermanagementservice.service.UserService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    // Roles qu'un utilisateur peut choisir seul a l'inscription (a valider avec l'equipe)
    private static final Set<UserRole> SELF_ASSIGNABLE =
            EnumSet.of(UserRole.CLIENT, UserRole.AGRICULTEUR);

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserResponse createMyProfile(String keycloakId, CreateProfileRequest request) {
        if (repository.existsByKeycloakId(keycloakId)) {
            throw new ProfileAlreadyExistsException("Un profil existe deja pour ce compte");
        }
        if (!SELF_ASSIGNABLE.contains(request.userRole())) {
            throw new ForbiddenRoleException("Ce role ne peut pas etre choisi a l'inscription");
        }
        User user = new User(keycloakId, request.firstName().trim(), request.lastName().trim(),
                request.phone(), request.address(), request.userRole());
        return UserMapper.toResponse(repository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyProfile(String keycloakId) {
        return UserMapper.toResponse(findByKeycloakId(keycloakId));
    }

    @Override
    public UserResponse updateMyProfile(String keycloakId, UpdateProfileRequest request) {
        User user = findByKeycloakId(keycloakId);
        user.updatePersonalInfo(request.firstName().trim(), request.lastName().trim(),
                request.phone(), request.address());
        return UserMapper.toResponse(repository.saveAndFlush(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable : " + id));
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummaryResponse getSummary(String keycloakId) {
        return UserMapper.toSummary(findByKeycloakId(keycloakId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> search(String name, UserRole role, Pageable pageable) {
        String term = (name == null || name.isBlank()) ? null : name.trim().toLowerCase();

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (role != null) {
                predicates.add(cb.equal(root.get("userRole"), role));
            }
            if (term != null) {
                String like = "%" + term + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), like),
                        cb.like(cb.lower(root.get("lastName")), like)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec, pageable).map(UserMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatisticsResponse statistics() {
        Map<UserRole, Long> byRole = new EnumMap<>(UserRole.class);
        for (UserRole role : UserRole.values()) {
            byRole.put(role, repository.countByUserRole(role));
        }
        return new UserStatisticsResponse(repository.count(), byRole);
    }

    private User findByKeycloakId(String keycloakId) {
        return repository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException("Profil introuvable pour ce compte"));
    }
}