package com.agrilink.cooperative.service.impl;

import com.agrilink.cooperative.dto.BesoinClientRequest;
import com.agrilink.cooperative.dto.BesoinClientResponse;
import com.agrilink.cooperative.entity.BesoinClient;
import com.agrilink.cooperative.entity.StatutBesoin;
import com.agrilink.cooperative.exception.BusinessException;
import com.agrilink.cooperative.exception.ResourceNotFoundException;
import com.agrilink.cooperative.mapper.BesoinClientMapper;
import com.agrilink.cooperative.repository.BesoinClientRepository;
import com.agrilink.cooperative.service.BesoinClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BesoinClientServiceImpl implements BesoinClientService {

    private final BesoinClientRepository besoinClientRepository;
    private final BesoinClientMapper mapper;

    @Override
    public BesoinClientResponse createBesoin(BesoinClientRequest request) {
        BesoinClient entity = mapper.toEntity(request);
        BesoinClient saved = besoinClientRepository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BesoinClientResponse> getAllBesoins(Long clientId, Long productId, StatutBesoin statut) {
        List<BesoinClient> result;
        if (clientId != null) {
            result = besoinClientRepository.findByClientId(clientId);
        } else if (productId != null) {
            result = besoinClientRepository.findByProductId(productId);
        } else if (statut != null) {
            result = besoinClientRepository.findByStatut(statut);
        } else {
            result = besoinClientRepository.findAll();
        }
        return result.stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BesoinClientResponse getBesoinById(Long id) {
        BesoinClient entity = findById(id);
        return mapper.toResponse(entity);
    }

    @Override
    public BesoinClientResponse updateBesoin(Long id, BesoinClientRequest request) {
        BesoinClient entity = findById(id);
        if (entity.getStatut() == StatutBesoin.SATISFAIT || entity.getStatut() == StatutBesoin.ANNULE) {
            throw new BusinessException("Impossible de modifier un besoin déjà " + entity.getStatut());
        }
        mapper.updateEntityFromRequest(entity, request);
        return mapper.toResponse(besoinClientRepository.save(entity));
    }

    @Override
    public void deleteBesoin(Long id) {
        BesoinClient entity = findById(id);
        if (entity.getAnnonceCollecte() != null) {
            throw new BusinessException("Impossible de supprimer un besoin déjà pris en charge par une annonce de collecte.");
        }
        besoinClientRepository.delete(entity);
    }

    @Override
    public BesoinClientResponse annulerBesoin(Long id) {
        BesoinClient entity = findById(id);
        if (entity.getStatut() == StatutBesoin.SATISFAIT) {
            throw new BusinessException("Impossible d'annuler un besoin déjà satisfait.");
        }
        entity.setStatut(StatutBesoin.ANNULE);
        return mapper.toResponse(besoinClientRepository.save(entity));
    }

    private BesoinClient findById(Long id) {
        return besoinClientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Besoin client introuvable avec l'identifiant: " + id));
    }
}
