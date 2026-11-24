package com.agrilink.cooperative.controller;

import com.agrilink.cooperative.dto.PropositionRecolteResponse;
import com.agrilink.cooperative.dto.ReceptionRecolteRequest;
import com.agrilink.cooperative.dto.TraiterPropositionRequest;
import com.agrilink.cooperative.entity.StatutProposition;
import com.agrilink.cooperative.service.PropositionRecolteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/propositions")
@RequiredArgsConstructor
public class PropositionRecolteController {

    private final PropositionRecolteService propositionRecolteService;

    /**
     * GET /api/propositions
     * Consulter les propositions (filtres optionnels par annonceId, agriculteurId ou statut).
     */
    @GetMapping
    public ResponseEntity<List<PropositionRecolteResponse>> getAllPropositions(
            @RequestParam(required = false) Long annonceId,
            @RequestParam(required = false) Long agriculteurId,
            @RequestParam(required = false) StatutProposition statut) {
        return ResponseEntity.ok(propositionRecolteService.getAllPropositions(annonceId, agriculteurId, statut));
    }

    /**
     * GET /api/propositions/{id}
     * Consulter une proposition par identifiant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PropositionRecolteResponse> getPropositionById(@PathVariable Long id) {
        return ResponseEntity.ok(propositionRecolteService.getPropositionById(id));
    }

    /**
     * PATCH /api/propositions/{id}/decision
     * Accepter, accepter partiellement ou refuser une proposition.
     */
    @PatchMapping("/{id}/decision")
    public ResponseEntity<PropositionRecolteResponse> traiterDecision(
            @PathVariable Long id,
            @Valid @RequestBody TraiterPropositionRequest request) {
        return ResponseEntity.ok(propositionRecolteService.traiterDecision(id, request));
    }

    /**
     * PATCH /api/propositions/{id}/reception
     * Enregistrer la quantite reellement recue au depot.
     */
    @PatchMapping("/{id}/reception")
    public ResponseEntity<PropositionRecolteResponse> enregistrerReception(
            @PathVariable Long id,
            @Valid @RequestBody ReceptionRecolteRequest request) {
        return ResponseEntity.ok(propositionRecolteService.enregistrerReception(id, request));
    }

    /**
     * DELETE /api/propositions/{id}
     * Supprimer une proposition.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProposition(@PathVariable Long id) {
        propositionRecolteService.deleteProposition(id);
        return ResponseEntity.noContent().build();
    }
}
