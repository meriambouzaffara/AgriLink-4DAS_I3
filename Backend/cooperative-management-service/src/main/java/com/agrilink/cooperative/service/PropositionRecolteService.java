package com.agrilink.cooperative.service;

import com.agrilink.cooperative.dto.PropositionRecolteRequest;
import com.agrilink.cooperative.dto.PropositionRecolteResponse;
import com.agrilink.cooperative.dto.ReceptionRecolteRequest;
import com.agrilink.cooperative.dto.TraiterPropositionRequest;
import com.agrilink.cooperative.entity.StatutProposition;

import java.util.List;

public interface PropositionRecolteService {

    PropositionRecolteResponse createProposition(Long annonceId, PropositionRecolteRequest request);

    List<PropositionRecolteResponse> getAllPropositions(Long annonceId, Long agriculteurId, StatutProposition statut);

    PropositionRecolteResponse getPropositionById(Long id);

    PropositionRecolteResponse traiterDecision(Long id, TraiterPropositionRequest request);

    PropositionRecolteResponse enregistrerReception(Long id, ReceptionRecolteRequest request);

    void deleteProposition(Long id);
}
