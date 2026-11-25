package tn.agrilink.backend.qualitycertificationservice.services;
import tn.agrilink.backend.qualitycertificationservice.dto.RapportDTO;
import org.springframework.core.io.Resource;
import tn.agrilink.backend.qualitycertificationservice.dto.GenererRapportDTO;
import java.util.List;

public interface IRapportService {

    // Consulter un rapport
    RapportDTO retrieve(Long id);

    // Consulter tous les rapports
    List<RapportDTO> retrieveAll();

    // Consulter les rapports d'un responsable
    List<RapportDTO> retrieveByResponsable(Long idResponsable);

    // Générer un rapport PDF de contrôles qualité
    RapportDTO genererRapportControles(GenererRapportDTO dto);

    // Générer un rapport PDF de certifications
    RapportDTO genererRapportCertifications(GenererRapportDTO dto);

    // Télécharger un rapport PDF
    Resource telechargerRapport(Long id);
}


