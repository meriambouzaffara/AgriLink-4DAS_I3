package com.agrilink.agriculturalmanagementservice.controller;

import com.agrilink.agriculturalmanagementservice.dto.ProduitCreationDTO;
import com.agrilink.agriculturalmanagementservice.dto.ProduitReponseDTO;
import com.agrilink.agriculturalmanagementservice.service.ProduitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProduitController {

    private final ProduitService produitService;
    private final ObjectMapper objectMapper;

    public ProduitController(ProduitService produitService, ObjectMapper objectMapper) {
        this.produitService = produitService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<ProduitReponseDTO>> consulterTousLesProduits() {
        List<ProduitReponseDTO> produits = produitService.consulterTousLesProduits();
        return ResponseEntity.ok(produits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitReponseDTO> consulterProduitParId(@PathVariable UUID id) {
        ProduitReponseDTO produit = produitService.consulterProduitParId(id);
        return ResponseEntity.ok(produit);
    }

    /**
     * Création d'un produit en format standard JSON (sans image).
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProduitReponseDTO> creerProduit(@Valid @RequestBody ProduitCreationDTO dto) {
        ProduitReponseDTO nouveauProduit = produitService.creerProduit(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauProduit);
    }

    /**
     * Création d'un produit avec photo optionnelle sur Cloudinary (Multipart).
     * Le champ "produit" est reçu comme texte JSON brut (pas besoin de Content-Type sur la partie).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProduitReponseDTO> creerProduitAvecImage(
            @RequestPart("produit") String produitJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            ProduitCreationDTO dto = objectMapper.readValue(produitJson, ProduitCreationDTO.class);
            ProduitReponseDTO nouveauProduit = produitService.creerProduit(dto, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauProduit);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "JSON invalide pour le champ 'produit' : " + e.getOriginalMessage());
        }
    }

    /**
     * Modification d'un produit en format standard JSON.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProduitReponseDTO> modifierProduit(@PathVariable UUID id,
                                                            @Valid @RequestBody ProduitCreationDTO dto) {
        ProduitReponseDTO produitMisAJour = produitService.modifierProduit(id, dto);
        return ResponseEntity.ok(produitMisAJour);
    }

    /**
     * Modification d'un produit avec remplacement optionnel de la photo (Multipart).
     * Le champ "produit" est reçu comme texte JSON brut.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProduitReponseDTO> modifierProduitAvecImage(
            @PathVariable UUID id,
            @RequestPart("produit") String produitJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            ProduitCreationDTO dto = objectMapper.readValue(produitJson, ProduitCreationDTO.class);
            ProduitReponseDTO produitMisAJour = produitService.modifierProduit(id, dto, image);
            return ResponseEntity.ok(produitMisAJour);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "JSON invalide pour le champ 'produit' : " + e.getOriginalMessage());
        }
    }

    /**
     * Ajout ou remplacement dédié de la photo d'un produit existant.
     */
    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProduitReponseDTO> ajouterOuRemplacerImage(
            @PathVariable UUID id,
            @RequestParam("image") MultipartFile image) {
        ProduitReponseDTO produitMisAJour = produitService.ajouterOuRemplacerImage(id, image);
        return ResponseEntity.ok(produitMisAJour);
    }

    /**
     * Suppression exclusive de la photo d'un produit.
     */
    @DeleteMapping("/{id}/image")
    public ResponseEntity<ProduitReponseDTO> supprimerImageProduit(@PathVariable UUID id) {
        ProduitReponseDTO produitMisAJour = produitService.supprimerImageProduit(id);
        return ResponseEntity.ok(produitMisAJour);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerProduit(@PathVariable UUID id) {
        produitService.supprimerProduit(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/agriculteur/{agriculteurId}")
    public ResponseEntity<List<ProduitReponseDTO>> consulterProduitsParAgriculteur(@PathVariable UUID agriculteurId) {
        List<ProduitReponseDTO> produits = produitService.consulterProduitsParAgriculteur(agriculteurId);
        return ResponseEntity.ok(produits);
    }
}
