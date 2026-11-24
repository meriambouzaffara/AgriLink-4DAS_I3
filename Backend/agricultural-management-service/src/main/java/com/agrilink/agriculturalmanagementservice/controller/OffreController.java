package com.agrilink.agriculturalmanagementservice.controller;

import com.agrilink.agriculturalmanagementservice.dto.OffreCreationDTO;
import com.agrilink.agriculturalmanagementservice.dto.OffreReponseDTO;
import com.agrilink.agriculturalmanagementservice.service.OffreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
public class OffreController {

    private final OffreService offreService;

    public OffreController(OffreService offreService) {
        this.offreService = offreService;
    }

    @PostMapping
    public ResponseEntity<OffreReponseDTO> creerOffre(@Valid @RequestBody OffreCreationDTO dto) {
        OffreReponseDTO nouvelleOffre = offreService.creerOffre(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleOffre);
    }

    @GetMapping
    public ResponseEntity<List<OffreReponseDTO>> consulterOffres() {
        List<OffreReponseDTO> offres = offreService.consulterOffres();
        return ResponseEntity.ok(offres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OffreReponseDTO> consulterOffreParId(@PathVariable UUID id) {
        OffreReponseDTO offre = offreService.consulterOffreParId(id);
        return ResponseEntity.ok(offre);
    }

    @GetMapping("/agriculteur/{agriculteurId}")
    public ResponseEntity<List<OffreReponseDTO>> consulterOffresParAgriculteur(@PathVariable UUID agriculteurId) {
        List<OffreReponseDTO> offres = offreService.consulterOffresParAgriculteur(agriculteurId);
        return ResponseEntity.ok(offres);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OffreReponseDTO> modifierOffre(@PathVariable UUID id,
                                                        @Valid @RequestBody OffreCreationDTO dto) {
        OffreReponseDTO offreModifiee = offreService.modifierOffre(id, dto);
        return ResponseEntity.ok(offreModifiee);
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<OffreReponseDTO> publierOffre(@PathVariable UUID id) {
        OffreReponseDTO offrePubliee = offreService.publierOffre(id);
        return ResponseEntity.ok(offrePubliee);
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<OffreReponseDTO> retirerOffre(@PathVariable UUID id) {
        OffreReponseDTO offreRetiree = offreService.retirerOffre(id);
        return ResponseEntity.ok(offreRetiree);
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<OffreReponseDTO> archiverOffre(@PathVariable UUID id) {
        OffreReponseDTO offreArchivee = offreService.archiverOffre(id);
        return ResponseEntity.ok(offreArchivee);
    }
}
