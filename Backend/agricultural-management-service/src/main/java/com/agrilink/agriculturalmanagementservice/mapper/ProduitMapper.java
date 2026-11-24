package com.agrilink.agriculturalmanagementservice.mapper;

import com.agrilink.agriculturalmanagementservice.dto.ProduitCreationDTO;
import com.agrilink.agriculturalmanagementservice.dto.ProduitReponseDTO;
import com.agrilink.agriculturalmanagementservice.entity.Produit;
import org.springframework.stereotype.Component;

@Component
public class ProduitMapper {

    public Produit toEntity(ProduitCreationDTO dto) {
        if (dto == null) {
            return null;
        }

        Produit produit = new Produit();
        produit.setDesignation(dto.designation());
        produit.setDescription(dto.description());
        produit.setCategorie(dto.categorie());
        produit.setCreeParAgriculteurId(dto.creeParAgriculteurId());
        return produit;
    }

    public ProduitReponseDTO toDto(Produit entity) {
        if (entity == null) {
            return null;
        }

        return new ProduitReponseDTO(
                entity.getId(),
                entity.getDesignation(),
                entity.getDescription(),
                entity.getCategorie(),
                entity.getCreeParAgriculteurId(),
                entity.getDateCreation(),
                entity.getImageUrl(),
                entity.getImagePublicId()
        );
    }

    public void updateEntityFromDto(Produit entity, ProduitCreationDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setDesignation(dto.designation());
        entity.setDescription(dto.description());
        entity.setCategorie(dto.categorie());
    }
}
