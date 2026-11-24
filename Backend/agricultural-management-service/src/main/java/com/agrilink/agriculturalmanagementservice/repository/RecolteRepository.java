package com.agrilink.agriculturalmanagementservice.repository;

import com.agrilink.agriculturalmanagementservice.entity.Recolte;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecolteRepository extends JpaRepository<Recolte, UUID> {

    @Override
    @EntityGraph(attributePaths = {"produit"})
    List<Recolte> findAll();

    @Override
    @EntityGraph(attributePaths = {"produit"})
    Optional<Recolte> findById(UUID id);

    @EntityGraph(attributePaths = {"produit"})
    List<Recolte> findByAgriculteurId(UUID agriculteurId);

    @EntityGraph(attributePaths = {"produit"})
    List<Recolte> findByProduitId(UUID produitId);

    boolean existsByProduitId(UUID produitId);

    List<Recolte> findByProduitIdAndAgriculteurId(UUID produitId, UUID agriculteurId);

    @Query("SELECT COALESCE(SUM(r.quantiteDisponible), 0.0) FROM Recolte r " +
            "WHERE r.produit.id = :produitId AND r.agriculteurId = :agriculteurId " +
            "AND r.statut IN (com.agrilink.agriculturalmanagementservice.enums.StatutRecolte.DISPONIBLE, " +
            "com.agrilink.agriculturalmanagementservice.enums.StatutRecolte.PARTIELLEMENT_DISPONIBLE)")
    Double sommeQuantiteDisponibleParProduitEtAgriculteur(@Param("produitId") UUID produitId,
                                                         @Param("agriculteurId") UUID agriculteurId);
}
