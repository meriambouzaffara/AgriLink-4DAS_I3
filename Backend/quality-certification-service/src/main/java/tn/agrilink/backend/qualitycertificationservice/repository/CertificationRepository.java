package tn.agrilink.backend.qualitycertificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.agrilink.backend.qualitycertificationservice.entity.Certification;

import java.util.List;
import java.util.UUID;

@Repository
public interface CertificationRepository
        extends JpaRepository<Certification, Long> {

    // Rechercher les certifications d'une récolte
    List<Certification> findByRecolteId(UUID recolteId);

    // Rechercher une certification par son numéro
    boolean existsByNumeroCertification(String numeroCertification);

    // Rechercher par nom de certification
    List<Certification> findByNomCertificationContainingIgnoreCase(
            String nomCertification
    );

    boolean existsByNumeroCertificationAndIdCertificationNot(
            String numeroCertification,
            Long idCertification
    );
}
