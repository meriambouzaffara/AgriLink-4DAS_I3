package com.agrilink.cooperative.repository;

import com.agrilink.cooperative.entity.PropositionRecolte;
import com.agrilink.cooperative.entity.StatutProposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropositionRecolteRepository extends JpaRepository<PropositionRecolte, Long> {

    List<PropositionRecolte> findByAnnonceId(Long annonceId);

    List<PropositionRecolte> findByAgriculteurId(Long agriculteurId);

    List<PropositionRecolte> findByAnnonceIdAndStatut(Long annonceId, StatutProposition statut);
}
