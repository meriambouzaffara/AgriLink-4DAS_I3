package com.agrilink.cooperative.controller;

import com.agrilink.cooperative.dto.AnnonceCollecteRequest;
import com.agrilink.cooperative.dto.AnnonceCollecteResponse;
import com.agrilink.cooperative.dto.PropositionRecolteRequest;
import com.agrilink.cooperative.dto.PropositionRecolteResponse;
import com.agrilink.cooperative.entity.StatutAnnonce;
import com.agrilink.cooperative.service.AnnonceCollecteService;
import com.agrilink.cooperative.service.PropositionRecolteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annonces-collecte")
@RequiredArgsConstructor
public class AnnonceCollecteController {

    private final AnnonceCollecteService annonceCollecteService;
    private final PropositionRecolteService propositionRecolteService;

    /**
     * POST /api/annonces-collecte
     * Prendre en charge un besoin et publier une annonce de collecte.
     */
    @PostMapping
    public ResponseEntity<AnnonceCollecteResponse> createAnnonce(@Valid @RequestBody AnnonceCollecteRequest request) {
        AnnonceCollecteResponse response = annonceCollecteService.createAnnonce(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/annonces-collecte
     * Consulter les annonces de collecte (filtres optionnels par statut et responsableId).
     */
    @GetMapping
    public ResponseEntity<List<AnnonceCollecteResponse>> getAllAnnonces(
            @RequestParam(required = false) StatutAnnonce statut,
            @RequestParam(required = false) Long responsableId) {
        return ResponseEntity.ok(annonceCollecteService.getAllAnnonces(statut, responsableId));
    }

    /**
     * GET /api/annonces-collecte/{id}
     * Consulter les details d'une annonce.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnnonceCollecteResponse> getAnnonceById(@PathVariable Long id) {
        return ResponseEntity.ok(annonceCollecteService.getAnnonceById(id));
    }

    /**
     * PUT /api/annonces-collecte/{id}
     * Modifier les informations d'une annonce.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AnnonceCollecteResponse> updateAnnonce(
            @PathVariable Long id,
            @Valid @RequestBody AnnonceCollecteRequest request) {
        return ResponseEntity.ok(annonceCollecteService.updateAnnonce(id, request));
    }

    /**
     * PATCH /api/annonces-collecte/{id}/cloturer
     * Cloturer une annonce.
     */
    @PatchMapping("/{id}/cloturer")
    public ResponseEntity<AnnonceCollecteResponse> cloturerAnnonce(@PathVariable Long id) {
        return ResponseEntity.ok(annonceCollecteService.cloturerAnnonce(id));
    }

    /**
     * PATCH /api/annonces-collecte/{id}/annuler
     * Annuler une annonce.
     */
    @PatchMapping("/{id}/annuler")
    public ResponseEntity<AnnonceCollecteResponse> annulerAnnonce(@PathVariable Long id) {
        return ResponseEntity.ok(annonceCollecteService.annulerAnnonce(id));
    }

    /**
     * DELETE /api/annonces-collecte/{id}
     * Supprimer une annonce.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnonce(@PathVariable Long id) {
        annonceCollecteService.deleteAnnonce(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/annonces-collecte/{id}/propositions
     * Soumettre une proposition de recolte par un agriculteur pour une annonce donnee.
     */
    @PostMapping("/{id}/propositions")
    public ResponseEntity<PropositionRecolteResponse> submitProposition(
            @PathVariable Long id,
            @Valid @RequestBody PropositionRecolteRequest request) {
        PropositionRecolteResponse response = propositionRecolteService.createProposition(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
