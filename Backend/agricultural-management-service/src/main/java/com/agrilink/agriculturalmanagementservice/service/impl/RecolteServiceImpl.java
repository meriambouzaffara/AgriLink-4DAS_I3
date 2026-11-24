package com.agrilink.agriculturalmanagementservice.service.impl;

import com.agrilink.agriculturalmanagementservice.dto.RecolteCreationDTO;
import com.agrilink.agriculturalmanagementservice.dto.RecolteReponseDTO;
import com.agrilink.agriculturalmanagementservice.entity.Produit;
import com.agrilink.agriculturalmanagementservice.entity.Recolte;
import com.agrilink.agriculturalmanagementservice.enums.StatutRecolte;
import com.agrilink.agriculturalmanagementservice.exception.DonneesInvalidesException;
import com.agrilink.agriculturalmanagementservice.exception.RessourceIntrouvableException;
import com.agrilink.agriculturalmanagementservice.mapper.RecolteMapper;
import com.agrilink.agriculturalmanagementservice.repository.ProduitRepository;
import com.agrilink.agriculturalmanagementservice.repository.RecolteRepository;
import com.agrilink.agriculturalmanagementservice.securite.SecuriteService;
import com.agrilink.agriculturalmanagementservice.service.RecolteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RecolteServiceImpl implements RecolteService {

    private final RecolteRepository recolteRepository;
    private final ProduitRepository produitRepository;
    private final RecolteMapper recolteMapper;
    private final SecuriteService securiteService;

    public RecolteServiceImpl(RecolteRepository recolteRepository,
                              ProduitRepository produitRepository,
                              RecolteMapper recolteMapper,
                              SecuriteService securiteService) {
        this.recolteRepository = recolteRepository;
        this.produitRepository = produitRepository;
        this.recolteMapper = recolteMapper;
        this.securiteService = securiteService;
    }

    @Override
    public RecolteReponseDTO declarerRecolte(RecolteCreationDTO dto) {
        if (dto.produitId() == null) {
            throw new DonneesInvalidesException("L'identifiant du produit est obligatoire");
        }
        if (dto.agriculteurId() == null) {
            throw new DonneesInvalidesException("L'identifiant de l'agriculteur est obligatoire");
        }
        if (dto.quantiteTotale() == null || dto.quantiteTotale() <= 0) {
            throw new DonneesInvalidesException("La quantité totale doit être strictement positive");
        }
        if (dto.dateRecolte() == null) {
            throw new DonneesInvalidesException("La date de récolte est obligatoire");
        }
        if (dto.dateDisponibilite() == null) {
            throw new DonneesInvalidesException("La date de disponibilité est obligatoire");
        }
        if (dto.dateDisponibilite().isBefore(dto.dateRecolte())) {
            throw new DonneesInvalidesException("La date de disponibilité doit être supérieure ou égale à la date de récolte");
        }

        // Vérification de sécurité : l'agriculteur connecté ne peut déclarer une récolte que pour lui-même
        securiteService.validerAccesAgriculteur(dto.agriculteurId());

        Produit produit = produitRepository.findById(dto.produitId())
                .orElseThrow(() -> new RessourceIntrouvableException("Produit introuvable avec l'identifiant : " + dto.produitId()));

        Recolte recolte = recolteMapper.toEntity(dto, produit);
        // Règles métier : à la création, quantité disponible = quantité totale et statut = DISPONIBLE
        recolte.setQuantiteDisponible(dto.quantiteTotale());
        recolte.setStatut(StatutRecolte.DISPONIBLE);

        Recolte enregistree = recolteRepository.save(recolte);
        return recolteMapper.toDto(enregistree);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecolteReponseDTO> consulterToutesLesRecoltes() {
        return recolteRepository.findAll()
                .stream()
                .map(recolteMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecolteReponseDTO consulterRecolteParId(UUID id) {
        return recolteRepository.findById(id)
                .map(recolteMapper::toDto)
                .orElseThrow(() -> new RessourceIntrouvableException("Récolte introuvable avec l'identifiant : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecolteReponseDTO> consulterRecoltesParAgriculteur(UUID agriculteurId) {
        return recolteRepository.findByAgriculteurId(agriculteurId)
                .stream()
                .map(recolteMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecolteReponseDTO> consulterRecoltesParProduit(UUID produitId) {
        return recolteRepository.findByProduitId(produitId)
                .stream()
                .map(recolteMapper::toDto)
                .toList();
    }

    @Override
    public RecolteReponseDTO modifierRecolte(UUID id, RecolteCreationDTO dto) {
        Recolte recolte = recolteRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Récolte introuvable avec l'identifiant : " + id));

        // Vérification de sécurité : seul le propriétaire peut modifier sa récolte
        securiteService.validerAccesAgriculteur(recolte.getAgriculteurId());

        if (dto.quantiteTotale() == null || dto.quantiteTotale() <= 0) {
            throw new DonneesInvalidesException("La quantité totale doit être strictement positive");
        }
        if (dto.dateDisponibilite() != null && dto.dateRecolte() != null && dto.dateDisponibilite().isBefore(dto.dateRecolte())) {
            throw new DonneesInvalidesException("La date de disponibilité doit être supérieure ou égale à la date de récolte");
        }

        Produit produit = null;
        if (dto.produitId() != null) {
            produit = produitRepository.findById(dto.produitId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Produit introuvable avec l'identifiant : " + dto.produitId()));
        }

        // Vérifier la cohérence de la quantité disponible par rapport à la nouvelle quantité totale
        double differenceQuantite = dto.quantiteTotale() - recolte.getQuantiteTotale();
        double nouvelleQuantiteDisponible = recolte.getQuantiteDisponible() + differenceQuantite;

        if (nouvelleQuantiteDisponible < 0) {
            throw new DonneesInvalidesException("La nouvelle quantité totale ne peut pas être inférieure aux quantités déjà engagées");
        }
        if (nouvelleQuantiteDisponible > dto.quantiteTotale()) {
            nouvelleQuantiteDisponible = dto.quantiteTotale();
        }

        recolte.setQuantiteTotale(dto.quantiteTotale());
        recolte.setQuantiteDisponible(nouvelleQuantiteDisponible);
        recolteMapper.updateEntityFromDto(recolte, dto, produit);

        Recolte misAJour = recolteRepository.save(recolte);
        return recolteMapper.toDto(misAJour);
    }

    @Override
    public RecolteReponseDTO modifierStatutRecolte(UUID id, StatutRecolte statut) {
        if (statut == null) {
            throw new DonneesInvalidesException("Le statut de la récolte est obligatoire");
        }

        Recolte recolte = recolteRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Récolte introuvable avec l'identifiant : " + id));

        // Vérification de sécurité : seul le propriétaire peut modifier le statut de sa récolte
        securiteService.validerAccesAgriculteur(recolte.getAgriculteurId());

        recolte.setStatut(statut);
        if (statut == StatutRecolte.INDISPONIBLE || statut == StatutRecolte.ENDOMMAGEE) {
            recolte.setQuantiteDisponible(0.0);
        }

        Recolte misAJour = recolteRepository.save(recolte);
        return recolteMapper.toDto(misAJour);
    }
}
