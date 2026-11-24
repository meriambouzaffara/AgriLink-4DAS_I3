package com.agrilink.agriculturalmanagementservice.mapper;

import com.agrilink.agriculturalmanagementservice.dto.OffreCreationDTO;
import com.agrilink.agriculturalmanagementservice.dto.OffreReponseDTO;
import com.agrilink.agriculturalmanagementservice.entity.Offre;
import com.agrilink.agriculturalmanagementservice.entity.Produit;
import com.agrilink.agriculturalmanagementservice.enums.StatutOffre;
import org.springframework.stereotype.Component;

@Component
public class OffreMapper {

    public Offre toEntity(OffreCreationDTO dto, Produit produit) {
        if (dto == null) {
            return null;
        }

        Offre offre = new Offre();
        offre.setProduit(produit);
        offre.setAgriculteurId(dto.agriculteurId());
        offre.setPrix(dto.prix());
        offre.setQuantite(dto.quantite());
        offre.setDateDisponibilite(dto.dateDisponibilite());
        offre.setStatut(StatutOffre.BROUILLON);
        return offre;
    }

    public OffreReponseDTO toDto(Offre entity) {
        if (entity == null) {
            return null;
        }

        return new OffreReponseDTO(
                entity.getId(),
                entity.getProduit() != null ? entity.getProduit().getId() : null,
                entity.getAgriculteurId(),
                entity.getPrix(),
                entity.getQuantite(),
                entity.getDateDisponibilite(),
                entity.getStatut(),
                entity.getDatePublication()
        );
    }

    public void updateEntityFromDto(Offre entity, OffreCreationDTO dto, Produit produit) {
        if (entity == null || dto == null) {
            return;
        }

        if (produit != null) {
            entity.setProduit(produit);
        }
        entity.setPrix(dto.prix());
        entity.setQuantite(dto.quantite());
        entity.setDateDisponibilite(dto.dateDisponibilite());
    }
}
