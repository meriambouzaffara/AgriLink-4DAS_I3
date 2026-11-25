package com.agrilink.cooperative.service.impl;

import com.agrilink.cooperative.dto.PropositionRecolteRequest;
import com.agrilink.cooperative.dto.PropositionRecolteResponse;
import com.agrilink.cooperative.dto.ReceptionRecolteRequest;
import com.agrilink.cooperative.dto.TraiterPropositionRequest;
import com.agrilink.cooperative.entity.*;
import com.agrilink.cooperative.exception.BusinessException;
import com.agrilink.cooperative.exception.ResourceNotFoundException;
import com.agrilink.cooperative.mapper.PropositionRecolteMapper;
import com.agrilink.cooperative.repository.AnnonceCollecteRepository;
import com.agrilink.cooperative.repository.BesoinClientRepository;
import com.agrilink.cooperative.repository.PropositionRecolteRepository;
import com.agrilink.cooperative.service.PropositionRecolteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PropositionRecolteServiceImpl implements PropositionRecolteService {

    private final PropositionRecolteRepository propositionRecolteRepository;
    private final AnnonceCollecteRepository annonceCollecteRepository;
    private final BesoinClientRepository besoinClientRepository;
    private final PropositionRecolteMapper mapper;

    @Override
    public PropositionRecolteResponse createProposition(Long annonceId, PropositionRecolteRequest request) {
        AnnonceCollecte annonce = annonceCollecteRepository.findById(annonceId)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce de collecte introuvable avec l'identifiant: " + annonceId));

        if (annonce.getStatut() != StatutAnnonce.OUVERTE) {
            throw new BusinessException("Impossible de soumettre une proposition sur une annonce non ouverte (Statut: " + annonce.getStatut() + ").");
        }

        PropositionRecolte proposition = mapper.toEntity(request, annonce);
        PropositionRecolte saved = propositionRecolteRepository.save(proposition);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropositionRecolteResponse> getAllPropositions(Long annonceId, Long agriculteurId, StatutProposition statut) {
        List<PropositionRecolte> result;
        if (annonceId != null && statut != null) {
            result = propositionRecolteRepository.findByAnnonce_IdAndStatut(annonceId, statut);
        } else if (annonceId != null) {
            result = propositionRecolteRepository.findByAnnonce_Id(annonceId);
        } else if (agriculteurId != null) {
            result = propositionRecolteRepository.findByAgriculteurId(agriculteurId);
        } else {
            result = propositionRecolteRepository.findAll();
        }
        return result.stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PropositionRecolteResponse getPropositionById(Long id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    public PropositionRecolteResponse traiterDecision(Long id, TraiterPropositionRequest request) {
        PropositionRecolte proposition = findById(id);

        if (proposition.getStatut() == StatutProposition.RECUE || proposition.getStatut() == StatutProposition.RECUE_PARTIELLEMENT) {
            throw new BusinessException("Impossible de modifier la décision sur une proposition déjà reçue.");
        }

        StatutProposition nouveauStatut = request.getStatut();

        if (nouveauStatut == StatutProposition.ACCEPTEE || nouveauStatut == StatutProposition.ACCEPTEE_PARTIELLEMENT) {
            BigDecimal qteAcceptee = request.getQuantiteAcceptee();
            if (qteAcceptee == null || qteAcceptee.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("La quantité acceptée doit être supérieure à zéro.");
            }
            if (qteAcceptee.compareTo(proposition.getQuantiteProposee()) > 0) {
                throw new BusinessException("La quantité acceptée ne peut pas dépasser la quantité proposée (" + proposition.getQuantiteProposee() + ").");
            }
            proposition.setQuantiteAcceptee(qteAcceptee);
            proposition.setStatut(nouveauStatut);
        } else if (nouveauStatut == StatutProposition.REFUSEE) {
            proposition.setQuantiteAcceptee(BigDecimal.ZERO);
            proposition.setStatut(StatutProposition.REFUSEE);
        } else {
            throw new BusinessException("Statut de décision non valide: " + nouveauStatut);
        }

        PropositionRecolte saved = propositionRecolteRepository.save(proposition);

        // Recalcul de la quantité acceptée totale pour l'annonce
        AnnonceCollecte annonce = proposition.getAnnonce();
        recalculerQuantiteAccepteeAnnonce(annonce);

        return mapper.toResponse(saved);
    }

    @Override
    public PropositionRecolteResponse enregistrerReception(Long id, ReceptionRecolteRequest request) {
        PropositionRecolte proposition = findById(id);

        if (proposition.getStatut() != StatutProposition.ACCEPTEE && proposition.getStatut() != StatutProposition.ACCEPTEE_PARTIELLEMENT) {
            throw new BusinessException("Seules les propositions acceptées peuvent faire l'objet d'une réception au dépôt.");
        }

        BigDecimal qteRecue = request.getQuantiteRecue();
        if (qteRecue == null || qteRecue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("La quantité reçue doit être supérieure à zéro.");
        }

        proposition.setQuantiteRecue(qteRecue);
        proposition.setDateReception(LocalDateTime.now());

        if (qteRecue.compareTo(proposition.getQuantiteAcceptee()) >= 0) {
            proposition.setStatut(StatutProposition.RECUE);
        } else {
            proposition.setStatut(StatutProposition.RECUE_PARTIELLEMENT);
        }

        PropositionRecolte saved = propositionRecolteRepository.save(proposition);

        // =========================================================================
        // APPLICATION STRICTE DE LA RÈGLE MÉTIER (Cahier des charges) :
        // "Un besoin est considéré comme satisfait uniquement lorsque la quantité
        // réellement reçue couvre la quantité demandée, et non sur la base des
        // seules quantités acceptées."
        // =========================================================================
        AnnonceCollecte annonce = proposition.getAnnonce();
        if (annonce != null && annonce.getBesoin() != null) {
            BesoinClient besoin = annonce.getBesoin();

            List<PropositionRecolte> toutesPropositions = propositionRecolteRepository.findByAnnonce_Id(annonce.getId());
            BigDecimal sommeQuantiteRecue = toutesPropositions.stream()
                    .map(p -> p.getQuantiteRecue() != null ? p.getQuantiteRecue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (besoin.estCouvertParQuantiteRecue(sommeQuantiteRecue)) {
                besoin.setStatut(StatutBesoin.SATISFAIT);
                besoinClientRepository.save(besoin);
            }
        }

        return mapper.toResponse(saved);
    }

    @Override
    public void deleteProposition(Long id) {
        PropositionRecolte entity = findById(id);
        if (entity.getStatut() == StatutProposition.RECUE || entity.getStatut() == StatutProposition.RECUE_PARTIELLEMENT) {
            throw new BusinessException("Impossible de supprimer une proposition déjà reçue.");
        }
        AnnonceCollecte annonce = entity.getAnnonce();
        propositionRecolteRepository.delete(entity);
        recalculerQuantiteAccepteeAnnonce(annonce);
    }

    private PropositionRecolte findById(Long id) {
        return propositionRecolteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proposition de récolte introuvable avec l'identifiant: " + id));
    }

    private void recalculerQuantiteAccepteeAnnonce(AnnonceCollecte annonce) {
        if (annonce == null) return;
        List<PropositionRecolte> propositions = propositionRecolteRepository.findByAnnonce_Id(annonce.getId());
        BigDecimal totalAcceptee = propositions.stream()
                .filter(p -> p.getStatut() == StatutProposition.ACCEPTEE
                        || p.getStatut() == StatutProposition.ACCEPTEE_PARTIELLEMENT
                        || p.getStatut() == StatutProposition.RECUE
                        || p.getStatut() == StatutProposition.RECUE_PARTIELLEMENT)
                .map(p -> p.getQuantiteAcceptee() != null ? p.getQuantiteAcceptee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        annonce.setQuantiteAcceptee(totalAcceptee);
        if (totalAcceptee.compareTo(annonce.getQuantiteObjectif()) >= 0 && annonce.getStatut() == StatutAnnonce.OUVERTE) {
            annonce.setStatut(StatutAnnonce.OBJECTIF_ATTEINT);
        }
        annonceCollecteRepository.save(annonce);
    }
}
