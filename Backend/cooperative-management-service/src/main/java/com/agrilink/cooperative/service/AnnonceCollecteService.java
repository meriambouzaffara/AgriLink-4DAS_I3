package com.agrilink.cooperative.service;

import com.agrilink.cooperative.dto.AnnonceCollecteRequest;
import com.agrilink.cooperative.dto.AnnonceCollecteResponse;
import com.agrilink.cooperative.entity.StatutAnnonce;

import java.util.List;

public interface AnnonceCollecteService {

    AnnonceCollecteResponse createAnnonce(AnnonceCollecteRequest request);

    List<AnnonceCollecteResponse> getAllAnnonces(StatutAnnonce statut, Long responsableId);

    AnnonceCollecteResponse getAnnonceById(Long id);

    AnnonceCollecteResponse updateAnnonce(Long id, AnnonceCollecteRequest request);

    void deleteAnnonce(Long id);

    AnnonceCollecteResponse cloturerAnnonce(Long id);

    AnnonceCollecteResponse annulerAnnonce(Long id);
}
