package tn.agrilink.backend.qualitycertificationservice.services;

import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import tn.agrilink.backend.qualitycertificationservice.dto.GenererRapportDTO;
import tn.agrilink.backend.qualitycertificationservice.dto.RapportDTO;
import tn.agrilink.backend.qualitycertificationservice.entity.Certification;
import tn.agrilink.backend.qualitycertificationservice.entity.ControleQualite;
import tn.agrilink.backend.qualitycertificationservice.entity.Rapport;
import tn.agrilink.backend.qualitycertificationservice.enums.TypeRapport;
import tn.agrilink.backend.qualitycertificationservice.exception.BusinessException;
import tn.agrilink.backend.qualitycertificationservice.exception.ResourceNotFoundException;
import tn.agrilink.backend.qualitycertificationservice.mapper.RapportMapper;
import tn.agrilink.backend.qualitycertificationservice.repository.CertificationRepository;
import tn.agrilink.backend.qualitycertificationservice.repository.ControleQualiteRepository;
import tn.agrilink.backend.qualitycertificationservice.repository.RapportRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RapportService implements IRapportService {

    private RapportRepository repo;
    private RapportMapper mapper;
    private ControleQualiteRepository controleRepo;
    private CertificationRepository certificationRepo;
    private RapportPdfService rapportPdfService;
    @Override
    public RapportDTO retrieve(Long id) {

        Rapport rapport = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rapport introuvable avec ID : " + id
                ));

        return mapper.toDTO(rapport);
    }

    @Override
    public List<RapportDTO> retrieveAll() {

        return repo.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<RapportDTO> retrieveByResponsable(Long idResponsable) {

        return repo.findByIdResponsable(idResponsable)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public RapportDTO genererRapportControles(GenererRapportDTO dto) {

        // Vérifier les informations reçues
        if (dto.getIdResponsable() == null ||
                dto.getIds() == null ||
                dto.getIds().isEmpty()) {
            throw new BusinessException("Les informations du rapport sont invalides.");
        }

        // Récupérer les contrôles sélectionnés
        List<Long> ids = dto.getIds().stream()
                .distinct()
                .toList();

        List<ControleQualite> controles = controleRepo.findAllById(ids)
                .stream()
                .toList();

        // Vérifier que tous les contrôles existent
        if (controles.size() != ids.size()) {
            throw new ResourceNotFoundException(
                    "Un ou plusieurs contrôles qualité sont introuvables."
            );
        }

        // Préparer les données du PDF
        List<String[]> donnees = new ArrayList<>();

        donnees.add(new String[]{
                "ID",
                "Date",
                "Type",
                "Décision",
                "Statut",
                "Récolte"
        });

        for (ControleQualite controle : controles) {
            donnees.add(new String[]{
                    String.valueOf(controle.getIdControle()),
                    String.valueOf(controle.getDateControle()),
                    String.valueOf(controle.getTypeControle()),
                    String.valueOf(controle.getDecision()),
                    String.valueOf(controle.getStatut()),
                    String.valueOf(controle.getRecolteId())
            });
        }

        // Générer le fichier PDF
        String nomFichier = "rapport_controles_" + UUID.randomUUID() + ".pdf";

        String cheminPdf = rapportPdfService.genererPdf(
                "Rapport des contrôles qualité",
                donnees,
                nomFichier
        );

        // Créer le rapport
        Rapport rapport = new Rapport();
        rapport.setTypeRapport(TypeRapport.CONTROLE_QUALITE);
        rapport.setDateGeneration(LocalDateTime.now());
        rapport.setUrlRapport(cheminPdf);
        rapport.setIdResponsable(dto.getIdResponsable());

        // Associer les contrôles au rapport
        for (ControleQualite controle : controles) {
            rapport.ajouterControle(controle);
        }

        // Enregistrer le rapport
        Rapport rapportEnregistre = repo.save(rapport);

        return mapper.toDTO(rapportEnregistre);
    }
    @Override
    public RapportDTO genererRapportCertifications(GenererRapportDTO dto) {

        // Vérifier les informations reçues
        if (dto.getIdResponsable() == null ||
                dto.getIds() == null ||
                dto.getIds().isEmpty()) {
            throw new BusinessException("Les informations du rapport sont invalides.");
        }

        // Récupérer les certifications sélectionnées
        List<Long> ids = dto.getIds().stream()
                .distinct()
                .toList();

        List<Certification> certifications = certificationRepo.findAllById(ids)
                .stream()
                .toList();

        // Vérifier que toutes les certifications existent
        if (certifications.size() != ids.size()) {
            throw new ResourceNotFoundException(
                    "Une ou plusieurs certifications sont introuvables."
            );
        }

        // Préparer les données du PDF
        List<String[]> donnees = new ArrayList<>();

        donnees.add(new String[]{
                "ID",
                "Nom",
                "Numéro",
                "Date délivrance",
                "Date expiration",
                "Statut",
                "Récolte"
        });

        for (Certification certification : certifications) {
            donnees.add(new String[]{
                    String.valueOf(certification.getIdCertification()),
                    certification.getNomCertification(),
                    certification.getNumeroCertification(),
                    String.valueOf(certification.getDateDelivrance()),
                    String.valueOf(certification.getDateExpiration()),
                    String.valueOf(certification.getStatut()),
                    String.valueOf(certification.getRecolteId())
            });
        }

        // Générer le fichier PDF
        String nomFichier = "rapport_certifications_" + UUID.randomUUID() + ".pdf";

        String cheminPdf = rapportPdfService.genererPdf(
                "Rapport des certifications agricoles",
                donnees,
                nomFichier
        );

        // Créer le rapport
        Rapport rapport = new Rapport();
        rapport.setTypeRapport(TypeRapport.CERTIFICATION);
        rapport.setDateGeneration(LocalDateTime.now());
        rapport.setUrlRapport(cheminPdf);
        rapport.setIdResponsable(dto.getIdResponsable());

        // Associer les certifications au rapport
        for (Certification certification : certifications) {
            rapport.ajouterCertification(certification);
        }

        // Enregistrer le rapport
        Rapport rapportEnregistre = repo.save(rapport);

        return mapper.toDTO(rapportEnregistre);
    }
    @Override
    public Resource telechargerRapport(Long id) {

        // Rechercher le rapport
        Rapport rapport = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rapport introuvable avec ID : " + id
                ));

        // Récupérer le chemin du fichier PDF
        Path chemin = Path.of(rapport.getUrlRapport());

        // Vérifier que le fichier existe
        if (!Files.exists(chemin) || !Files.isRegularFile(chemin)) {
            throw new ResourceNotFoundException(
                    "Le fichier PDF de ce rapport est introuvable."
            );
        }

        // Retourner le fichier
        return new FileSystemResource(chemin);
    }
}
