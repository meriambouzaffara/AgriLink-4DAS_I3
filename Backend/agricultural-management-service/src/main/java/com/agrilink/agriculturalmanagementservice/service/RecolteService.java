package com.agrilink.agriculturalmanagementservice.service;

import com.agrilink.agriculturalmanagementservice.dto.RecolteCreationDTO;
import com.agrilink.agriculturalmanagementservice.dto.RecolteReponseDTO;
import com.agrilink.agriculturalmanagementservice.enums.StatutRecolte;

import java.util.List;
import java.util.UUID;

public interface RecolteService {

    RecolteReponseDTO declarerRecolte(RecolteCreationDTO dto);

    List<RecolteReponseDTO> consulterToutesLesRecoltes();

    RecolteReponseDTO consulterRecolteParId(UUID id);

    List<RecolteReponseDTO> consulterRecoltesParAgriculteur(UUID agriculteurId);

    List<RecolteReponseDTO> consulterRecoltesParProduit(UUID produitId);

    RecolteReponseDTO modifierRecolte(UUID id, RecolteCreationDTO dto);

    RecolteReponseDTO modifierStatutRecolte(UUID id, StatutRecolte statut);
}
