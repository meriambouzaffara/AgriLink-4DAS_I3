package com.agrilink.cooperative.mapper;

import com.agrilink.cooperative.dto.AnnonceCollecteRequest;
import com.agrilink.cooperative.dto.AnnonceCollecteResponse;
import com.agrilink.cooperative.entity.AnnonceCollecte;
import com.agrilink.cooperative.entity.BesoinClient;
import com.agrilink.cooperative.entity.StatutAnnonce;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class AnnonceCollecteMapper {

    public AnnonceCollecte toEntity(AnnonceCollecteRequest request, BesoinClient besoin) {
        if (request == null) {
            return null;
        }
        return AnnonceCollecte.builder()
                .besoin(besoin)
                .responsableId(request.getResponsableId())
                .quantiteObjectif(request.getQuantiteObjectif())
                .quantiteAcceptee(BigDecimal.ZERO)
                .datePublication(LocalDateTime.now())
                .dateLimite(request.getDateLimite())
                .description(request.getDescription())
                .statut(StatutAnnonce.OUVERTE)
                .build();
    }

    public AnnonceCollecteResponse toResponse(AnnonceCollecte entity) {
        if (entity == null) {
            return null;
        }
        return AnnonceCollecteResponse.builder()
                .id(entity.getId())
                .besoinId(entity.getBesoin() != null ? entity.getBesoin().getId() : null)
                .responsableId(entity.getResponsableId())
                .quantiteObjectif(entity.getQuantiteObjectif())
                .quantiteAcceptee(entity.getQuantiteAcceptee())
                .datePublication(entity.getDatePublication())
                .dateLimite(entity.getDateLimite())
                .description(entity.getDescription())
                .statut(entity.getStatut())
                .nombrePropositions(entity.getPropositions() != null ? entity.getPropositions().size() : 0)
                .build();
    }

    public void updateEntityFromRequest(AnnonceCollecte entity, AnnonceCollecteRequest request) {
        if (entity == null || request == null) {
            return;
        }
        entity.setResponsableId(request.getResponsableId());
        entity.setQuantiteObjectif(request.getQuantiteObjectif());
        entity.setDateLimite(request.getDateLimite());
        entity.setDescription(request.getDescription());
    }
}
