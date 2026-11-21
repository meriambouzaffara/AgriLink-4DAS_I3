package tn.agrilink.backend.qualitycertificationservice.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.agrilink.backend.qualitycertificationservice.dto.CertificationDTO;
import tn.agrilink.backend.qualitycertificationservice.dto.ChangerStatutCertificationDTO;
import tn.agrilink.backend.qualitycertificationservice.services.ICertificationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/qualite-agricole/certifications")
@AllArgsConstructor
public class CertificationController {

    private final ICertificationService service;

    // Créer une certification
    @PostMapping
    public CertificationDTO add(@RequestBody CertificationDTO dto) {
        return service.add(dto);
    }

    // Consulter toutes les certifications
    @GetMapping
    public List<CertificationDTO> retrieveAll() {
        return service.retrieveAll();
    }

    // Consulter une certification par ID
    @GetMapping("/{id}")
    public CertificationDTO retrieve(@PathVariable Long id) {
        return service.retrieve(id);
    }

    // Modifier une certification
    @PutMapping("/{id}")
    public CertificationDTO update(
            @PathVariable Long id,
            @RequestBody CertificationDTO dto) {
        return service.update(id, dto);
    }

    // Modifier le statut d'une certification
    @PatchMapping("/{id}/statut")
    public CertificationDTO changerStatut(
            @PathVariable Long id,
            @RequestBody ChangerStatutCertificationDTO dto) {
        return service.changerStatut(id, dto.getStatut());
    }

    // Archiver une certification
    @PatchMapping("/{id}/archivage")
    public void archive(@PathVariable Long id) {
        service.archive(id);
    }

    // Rechercher les certifications par nom
    @GetMapping("/recherche")
    public List<CertificationDTO> rechercherParNom(
            @RequestParam String nom) {
        return service.rechercherParNom(nom);
    }

    // Consulter les certifications associées à une récolte
    @GetMapping("/recolte/{recolteId}")
    public List<CertificationDTO> rechercherParRecolte(
            @PathVariable UUID recolteId) {
        return service.rechercherParRecolte(recolteId);
    }
}
