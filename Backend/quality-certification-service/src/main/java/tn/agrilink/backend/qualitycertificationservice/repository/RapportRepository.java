package tn.agrilink.backend.qualitycertificationservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.agrilink.backend.qualitycertificationservice.entity.Rapport;

import java.util.List;

@Repository
public interface RapportRepository
        extends JpaRepository<Rapport, Long> {

    // Consulter les rapports d'un responsable
    List<Rapport> findByIdResponsable(Long idResponsable);
}
