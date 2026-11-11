package com.agrilink.cooperative.controller;

import com.agrilink.cooperative.dto.request.CreateCooperativeRequest;
import com.agrilink.cooperative.dto.request.MembershipRequestDto;
import com.agrilink.cooperative.dto.response.CooperativeResponse;
import com.agrilink.cooperative.dto.response.MembershipResponse;
import com.agrilink.cooperative.service.CooperativeService;
import com.agrilink.cooperative.service.MembershipRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cooperatives")
@RequiredArgsConstructor
public class CooperativeController {

    private final CooperativeService cooperativeService;
    private final MembershipRequestService membershipRequestService;

    /**
     * POST /api/cooperatives
     * Créer une nouvelle coopérative agricole.
     */
    @PostMapping
    public ResponseEntity<CooperativeResponse> createCooperative(
            @Valid @RequestBody CreateCooperativeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cooperativeService.createCooperative(request));
    }

    /**
     * GET /api/cooperatives
     * Consulter toutes les coopératives disponibles.
     */
    @GetMapping
    public ResponseEntity<List<CooperativeResponse>> getAllCooperatives() {
        return ResponseEntity.ok(cooperativeService.getAllCooperatives());
    }

    /**
     * GET /api/cooperatives/{id}
     * Consulter une coopérative par son identifiant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CooperativeResponse> getCooperativeById(@PathVariable Long id) {
        return ResponseEntity.ok(cooperativeService.getCooperativeById(id));
    }

    /**
     * PUT /api/cooperatives/{id}
     * Modifier les informations d'une coopérative.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CooperativeResponse> updateCooperative(
            @PathVariable Long id,
            @Valid @RequestBody CreateCooperativeRequest request) {
        return ResponseEntity.ok(cooperativeService.updateCooperative(id, request));
    }

    /**
     * DELETE /api/cooperatives/{id}
     * Supprimer une coopérative.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCooperative(@PathVariable Long id) {
        cooperativeService.deleteCooperative(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/cooperatives/{id}/members
     * Consulter la liste des agriculteurs membres d'une coopérative.
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<MembershipResponse>> getMembers(@PathVariable Long id) {
        return ResponseEntity.ok(cooperativeService.getMembersByCooperativeId(id));
    }

    /**
     * POST /api/cooperatives/{id}/members
     * Soumettre une demande d'adhésion à une coopérative.
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<?> submitMembershipRequest(
            @PathVariable Long id,
            @Valid @RequestBody MembershipRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipRequestService.submitRequest(id, request));
    }
}
