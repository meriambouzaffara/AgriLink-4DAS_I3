package com.agrilink.cooperative.repository;

import com.agrilink.cooperative.entity.AnnonceCollecte;
import com.agrilink.cooperative.entity.StatutAnnonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnonceCollecteRepository extends JpaRepository<AnnonceCollecte, Long> {

    List<AnnonceCollecte> findByStatut(StatutAnnonce statut);

    List<AnnonceCollecte> findByResponsableId(Long responsableId);

    Optional<AnnonceCollecte> findByBesoinId(Long besoinId);

    boolean existsByBesoinId(Long besoinId);
}
