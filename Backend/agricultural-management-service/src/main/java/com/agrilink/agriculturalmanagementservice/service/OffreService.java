package com.agrilink.agriculturalmanagementservice.service;

import com.agrilink.agriculturalmanagementservice.dto.OffreCreationDTO;
import com.agrilink.agriculturalmanagementservice.dto.OffreReponseDTO;
import com.agrilink.agriculturalmanagementservice.enums.Categorie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OffreService {

    OffreReponseDTO creerOffre(OffreCreationDTO dto);

    List<OffreReponseDTO> consulterOffres();

    OffreReponseDTO consulterOffreParId(UUID id);

    List<OffreReponseDTO> consulterOffresParAgriculteur(UUID agriculteurId);

    OffreReponseDTO modifierOffre(UUID id, OffreCreationDTO dto);

    OffreReponseDTO publierOffre(UUID id);

    OffreReponseDTO retirerOffre(UUID id);

    OffreReponseDTO archiverOffre(UUID id);

    Page<OffreReponseDTO> consulterOffresMarketplace(Categorie categorie,
                                                    Double prixMin,
                                                    Double prixMax,
                                                    UUID agriculteurId,
                                                    Pageable pageable);

    OffreReponseDTO consulterOffreMarketplaceParId(UUID id);
}
