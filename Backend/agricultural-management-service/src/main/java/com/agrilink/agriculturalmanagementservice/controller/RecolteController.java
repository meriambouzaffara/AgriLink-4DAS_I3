package com.agrilink.agriculturalmanagementservice.controller;

import com.agrilink.agriculturalmanagementservice.dto.RecolteCreationDTO;
import com.agrilink.agriculturalmanagementservice.dto.RecolteReponseDTO;
import com.agrilink.agriculturalmanagementservice.dto.RecolteStatutModificationDTO;
import com.agrilink.agriculturalmanagementservice.enums.StatutRecolte;
import com.agrilink.agriculturalmanagementservice.exception.DonneesInvalidesException;
import com.agrilink.agriculturalmanagementservice.service.RecolteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class RecolteController {

    private final RecolteService recolteService;

    public RecolteController(RecolteService recolteService) {
        this.recolteService = recolteService;
    }

    @GetMapping("/api/recoltes")
    public ResponseEntity<List<RecolteReponseDTO>> consulterToutesLesRecoltes() {
        List<RecolteReponseDTO> recoltes = recolteService.consulterToutesLesRecoltes();
        return ResponseEntity.ok(recoltes);
    }

    @GetMapping("/api/recoltes/{id}")
    public ResponseEntity<RecolteReponseDTO> consulterRecolteParId(@PathVariable UUID id) {
        RecolteReponseDTO recolte = recolteService.consulterRecolteParId(id);
        return ResponseEntity.ok(recolte);
    }

    @GetMapping("/api/recoltes/agriculteur/{agriculteurId}")
    public ResponseEntity<List<RecolteReponseDTO>> consulterRecoltesParAgriculteur(@PathVariable UUID agriculteurId) {
        List<RecolteReponseDTO> recoltes = recolteService.consulterRecoltesParAgriculteur(agriculteurId);
        return ResponseEntity.ok(recoltes);
    }

    @GetMapping("/api/recoltes/produit/{produitId}")
    public ResponseEntity<List<RecolteReponseDTO>> consulterRecoltesParProduit(@PathVariable UUID produitId) {
        List<RecolteReponseDTO> recoltes = recolteService.consulterRecoltesParProduit(produitId);
        return ResponseEntity.ok(recoltes);
    }

    @PostMapping("/api/products/{produitId}/recoltes")
    public ResponseEntity<RecolteReponseDTO> declarerRecoltePourProduit(@PathVariable UUID produitId,
                                                                       @Valid @RequestBody RecolteCreationDTO dto) {
        RecolteCreationDTO dtoFinal = dto;
        if (dto.produitId() == null || !produitId.equals(dto.produitId())) {
            dtoFinal = new RecolteCreationDTO(
                    produitId,
                    dto.agriculteurId(),
                    dto.quantiteTotale(),
                    dto.dateRecolte(),
                    dto.dateDisponibilite(),
                    dto.description()
            );
        }

        RecolteReponseDTO nouvelleRecolte = recolteService.declarerRecolte(dtoFinal);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleRecolte);
    }

    @PostMapping("/api/recoltes")
    public ResponseEntity<RecolteReponseDTO> declarerRecolte(@Valid @RequestBody RecolteCreationDTO dto) {
        RecolteReponseDTO nouvelleRecolte = recolteService.declarerRecolte(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleRecolte);
    }

    @PutMapping("/api/recoltes/{id}")
    public ResponseEntity<RecolteReponseDTO> modifierRecolte(@PathVariable UUID id,
                                                            @Valid @RequestBody RecolteCreationDTO dto) {
        RecolteReponseDTO recolteModifiee = recolteService.modifierRecolte(id, dto);
        return ResponseEntity.ok(recolteModifiee);
    }

    @PatchMapping("/api/recoltes/{id}/status")
    public ResponseEntity<RecolteReponseDTO> modifierStatutRecolte(
            @PathVariable UUID id,
            @RequestBody(required = false) RecolteStatutModificationDTO body,
            @RequestParam(name = "status", required = false) StatutRecolte statusParam) {

        StatutRecolte statutFinal = null;
        if (body != null && body.statut() != null) {
            statutFinal = body.statut();
        } else if (statusParam != null) {
            statutFinal = statusParam;
        } else {
            throw new DonneesInvalidesException("Le statut de la récolte est obligatoire (via le corps JSON ou le paramètre 'status')");
        }

        RecolteReponseDTO recolteModifiee = recolteService.modifierStatutRecolte(id, statutFinal);
        return ResponseEntity.ok(recolteModifiee);
    }
}
