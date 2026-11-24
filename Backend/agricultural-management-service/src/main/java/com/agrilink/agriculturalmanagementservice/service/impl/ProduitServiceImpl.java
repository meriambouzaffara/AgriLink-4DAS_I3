package com.agrilink.agriculturalmanagementservice.service.impl;

import com.agrilink.agriculturalmanagementservice.dto.ProduitCreationDTO;
import com.agrilink.agriculturalmanagementservice.dto.ProduitReponseDTO;
import com.agrilink.agriculturalmanagementservice.dto.ResultatUploadImageDTO;
import com.agrilink.agriculturalmanagementservice.entity.Produit;
import com.agrilink.agriculturalmanagementservice.exception.ConflitMetierException;
import com.agrilink.agriculturalmanagementservice.exception.DonneesInvalidesException;
import com.agrilink.agriculturalmanagementservice.exception.RessourceIntrouvableException;
import com.agrilink.agriculturalmanagementservice.mapper.ProduitMapper;
import com.agrilink.agriculturalmanagementservice.repository.OffreRepository;
import com.agrilink.agriculturalmanagementservice.repository.ProduitRepository;
import com.agrilink.agriculturalmanagementservice.repository.RecolteRepository;
import com.agrilink.agriculturalmanagementservice.securite.SecuriteService;
import com.agrilink.agriculturalmanagementservice.service.CloudinaryService;
import com.agrilink.agriculturalmanagementservice.service.ProduitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final RecolteRepository recolteRepository;
    private final OffreRepository offreRepository;
    private final ProduitMapper produitMapper;
    private final SecuriteService securiteService;
    private final CloudinaryService cloudinaryService;

    public ProduitServiceImpl(ProduitRepository produitRepository,
                              RecolteRepository recolteRepository,
                              OffreRepository offreRepository,
                              ProduitMapper produitMapper,
                              SecuriteService securiteService,
                              CloudinaryService cloudinaryService) {
        this.produitRepository = produitRepository;
        this.recolteRepository = recolteRepository;
        this.offreRepository = offreRepository;
        this.produitMapper = produitMapper;
        this.securiteService = securiteService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public ProduitReponseDTO creerProduit(ProduitCreationDTO dto) {
        return creerProduit(dto, null);
    }

    @Override
    public ProduitReponseDTO creerProduit(ProduitCreationDTO dto, MultipartFile image) {
        if (dto.designation() == null || dto.designation().isBlank()) {
            throw new DonneesInvalidesException("La désignation du produit est obligatoire");
        }
        if (dto.categorie() == null) {
            throw new DonneesInvalidesException("La catégorie du produit est obligatoire");
        }
        if (dto.creeParAgriculteurId() == null) {
            throw new DonneesInvalidesException("L'identifiant de l'agriculteur créateur est obligatoire");
        }

        securiteService.validerAccesAgriculteur(dto.creeParAgriculteurId());

        Produit produit = produitMapper.toEntity(dto);

        // Upload de l'image sur Cloudinary si fournie
        if (image != null && !image.isEmpty()) {
            ResultatUploadImageDTO uploadResult = cloudinaryService.uploaderImage(image);
            produit.setImageUrl(uploadResult.imageUrl());
            produit.setImagePublicId(uploadResult.imagePublicId());
        }

        Produit enregistre = produitRepository.save(produit);
        return produitMapper.toDto(enregistre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduitReponseDTO> consulterTousLesProduits() {
        return produitRepository.findAll()
                .stream()
                .map(produitMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProduitReponseDTO consulterProduitParId(UUID id) {
        return produitRepository.findById(id)
                .map(produitMapper::toDto)
                .orElseThrow(() -> new RessourceIntrouvableException("Produit introuvable avec l'identifiant : " + id));
    }

    @Override
    public ProduitReponseDTO modifierProduit(UUID id, ProduitCreationDTO dto) {
        return modifierProduit(id, dto, null);
    }

    @Override
    public ProduitReponseDTO modifierProduit(UUID id, ProduitCreationDTO dto, MultipartFile image) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Produit introuvable avec l'identifiant : " + id));

        securiteService.validerAccesAgriculteur(produit.getCreeParAgriculteurId());

        if (dto.designation() == null || dto.designation().isBlank()) {
            throw new DonneesInvalidesException("La désignation du produit est obligatoire");
        }
        if (dto.categorie() == null) {
            throw new DonneesInvalidesException("La catégorie du produit est obligatoire");
        }

        produitMapper.updateEntityFromDto(produit, dto);

        // Remplacement de l'image si un nouveau fichier est transmis
        if (image != null && !image.isEmpty()) {
            // Suppression de l'ancienne image sur Cloudinary
            if (produit.getImagePublicId() != null && !produit.getImagePublicId().isBlank()) {
                cloudinaryService.supprimerImage(produit.getImagePublicId());
            }

            ResultatUploadImageDTO uploadResult = cloudinaryService.uploaderImage(image);
            produit.setImageUrl(uploadResult.imageUrl());
            produit.setImagePublicId(uploadResult.imagePublicId());
        }

        Produit misAJour = produitRepository.save(produit);
        return produitMapper.toDto(misAJour);
    }

    @Override
    public ProduitReponseDTO ajouterOuRemplacerImage(UUID id, MultipartFile image) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Produit introuvable avec l'identifiant : " + id));

        securiteService.validerAccesAgriculteur(produit.getCreeParAgriculteurId());

        if (image == null || image.isEmpty()) {
            throw new DonneesInvalidesException("Le fichier image est obligatoire pour cette opération");
        }

        // Suppression de l'ancienne image sur Cloudinary si elle existe
        if (produit.getImagePublicId() != null && !produit.getImagePublicId().isBlank()) {
            cloudinaryService.supprimerImage(produit.getImagePublicId());
        }

        ResultatUploadImageDTO uploadResult = cloudinaryService.uploaderImage(image);
        produit.setImageUrl(uploadResult.imageUrl());
        produit.setImagePublicId(uploadResult.imagePublicId());

        Produit misAJour = produitRepository.save(produit);
        return produitMapper.toDto(misAJour);
    }

    @Override
    public ProduitReponseDTO supprimerImageProduit(UUID id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Produit introuvable avec l'identifiant : " + id));

        securiteService.validerAccesAgriculteur(produit.getCreeParAgriculteurId());

        // Suppression sur Cloudinary
        if (produit.getImagePublicId() != null && !produit.getImagePublicId().isBlank()) {
            cloudinaryService.supprimerImage(produit.getImagePublicId());
        }

        // Réinitialisation des attributs dans H2
        produit.setImageUrl(null);
        produit.setImagePublicId(null);

        Produit misAJour = produitRepository.save(produit);
        return produitMapper.toDto(misAJour);
    }

    @Override
    public void supprimerProduit(UUID id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Produit introuvable avec l'identifiant : " + id));

        securiteService.validerAccesAgriculteur(produit.getCreeParAgriculteurId());

        if (recolteRepository.existsByProduitId(id)) {
            throw new ConflitMetierException("Impossible de supprimer le produit : des récoltes y sont associées");
        }
        if (offreRepository.existsByProduitId(id)) {
            throw new ConflitMetierException("Impossible de supprimer le produit : des offres y sont associées");
        }

        // Nettoyage de l'image hébergée sur Cloudinary lors de la suppression définitive du produit
        if (produit.getImagePublicId() != null && !produit.getImagePublicId().isBlank()) {
            cloudinaryService.supprimerImage(produit.getImagePublicId());
        }

        produitRepository.delete(produit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduitReponseDTO> consulterProduitsParAgriculteur(UUID agriculteurId) {
        return produitRepository.findByCreeParAgriculteurId(agriculteurId)
                .stream()
                .map(produitMapper::toDto)
                .toList();
    }
}
