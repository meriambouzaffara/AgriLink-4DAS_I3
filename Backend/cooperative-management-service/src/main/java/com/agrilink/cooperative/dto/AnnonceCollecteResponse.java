package com.agrilink.cooperative.dto;

import com.agrilink.cooperative.entity.StatutAnnonce;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceCollecteResponse {

    private Long id;
    private Long besoinId;
    private Long responsableId;
    private BigDecimal quantiteObjectif;
    private BigDecimal quantiteAcceptee;
    private LocalDateTime datePublication;
    private LocalDate dateLimite;
    private String description;
    private StatutAnnonce statut;
    private int nombrePropositions;
}
