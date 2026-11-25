package com.agrilink.cooperative.controller;

import com.agrilink.cooperative.dto.BesoinClientRequest;
import com.agrilink.cooperative.dto.BesoinClientResponse;
import com.agrilink.cooperative.entity.StatutBesoin;
import com.agrilink.cooperative.service.BesoinClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/besoins")
@RequiredArgsConstructor
public class BesoinClientController {

    private final BesoinClientService besoinClientService;

    /**
     * POST /api/besoins
     * Creer un besoin client.
     */
    @PostMapping
    public ResponseEntity<BesoinClientResponse> createBesoin(@Valid @RequestBody BesoinClientRequest request) {
        BesoinClientResponse response = besoinClientService.createBesoin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/besoins
     * Consulter tous les besoins (filtres optionnels par clientId, productId ou statut).
     */
    @GetMapping
    public ResponseEntity<List<BesoinClientResponse>> getAllBesoins(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) StatutBesoin statut) {
        return ResponseEntity.ok(besoinClientService.getAllBesoins(clientId, productId, statut));
    }

    /**
     * GET /api/besoins/{id}
     * Consulter un besoin par identifiant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BesoinClientResponse> getBesoinById(@PathVariable Long id) {
        return ResponseEntity.ok(besoinClientService.getBesoinById(id));
    }

    /**
     * PUT /api/besoins/{id}
     * Modifier un besoin existant.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BesoinClientResponse> updateBesoin(
            @PathVariable Long id,
            @Valid @RequestBody BesoinClientRequest request) {
        return ResponseEntity.ok(besoinClientService.updateBesoin(id, request));
    }

    /**
     * PATCH /api/besoins/{id}/annuler
     * Annuler un besoin client.
     */
    @PatchMapping("/{id}/annuler")
    public ResponseEntity<BesoinClientResponse> annulerBesoin(@PathVariable Long id) {
        return ResponseEntity.ok(besoinClientService.annulerBesoin(id));
    }

    /**
     * DELETE /api/besoins/{id}
     * Supprimer un besoin client.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBesoin(@PathVariable Long id) {
        besoinClientService.deleteBesoin(id);
        return ResponseEntity.noContent().build();
    }
}
