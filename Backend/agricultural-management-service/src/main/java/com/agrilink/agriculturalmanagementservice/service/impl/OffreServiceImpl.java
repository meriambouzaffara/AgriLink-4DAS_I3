package com.agrilink.agriculturalmanagementservice.service.impl;

import com.agrilink.agriculturalmanagementservice.dto.OffreCreationDTO;
import com.agrilink.agriculturalmanagementservice.dto.OffreReponseDTO;
import com.agrilink.agriculturalmanagementservice.entity.Offre;
import com.agrilink.agriculturalmanagementservice.entity.Produit;
import com.agrilink.agriculturalmanagementservice.enums.Categorie;
import com.agrilink.agriculturalmanagementservice.enums.StatutOffre;
import com.agrilink.agriculturalmanagementservice.exception.DonneesInvalidesException;
import com.agrilink.agriculturalmanagementservice.exception.RessourceIntrouvableException;
import com.agrilink.agriculturalmanagementservice.mapper.OffreMapper;
import com.agrilink.agriculturalmanagementservice.repository.OffreRepository;
import com.agrilink.agriculturalmanagementservice.repository.ProduitRepository;
import com.agrilink.agriculturalmanagementservice.repository.RecolteRepository;
import com.agrilink.agriculturalmanagementservice.securite.SecuriteService;
import com.agrilink.agriculturalmanagementservice.service.OffreService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OffreServiceImpl implements OffreService {

    private final OffreRepository offreRepository;
    private final ProduitRepository produitRepository;
    private final RecolteRepository recolteRepository;
    private final OffreMapper offreMapper;
    private final SecuriteService securiteService;

    public OffreServiceImpl(OffreRepository offreRepository,
                            ProduitRepository produitRepository,
                            RecolteRepository recolteRepository,
                            OffreMapper offreMapper,
                            SecuriteService securiteService) {
        this.offreRepository = offreRepository;
        this.produitRepository = produitRepository;
        this.recolteRepository = recolteRepository;
        this.offreMapper = offreMapper;
        this.securiteService = securiteService;
    }

    @Override
    public OffreReponseDTO creerOffre(OffreCreationDTO dto) {
        if (dto.produitId() == null) {
            throw new DonneesInvalidesException("L'identifiant du produit est obligatoire");
        }
        if (dto.agriculteurId() == null) {
            throw new DonneesInvalidesException("L'identifiant de l'agriculteur est obligatoire");
        }
        if (dto.prix() == null || dto.prix() <= 0) {
            throw new DonneesInvalidesException("Le prix de l'offre doit être strictement positif");
        }
        if (dto.quantite() == null || dto.quantite() <= 0) {
            throw new DonneesInvalidesException("La quantité de l'offre doit être strictement positive");
        }
        if (dto.dateDisponibilite() == null) {
            throw new DonneesInvalidesException("La date de disponibilité est obligatoire");
        }

        // Vérification de sécurité : l'agriculteur connecté ne peut créer une offre que pour lui-même
        securiteService.validerAccesAgriculteur(dto.agriculteurId());

        Produit produit = produitRepository.findById(dto.produitId())
                .orElseThrow(() -> new RessourceIntrouvableException("Produit introuvable avec l'identifiant : " + dto.produitId()));

        // Règle métier : la quantité de l'offre ne doit pas dépasser la quantité disponible de récolte pour ce produit et cet agriculteur
        Double totalDisponible = recolteRepository.sommeQuantiteDisponibleParProduitEtAgriculteur(dto.produitId(), dto.agriculteurId());
        double quantiteRecolteDisponible = totalDisponible != null ? totalDisponible : 0.0;
        if (dto.quantite() > quantiteRecolteDisponible) {
            throw new DonneesInvalidesException(
                    "La quantité de l'offre (" + dto.quantite() +
                            ") dépasse la quantité disponible des récoltes (" + quantiteRecolteDisponible + ") pour ce produit"
            );
        }

        Offre offre = offreMapper.toEntity(dto, produit);
        offre.setStatut(StatutOffre.BROUILLON);

        Offre enregistree = offreRepository.save(offre);
        return offreMapper.toDto(enregistree);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OffreReponseDTO> consulterOffres() {
        return offreRepository.findAll()
                .stream()
                .map(offreMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OffreReponseDTO consulterOffreParId(UUID id) {
        return offreRepository.findById(id)
                .map(offreMapper::toDto)
                .orElseThrow(() -> new RessourceIntrouvableException("Offre introuvable avec l'identifiant : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OffreReponseDTO> consulterOffresParAgriculteur(UUID agriculteurId) {
        return offreRepository.findByAgriculteurId(agriculteurId)
                .stream()
                .map(offreMapper::toDto)
                .toList();
    }

    @Override
    public OffreReponseDTO modifierOffre(UUID id, OffreCreationDTO dto) {
        Offre offre = offreRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Offre introuvable avec l'identifiant : " + id));

        // Vérification de sécurité : seul l'agriculteur propriétaire peut modifier son offre
        securiteService.validerAccesAgriculteur(offre.getAgriculteurId());

        if (dto.prix() == null || dto.prix() <= 0) {
            throw new DonneesInvalidesException("Le prix de l'offre doit être strictement positif");
        }
        if (dto.quantite() == null || dto.quantite() <= 0) {
            throw new DonneesInvalidesException("La quantité de l'offre doit être strictement positive");
        }

        Produit produit = null;
        UUID produitIdCible = dto.produitId() != null ? dto.produitId() : offre.getProduit().getId();
        if (dto.produitId() != null) {
            produit = produitRepository.findById(dto.produitId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Produit introuvable avec l'identifiant : " + dto.produitId()));
        }

        // Vérification du stock disponible
        Double totalDisponible = recolteRepository.sommeQuantiteDisponibleParProduitEtAgriculteur(produitIdCible, offre.getAgriculteurId());
        double quantiteRecolteDisponible = totalDisponible != null ? totalDisponible : 0.0;
        if (dto.quantite() > quantiteRecolteDisponible) {
            throw new DonneesInvalidesException(
                    "La quantité de l'offre (" + dto.quantite() +
                            ") dépasse la quantité disponible des récoltes (" + quantiteRecolteDisponible + ") pour ce produit"
            );
        }

        offreMapper.updateEntityFromDto(offre, dto, produit);
        Offre misAJour = offreRepository.save(offre);
        return offreMapper.toDto(misAJour);
    }

    @Override
    public OffreReponseDTO publierOffre(UUID id) {
        Offre offre = offreRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Offre introuvable avec l'identifiant : " + id));

        // Vérification de sécurité : seul l'agriculteur propriétaire peut publier son offre
        securiteService.validerAccesAgriculteur(offre.getAgriculteurId());

        offre.publierOffre();
        Offre enregistree = offreRepository.save(offre);
        return offreMapper.toDto(enregistree);
    }

    @Override
    public OffreReponseDTO retirerOffre(UUID id) {
        Offre offre = offreRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Offre introuvable avec l'identifiant : " + id));

        // Vérification de sécurité : seul l'agriculteur propriétaire peut retirer son offre
        securiteService.validerAccesAgriculteur(offre.getAgriculteurId());

        offre.retirerOffre();
        Offre enregistree = offreRepository.save(offre);
        return offreMapper.toDto(enregistree);
    }

    @Override
    public OffreReponseDTO archiverOffre(UUID id) {
        Offre offre = offreRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Offre introuvable avec l'identifiant : " + id));

        // Vérification de sécurité : seul l'agriculteur propriétaire peut archiver son offre
        securiteService.validerAccesAgriculteur(offre.getAgriculteurId());

        offre.archiverOffre();
        Offre enregistree = offreRepository.save(offre);
        return offreMapper.toDto(enregistree);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OffreReponseDTO> consulterOffresMarketplace(Categorie categorie,
                                                            Double prixMin,
                                                            Double prixMax,
                                                            UUID agriculteurId,
                                                            Pageable pageable) {
        Specification<Offre> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Règle métier : seules les offres au statut PUBLIEE sont visibles sur la Marketplace
            predicates.add(criteriaBuilder.equal(root.get("statut"), StatutOffre.PUBLIEE));

            if (categorie != null) {
                predicates.add(criteriaBuilder.equal(root.get("produit").get("categorie"), categorie));
            }
            if (prixMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("prix"), prixMin));
            }
            if (prixMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("prix"), prixMax));
            }
            if (agriculteurId != null) {
                predicates.add(criteriaBuilder.equal(root.get("agriculteurId"), agriculteurId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return offreRepository.findAll(spec, pageable).map(offreMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OffreReponseDTO consulterOffreMarketplaceParId(UUID id) {
        Offre offre = offreRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Offre introuvable sur la Marketplace avec l'identifiant : " + id));

        // Règle métier : les offres retirées, archivées ou brouillons ne doivent pas apparaître sur la Marketplace
        if (offre.getStatut() != StatutOffre.PUBLIEE) {
            throw new RessourceIntrouvableException("Cette offre n'est pas disponible sur la Marketplace");
        }

        return offreMapper.toDto(offre);
    }
}
