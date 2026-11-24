package com.agrilink.cooperative.mapper;

import com.agrilink.cooperative.dto.PropositionRecolteRequest;
import com.agrilink.cooperative.dto.PropositionRecolteResponse;
import com.agrilink.cooperative.entity.AnnonceCollecte;
import com.agrilink.cooperative.entity.PropositionRecolte;
import com.agrilink.cooperative.entity.StatutProposition;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PropositionRecolteMapper {

    public PropositionRecolte toEntity(PropositionRecolteRequest request, AnnonceCollecte annonce) {
        if (request == null) {
            return null;
        }
        return PropositionRecolte.builder()
                .annonce(annonce)
                .agriculteurId(request.getAgriculteurId())
                .recolteId(request.getRecolteId())
                .quantiteProposee(request.getQuantiteProposee())
                .dateProposition(LocalDateTime.now())
                .statut(StatutProposition.EN_ATTENTE)
                .commentaire(request.getCommentaire())
                .build();
    }

    public PropositionRecolteResponse toResponse(PropositionRecolte entity) {
        if (entity == null) {
            return null;
        }
        return PropositionRecolteResponse.builder()
                .id(entity.getId())
                .annonceId(entity.getAnnonce() != null ? entity.getAnnonce().getId() : null)
                .agriculteurId(entity.getAgriculteurId())
                .recolteId(entity.getRecolteId())
                .quantiteProposee(entity.getQuantiteProposee())
                .quantiteAcceptee(entity.getQuantiteAcceptee())
                .quantiteRecue(entity.getQuantiteRecue())
                .dateProposition(entity.getDateProposition())
                .dateReception(entity.getDateReception())
                .statut(entity.getStatut())
                .commentaire(entity.getCommentaire())
                .build();
    }
}
