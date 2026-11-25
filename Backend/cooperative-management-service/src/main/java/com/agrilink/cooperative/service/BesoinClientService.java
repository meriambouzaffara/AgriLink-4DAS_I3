package com.agrilink.cooperative.service;

import com.agrilink.cooperative.dto.BesoinClientRequest;
import com.agrilink.cooperative.dto.BesoinClientResponse;
import com.agrilink.cooperative.entity.StatutBesoin;

import java.util.List;

public interface BesoinClientService {

    BesoinClientResponse createBesoin(BesoinClientRequest request);

    List<BesoinClientResponse> getAllBesoins(Long clientId, Long productId, StatutBesoin statut);

    BesoinClientResponse getBesoinById(Long id);

    BesoinClientResponse updateBesoin(Long id, BesoinClientRequest request);

    void deleteBesoin(Long id);

    BesoinClientResponse annulerBesoin(Long id);
}
