package com.agrilink.agriculturalmanagementservice.service;

import com.agrilink.agriculturalmanagementservice.dto.ProduitCreationDTO;
import com.agrilink.agriculturalmanagementservice.dto.ProduitReponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProduitService {

    ProduitReponseDTO creerProduit(ProduitCreationDTO dto);

    ProduitReponseDTO creerProduit(ProduitCreationDTO dto, MultipartFile image);

    List<ProduitReponseDTO> consulterTousLesProduits();

    ProduitReponseDTO consulterProduitParId(UUID id);

    ProduitReponseDTO modifierProduit(UUID id, ProduitCreationDTO dto);

    ProduitReponseDTO modifierProduit(UUID id, ProduitCreationDTO dto, MultipartFile image);

    ProduitReponseDTO ajouterOuRemplacerImage(UUID id, MultipartFile image);

    ProduitReponseDTO supprimerImageProduit(UUID id);

    void supprimerProduit(UUID id);

    List<ProduitReponseDTO> consulterProduitsParAgriculteur(UUID agriculteurId);
}
