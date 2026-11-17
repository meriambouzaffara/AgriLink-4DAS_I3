package com.agrilink.cooperative.dto;

import com.agrilink.cooperative.entity.StatutProposition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropositionRecolteResponse {

    private Long id;
    private Long annonceId;
    private Long agriculteurId;
    private Long recolteId;
    private BigDecimal quantiteProposee;
    private BigDecimal quantiteAcceptee;
    private BigDecimal quantiteRecue;
    private LocalDateTime dateProposition;
    private LocalDateTime dateReception;
    private StatutProposition statut;
    private String commentaire;
}

