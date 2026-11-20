package tn.agrilink.backend.qualitycertificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.agrilink.backend.qualitycertificationservice.entity.ControleQualite;

import java.util.List;
import java.util.UUID;

@Repository
public interface ControleQualiteRepository
        extends JpaRepository<ControleQualite, Long> {

    // Consulter l'historique des contrôles d'une récolte
    List<ControleQualite> findByRecolteId(UUID recolteId);
}
