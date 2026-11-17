package com.agrilink.cooperative.dto;

import com.agrilink.cooperative.entity.StatutBesoin;
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
public class BesoinClientResponse {

    private Long id;
    private Long clientId;
    private Long productId;
    private BigDecimal quantiteDemandee;
    private LocalDate dateSouhaitee;
    private String description;
    private StatutBesoin statut;
    private LocalDateTime dateCreation;
    private Long annonceCollecteId;
}

