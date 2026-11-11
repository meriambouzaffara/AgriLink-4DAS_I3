package com.agrilink.cooperative.service.impl;

import com.agrilink.cooperative.dto.request.MembershipRequestDto;
import com.agrilink.cooperative.dto.response.MembershipRequestResponse;
import com.agrilink.cooperative.entity.*;
import com.agrilink.cooperative.exception.DuplicateResourceException;
import com.agrilink.cooperative.exception.ResourceNotFoundException;
import com.agrilink.cooperative.repository.CooperativeRepository;
import com.agrilink.cooperative.repository.MembershipRepository;
import com.agrilink.cooperative.repository.MembershipRequestRepository;
import com.agrilink.cooperative.service.MembershipRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipRequestServiceImpl implements MembershipRequestService {

    private final MembershipRequestRepository requestRepository;
    private final MembershipRepository membershipRepository;
    private final CooperativeRepository cooperativeRepository;

    @Override
    public MembershipRequestResponse submitRequest(Long cooperativeId, MembershipRequestDto dto) {
        Cooperative cooperative = cooperativeRepository.findById(cooperativeId)
                .orElseThrow(() -> new ResourceNotFoundException("Coopérative", cooperativeId));

        // Vérifier si l'agriculteur est déjà membre
        if (membershipRepository.existsByCooperativeIdAndFarmerUserId(cooperativeId, dto.getFarmerUserId())) {
            throw new DuplicateResourceException("L'agriculteur est déjà membre de cette coopérative.");
        }

        // Vérifier si une demande PENDING existe déjà
        if (requestRepository.existsByCooperativeIdAndFarmerUserIdAndStatus(
                cooperativeId, dto.getFarmerUserId(), MembershipRequestStatus.PENDING)) {
            throw new DuplicateResourceException("Une demande d'adhésion est déjà en cours de traitement.");
        }

        MembershipRequest membershipRequest = MembershipRequest.builder()
                .cooperative(cooperative)
                .farmerUserId(dto.getFarmerUserId())
                .message(dto.getMessage())
                .status(MembershipRequestStatus.PENDING)
                .build();

        return toResponse(requestRepository.save(membershipRequest));
    }

    @Override
    public MembershipRequestResponse acceptRequest(Long requestId) {
        MembershipRequest request = findRequestById(requestId);

        if (request.getStatus() != MembershipRequestStatus.PENDING) {
            throw new IllegalStateException("Seules les demandes en attente peuvent être acceptées.");
        }

        request.setStatus(MembershipRequestStatus.ACCEPTED);
        request.setProcessedAt(LocalDateTime.now());
        requestRepository.save(request);

        // Créer l'adhésion effective
        Membership membership = Membership.builder()
                .cooperative(request.getCooperative())
                .farmerUserId(request.getFarmerUserId())
                .build();
        membershipRepository.save(membership);

        return toResponse(request);
    }

    @Override
    public MembershipRequestResponse rejectRequest(Long requestId) {
        MembershipRequest request = findRequestById(requestId);

        if (request.getStatus() != MembershipRequestStatus.PENDING) {
            throw new IllegalStateException("Seules les demandes en attente peuvent être refusées.");
        }

        request.setStatus(MembershipRequestStatus.REJECTED);
        request.setProcessedAt(LocalDateTime.now());

        return toResponse(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipRequestResponse> getPendingRequestsByCooperative(Long cooperativeId) {
        return requestRepository.findByCooperativeIdAndStatus(cooperativeId, MembershipRequestStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipRequestResponse> getAllRequestsByCooperative(Long cooperativeId) {
        return requestRepository.findByCooperativeId(cooperativeId)
                .stream().map(this::toResponse).toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private MembershipRequest findRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande d'adhésion", id));
    }

    private MembershipRequestResponse toResponse(MembershipRequest r) {
        return MembershipRequestResponse.builder()
                .id(r.getId())
                .cooperativeId(r.getCooperative().getId())
                .cooperativeName(r.getCooperative().getName())
                .farmerUserId(r.getFarmerUserId())
                .status(r.getStatus())
                .message(r.getMessage())
                .requestedAt(r.getRequestedAt())
                .processedAt(r.getProcessedAt())
                .build();
    }
}
