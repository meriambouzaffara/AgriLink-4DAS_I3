package com.agrilink.cooperative.mapper;

import com.agrilink.cooperative.dto.BesoinClientRequest;
import com.agrilink.cooperative.dto.BesoinClientResponse;
import com.agrilink.cooperative.entity.BesoinClient;
import com.agrilink.cooperative.entity.StatutBesoin;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BesoinClientMapper {

    public BesoinClient toEntity(BesoinClientRequest request) {
        if (request == null) {
            return null;
        }
        return BesoinClient.builder()
                .clientId(request.getClientId())
                .productId(request.getProductId())
                .quantiteDemandee(request.getQuantiteDemandee())
                .dateSouhaitee(request.getDateSouhaitee())
                .description(request.getDescription())
                .statut(StatutBesoin.EN_ATTENTE)
                .dateCreation(LocalDateTime.now())
                .build();
    }

    public BesoinClientResponse toResponse(BesoinClient entity) {
        if (entity == null) {
            return null;
        }
        return BesoinClientResponse.builder()
                .id(entity.getId())
                .clientId(entity.getClientId())
                .productId(entity.getProductId())
                .quantiteDemandee(entity.getQuantiteDemandee())
                .dateSouhaitee(entity.getDateSouhaitee())
                .description(entity.getDescription())
                .statut(entity.getStatut())
                .dateCreation(entity.getDateCreation())
                .annonceCollecteId(entity.getAnnonceCollecte() != null ? entity.getAnnonceCollecte().getId() : null)
                .build();
    }

    public void updateEntityFromRequest(BesoinClient entity, BesoinClientRequest request) {
        if (entity == null || request == null) {
            return;
        }
        entity.setClientId(request.getClientId());
        entity.setProductId(request.getProductId());
        entity.setQuantiteDemandee(request.getQuantiteDemandee());
        entity.setDateSouhaitee(request.getDateSouhaitee());
        entity.setDescription(request.getDescription());
    }
}
