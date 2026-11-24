package com.agrilink.agriculturalmanagementservice.repository;

import com.agrilink.agriculturalmanagementservice.entity.Offre;
import com.agrilink.agriculturalmanagementservice.enums.StatutOffre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OffreRepository extends JpaRepository<Offre, UUID>, JpaSpecificationExecutor<Offre> {

    @Override
    @EntityGraph(attributePaths = {"produit"})
    List<Offre> findAll();

    @Override
    @EntityGraph(attributePaths = {"produit"})
    Optional<Offre> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"produit"})
    Page<Offre> findAll(Specification<Offre> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"produit"})
    List<Offre> findByAgriculteurId(UUID agriculteurId);

    @EntityGraph(attributePaths = {"produit"})
    List<Offre> findByProduitId(UUID produitId);

    boolean existsByProduitId(UUID produitId);

    @EntityGraph(attributePaths = {"produit"})
    List<Offre> findByStatut(StatutOffre statut);
}
