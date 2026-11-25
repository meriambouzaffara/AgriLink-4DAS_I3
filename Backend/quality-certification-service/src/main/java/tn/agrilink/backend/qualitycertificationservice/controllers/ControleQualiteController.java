package tn.agrilink.backend.qualitycertificationservice.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.agrilink.backend.qualitycertificationservice.dto.ControleQualiteDTO;
import tn.agrilink.backend.qualitycertificationservice.dto.ChangerStatutControleDTO;
import tn.agrilink.backend.qualitycertificationservice.services.IControleQualiteService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/qualite-agricole/controles")
@AllArgsConstructor
public class ControleQualiteController {

    private final IControleQualiteService service;

    // Ajouter un contrôle
    @PostMapping
    public ControleQualiteDTO add(@RequestBody ControleQualiteDTO dto) {
        return service.add(dto);
    }

    // Consulter tous les contrôles
    @GetMapping
    public List<ControleQualiteDTO> retrieveAll() {
        return service.retrieveAll();
    }

    // Consulter un contrôle par ID
    @GetMapping("/{id}")
    public ControleQualiteDTO retrieve(@PathVariable Long id) {
        return service.retrieve(id);
    }

    // Modifier un contrôle
    @PutMapping("/{id}")
    public ControleQualiteDTO update(
            @PathVariable Long id,
            @RequestBody ControleQualiteDTO dto) {
        return service.update(id, dto);
    }

    // Modifier le statut d'un contrôle
    @PatchMapping("/{id}/statut")
    public ControleQualiteDTO changerStatut(
            @PathVariable Long id,
            @RequestBody ChangerStatutControleDTO dto) {
        return service.changerStatut(id, dto.getStatut());
    }

    // Annuler un contrôle
    // Annuler un contrôle
    @PatchMapping("/{id}/annulation")
    public Map<String, String> cancel(@PathVariable Long id) {

        service.cancel(id);

        return Map.of(
                "message",
                "Le contrôle a été annulé avec succès."
        );
    }

    // Consulter l'historique d'une récolte
    @GetMapping("/historique")
    public List<ControleQualiteDTO> historique(
            @RequestParam UUID recolteId) {
        return service.historique(recolteId);
    }
}
