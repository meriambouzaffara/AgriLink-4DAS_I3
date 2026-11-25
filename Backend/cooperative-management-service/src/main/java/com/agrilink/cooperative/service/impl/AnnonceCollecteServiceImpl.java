package com.agrilink.cooperative.service.impl;

import com.agrilink.cooperative.dto.AnnonceCollecteRequest;
import com.agrilink.cooperative.dto.AnnonceCollecteResponse;
import com.agrilink.cooperative.entity.AnnonceCollecte;
import com.agrilink.cooperative.entity.BesoinClient;
import com.agrilink.cooperative.entity.StatutAnnonce;
import com.agrilink.cooperative.entity.StatutBesoin;
import com.agrilink.cooperative.exception.BusinessException;
import com.agrilink.cooperative.exception.ResourceNotFoundException;
import com.agrilink.cooperative.mapper.AnnonceCollecteMapper;
import com.agrilink.cooperative.repository.AnnonceCollecteRepository;
import com.agrilink.cooperative.repository.BesoinClientRepository;
import com.agrilink.cooperative.service.AnnonceCollecteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnonceCollecteServiceImpl implements AnnonceCollecteService {

    private final AnnonceCollecteRepository annonceCollecteRepository;
    private final BesoinClientRepository besoinClientRepository;
    private final AnnonceCollecteMapper mapper;

    @Override
    public AnnonceCollecteResponse createAnnonce(AnnonceCollecteRequest request) {
        BesoinClient besoin = besoinClientRepository.findById(request.getBesoinId())
                .orElseThrow(() -> new ResourceNotFoundException("Besoin client introuvable avec l'identifiant: " + request.getBesoinId()));

        if (annonceCollecteRepository.existsByBesoin_Id(besoin.getId())) {
            throw new BusinessException("Ce besoin client est déjà pris en charge par une annonce de collecte.");
        }

        if (besoin.getStatut() == StatutBesoin.ANNULE || besoin.getStatut() == StatutBesoin.SATISFAIT) {
            throw new BusinessException("Impossible de créer une annonce pour un besoin dans l'état: " + besoin.getStatut());
        }

        AnnonceCollecte annonce = mapper.toEntity(request, besoin);
        AnnonceCollecte saved = annonceCollecteRepository.save(annonce);

        // Mise à jour du statut du besoin
        besoin.setStatut(StatutBesoin.EN_COLLECTE);
        besoinClientRepository.save(besoin);

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnonceCollecteResponse> getAllAnnonces(StatutAnnonce statut, Long responsableId) {
        List<AnnonceCollecte> result;
        if (statut != null) {
            result = annonceCollecteRepository.findByStatut(statut);
        } else if (responsableId != null) {
            result = annonceCollecteRepository.findByResponsableId(responsableId);
        } else {
            result = annonceCollecteRepository.findAll();
        }
        return result.stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AnnonceCollecteResponse getAnnonceById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    public AnnonceCollecteResponse updateAnnonce(Long id, AnnonceCollecteRequest request) {
        AnnonceCollecte entity = findById(id);
        if (entity.getStatut() == StatutAnnonce.CLOTUREE || entity.getStatut() == StatutAnnonce.ANNULEE) {
            throw new BusinessException("Impossible de modifier une annonce clôturée ou annulée.");
        }
        mapper.updateEntityFromRequest(entity, request);
        return mapper.toResponse(annonceCollecteRepository.save(entity));
    }

    @Override
    public void deleteAnnonce(Long id) {
        AnnonceCollecte entity = findById(id);
        if (entity.getPropositions() != null && !entity.getPropositions().isEmpty()) {
            throw new BusinessException("Impossible de supprimer une annonce ayant déjà reçu des propositions de récolte.");
        }
        annonceCollecteRepository.delete(entity);
    }

    @Override
    public AnnonceCollecteResponse cloturerAnnonce(Long id) {
        AnnonceCollecte entity = findById(id);
        if (entity.getStatut() == StatutAnnonce.ANNULEE) {
            throw new BusinessException("Impossible de clôturer une annonce déjà annulée.");
        }
        entity.setStatut(StatutAnnonce.CLOTUREE);
        return mapper.toResponse(annonceCollecteRepository.save(entity));
    }

    @Override
    public AnnonceCollecteResponse annulerAnnonce(Long id) {
        AnnonceCollecte entity = findById(id);
        entity.setStatut(StatutAnnonce.ANNULEE);
        return mapper.toResponse(annonceCollecteRepository.save(entity));
    }

    private AnnonceCollecte findById(Long id) {
        return annonceCollecteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce de collecte introuvable avec l'identifiant: " + id));
    }
}
