package com.agrilink.agriculturalmanagementservice.mapper;

import com.agrilink.agriculturalmanagementservice.dto.RecolteCreationDTO;
import com.agrilink.agriculturalmanagementservice.dto.RecolteReponseDTO;
import com.agrilink.agriculturalmanagementservice.entity.Produit;
import com.agrilink.agriculturalmanagementservice.entity.Recolte;
import com.agrilink.agriculturalmanagementservice.enums.StatutRecolte;
import org.springframework.stereotype.Component;

@Component
public class RecolteMapper {

    public Recolte toEntity(RecolteCreationDTO dto, Produit produit) {
        if (dto == null) {
            return null;
        }

        Recolte recolte = new Recolte();
        recolte.setProduit(produit);
        recolte.setAgriculteurId(dto.agriculteurId());
        recolte.setQuantiteTotale(dto.quantiteTotale());
        // À la création, la quantité disponible est égale à la quantité totale
        recolte.setQuantiteDisponible(dto.quantiteTotale());
        recolte.setDateRecolte(dto.dateRecolte());
        recolte.setDateDisponibilite(dto.dateDisponibilite());
        recolte.setDescription(dto.description());
        recolte.setStatut(StatutRecolte.DISPONIBLE);
        return recolte;
    }

    public RecolteReponseDTO toDto(Recolte entity) {
        if (entity == null) {
            return null;
        }

        return new RecolteReponseDTO(
                entity.getId(),
                entity.getProduit() != null ? entity.getProduit().getId() : null,
                entity.getAgriculteurId(),
                entity.getQuantiteTotale(),
                entity.getQuantiteDisponible(),
                entity.getDateRecolte(),
                entity.getDateDisponibilite(),
                entity.getDescription(),
                entity.getStatut()
        );
    }

    public void updateEntityFromDto(Recolte entity, RecolteCreationDTO dto, Produit produit) {
        if (entity == null || dto == null) {
            return;
        }

        if (produit != null) {
            entity.setProduit(produit);
        }
        entity.setDateRecolte(dto.dateRecolte());
        entity.setDateDisponibilite(dto.dateDisponibilite());
        entity.setDescription(dto.description());
        // Note : quantiteTotale et quantiteDisponible font l'objet d'un contrôle métier
        // dans le service avant mise à jour.
    }
}
