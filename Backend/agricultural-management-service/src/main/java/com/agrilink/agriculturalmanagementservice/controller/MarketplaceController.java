package com.agrilink.agriculturalmanagementservice.controller;

import com.agrilink.agriculturalmanagementservice.dto.OffreReponseDTO;
import com.agrilink.agriculturalmanagementservice.enums.Categorie;
import com.agrilink.agriculturalmanagementservice.service.OffreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/marketplace/offers")
public class MarketplaceController {

    private final OffreService offreService;

    public MarketplaceController(OffreService offreService) {
        this.offreService = offreService;
    }

    @GetMapping
    public ResponseEntity<Page<OffreReponseDTO>> consulterOffresMarketplace(
            @RequestParam(required = false) Categorie categorie,
            @RequestParam(required = false) Double prixMin,
            @RequestParam(required = false) Double prixMax,
            @RequestParam(required = false) UUID agriculteurId,
            @PageableDefault(size = 10, sort = "datePublication", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<OffreReponseDTO> offres = offreService.consulterOffresMarketplace(
                categorie,
                prixMin,
                prixMax,
                agriculteurId,
                pageable
        );
        return ResponseEntity.ok(offres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OffreReponseDTO> consulterOffreMarketplaceParId(@PathVariable UUID id) {
        OffreReponseDTO offre = offreService.consulterOffreMarketplaceParId(id);
        return ResponseEntity.ok(offre);
    }
}
