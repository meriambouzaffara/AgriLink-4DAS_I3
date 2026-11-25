package tn.agrilink.backend.qualitycertificationservice.controllers;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.agrilink.backend.qualitycertificationservice.dto.RapportDTO;
import tn.agrilink.backend.qualitycertificationservice.dto.GenererRapportDTO;
import tn.agrilink.backend.qualitycertificationservice.services.IRapportService;

import java.util.List;

@RestController
@RequestMapping("/api/qualite-agricole/rapports")
@AllArgsConstructor
public class RapportController {

    private final IRapportService service;

    // Générer un rapport PDF de contrôles qualité
    @PostMapping("/controles-qualite")
    public RapportDTO genererRapportControles(
            @RequestBody GenererRapportDTO dto) {
        return service.genererRapportControles(dto);
    }

    // Générer un rapport PDF de certifications
    @PostMapping("/certifications")
    public RapportDTO genererRapportCertifications(
            @RequestBody GenererRapportDTO dto) {
        return service.genererRapportCertifications(dto);
    }

    // Consulter un rapport par ID
    @GetMapping("/{id}")
    public RapportDTO retrieve(@PathVariable Long id) {
        return service.retrieve(id);
    }

    // Consulter tous les rapports
    @GetMapping
    public List<RapportDTO> retrieveAll() {
        return service.retrieveAll();
    }

    // Consulter les rapports d'un responsable
    @GetMapping("/responsable/{idResponsable}")
    public List<RapportDTO> retrieveByResponsable(
            @PathVariable Long idResponsable) {
        return service.retrieveByResponsable(idResponsable);
    }

    // Télécharger un rapport PDF
    @GetMapping("/{id}/telechargement")
    public ResponseEntity<Resource> telechargerRapport(
            @PathVariable Long id) {

        Resource fichier = service.telechargerRapport(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fichier.getFilename() + "\""
                )
                .body(fichier);
    }
}

