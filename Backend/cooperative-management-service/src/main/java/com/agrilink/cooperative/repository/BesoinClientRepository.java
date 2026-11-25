package com.agrilink.cooperative.repository;

import com.agrilink.cooperative.entity.BesoinClient;
import com.agrilink.cooperative.entity.StatutBesoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BesoinClientRepository extends JpaRepository<BesoinClient, Long> {

    List<BesoinClient> findByClientId(Long clientId);

    List<BesoinClient> findByProductId(Long productId);

    List<BesoinClient> findByStatut(StatutBesoin statut);
}
