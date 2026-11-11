package com.agrilink.cooperative.service.impl;

import com.agrilink.cooperative.dto.request.CreateCooperativeRequest;
import com.agrilink.cooperative.dto.response.CooperativeResponse;
import com.agrilink.cooperative.dto.response.MembershipResponse;
import com.agrilink.cooperative.entity.Cooperative;
import com.agrilink.cooperative.exception.DuplicateResourceException;
import com.agrilink.cooperative.exception.ResourceNotFoundException;
import com.agrilink.cooperative.repository.CooperativeRepository;
import com.agrilink.cooperative.repository.MembershipRepository;
import com.agrilink.cooperative.service.CooperativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CooperativeServiceImpl implements CooperativeService {

    private final CooperativeRepository cooperativeRepository;
    private final MembershipRepository membershipRepository;

    @Override
    public CooperativeResponse createCooperative(CreateCooperativeRequest request) {
        if (cooperativeRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Une coopérative avec le nom '" + request.getName() + "' existe déjà.");
        }

        Cooperative cooperative = Cooperative.builder()
                .name(request.getName())
                .description(request.getDescription())
                .adminUserId(request.getAdminUserId())
                .location(request.getLocation())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .build();

        Cooperative saved = cooperativeRepository.save(cooperative);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CooperativeResponse> getAllCooperatives() {
        return cooperativeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CooperativeResponse getCooperativeById(Long id) {
        Cooperative cooperative = findCooperativeById(id);
        return toResponse(cooperative);
    }

    @Override
    public CooperativeResponse updateCooperative(Long id, CreateCooperativeRequest request) {
        Cooperative cooperative = findCooperativeById(id);

        if (!cooperative.getName().equals(request.getName())
                && cooperativeRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Une coopérative avec le nom '" + request.getName() + "' existe déjà.");
        }

        cooperative.setName(request.getName());
        cooperative.setDescription(request.getDescription());
        cooperative.setLocation(request.getLocation());
        cooperative.setContactEmail(request.getContactEmail());
        cooperative.setContactPhone(request.getContactPhone());

        return toResponse(cooperativeRepository.save(cooperative));
    }

    @Override
    public void deleteCooperative(Long id) {
        Cooperative cooperative = findCooperativeById(id);
        cooperativeRepository.delete(cooperative);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipResponse> getMembersByCooperativeId(Long cooperativeId) {
        findCooperativeById(cooperativeId);
        return membershipRepository.findByCooperativeId(cooperativeId).stream()
                .map(m -> MembershipResponse.builder()
                        .id(m.getId())
                        .cooperativeId(cooperativeId)
                        .cooperativeName(m.getCooperative().getName())
                        .farmerUserId(m.getFarmerUserId())
                        .joinedAt(m.getJoinedAt())
                        .build())
                .toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Cooperative findCooperativeById(Long id) {
        return cooperativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coopérative", id));
    }

    private CooperativeResponse toResponse(Cooperative c) {
        return CooperativeResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .adminUserId(c.getAdminUserId())
                .location(c.getLocation())
                .contactEmail(c.getContactEmail())
                .contactPhone(c.getContactPhone())
                .memberCount(c.getMemberships().size())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
