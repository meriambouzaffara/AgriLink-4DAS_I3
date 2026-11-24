package com.agrilink.agriculturalmanagementservice.repository;

import com.agrilink.agriculturalmanagementservice.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, UUID> {

    List<Produit> findByCreeParAgriculteurId(UUID creeParAgriculteurId);

    boolean existsByCreeParAgriculteurId(UUID creeParAgriculteurId);
}
