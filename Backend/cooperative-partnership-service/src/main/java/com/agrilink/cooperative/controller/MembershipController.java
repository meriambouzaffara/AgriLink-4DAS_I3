package com.agrilink.cooperative.controller;

import com.agrilink.cooperative.dto.response.MembershipRequestResponse;
import com.agrilink.cooperative.service.MembershipRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipRequestService membershipRequestService;

    /**
     * PATCH /api/memberships/{id}/accept
     * Accepter une demande d'adhésion.
     */
    @PatchMapping("/{id}/accept")
    public ResponseEntity<MembershipRequestResponse> acceptMembership(@PathVariable Long id) {
        return ResponseEntity.ok(membershipRequestService.acceptRequest(id));
    }

    /**
     * PATCH /api/memberships/{id}/reject
     * Refuser une demande d'adhésion.
     */
    @PatchMapping("/{id}/reject")
    public ResponseEntity<MembershipRequestResponse> rejectMembership(@PathVariable Long id) {
        return ResponseEntity.ok(membershipRequestService.rejectRequest(id));
    }

    /**
     * GET /api/memberships/cooperative/{cooperativeId}/pending
     * Consulter les demandes d'adhésion en attente pour une coopérative.
     */
    @GetMapping("/cooperative/{cooperativeId}/pending")
    public ResponseEntity<List<MembershipRequestResponse>> getPendingRequests(
            @PathVariable Long cooperativeId) {
        return ResponseEntity.ok(membershipRequestService.getPendingRequestsByCooperative(cooperativeId));
    }

    /**
     * GET /api/memberships/cooperative/{cooperativeId}
     * Consulter toutes les demandes d'adhésion pour une coopérative.
     */
    @GetMapping("/cooperative/{cooperativeId}")
    public ResponseEntity<List<MembershipRequestResponse>> getAllRequests(
            @PathVariable Long cooperativeId) {
        return ResponseEntity.ok(membershipRequestService.getAllRequestsByCooperative(cooperativeId));
    }
}
