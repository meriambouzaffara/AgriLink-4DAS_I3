package tn.agrilink.backend.qualitycertificationservice.services;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.agrilink.backend.qualitycertificationservice.dto.CertificationDTO;
import tn.agrilink.backend.qualitycertificationservice.entity.Certification;
import tn.agrilink.backend.qualitycertificationservice.enums.StatutCertification;
import tn.agrilink.backend.qualitycertificationservice.exception.BusinessException;
import tn.agrilink.backend.qualitycertificationservice.exception.ResourceNotFoundException;
import tn.agrilink.backend.qualitycertificationservice.services.ICertificationService;
import tn.agrilink.backend.qualitycertificationservice.mapper.CertificationMapper;
import tn.agrilink.backend.
        qualitycertificationservice.repository.CertificationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CertificationService implements ICertificationService {

    private CertificationRepository repo;
    private CertificationMapper mapper;

    @Override
    public CertificationDTO add(CertificationDTO dto) {

        if (dto.getDateDelivrance() == null
                || dto.getDateExpiration() == null
                || dto.getDateExpiration().isBefore(dto.getDateDelivrance())) {

            throw new BusinessException(
                    "La date d'expiration doit être postérieure ou égale à la date de délivrance."
            );
        }

        if (repo.existsByNumeroCertification(dto.getNumeroCertification())) {
            throw new BusinessException(
                    "Ce numéro de certification existe déjà."
            );
        }

        Certification certification = mapper.toEntity(dto);

        if (certification.getStatut() == null) {
            certification.setStatut(StatutCertification.VALIDEE);
        }

        Certification saved = repo.save(certification);

        return mapper.toDTO(saved);
    }


    @Override

    public CertificationDTO update(Long id, CertificationDTO dto) {

        Certification existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certification introuvable avec ID : " + id
                ));

        // Une certification archivée ne peut pas être modifiée
        if (existing.getStatut() == StatutCertification.ARCHIVEE) {
            throw new BusinessException(
                    "Impossible de modifier une certification archivée."
            );
        }

        // Vérifier les champs obligatoires
        if (dto == null
                || dto.getNomCertification() == null
                || dto.getNomCertification().isBlank()
                || dto.getNumeroCertification() == null
                || dto.getNumeroCertification().isBlank()) {

            throw new BusinessException(
                    "Le nom et le numéro de certification sont obligatoires."
            );
        }

        // Vérifier les dates
        if (dto.getDateDelivrance() == null
                || dto.getDateExpiration() == null
                || dto.getDateExpiration().isBefore(dto.getDateDelivrance())) {

            throw new BusinessException(
                    "Les dates de la certification sont invalides."
            );
        }

        // Vérifier l'unicité du numéro
        if (repo.existsByNumeroCertificationAndIdCertificationNot(
                dto.getNumeroCertification(), id)) {

            throw new BusinessException(
                    "Ce numéro de certification est déjà utilisé."
            );
        }

        // Modifier les informations
        existing.setNomCertification(dto.getNomCertification());
        existing.setNumeroCertification(dto.getNumeroCertification());
        existing.setDateDelivrance(dto.getDateDelivrance());
        existing.setDateExpiration(dto.getDateExpiration());
        existing.setDocumentCertificat(dto.getDocumentCertificat());
        existing.setRecolteId(dto.getRecolteId());
        existing.setIdResponsable(dto.getIdResponsable());

        // Synchroniser le statut avec la date d'expiration
        if (dto.getDateExpiration().isBefore(LocalDate.now())) {
            existing.setStatut(StatutCertification.EXPIREE);
        } else {
            existing.setStatut(StatutCertification.VALIDEE);
        }

        Certification saved = repo.save(existing);

        return mapper.toDTO(saved);
    }



    @Override
    public CertificationDTO retrieve(Long id) {

        Certification certification = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certification introuvable avec ID : " + id
                ));

        return mapper.toDTO(certification);
    }

    @Override
    public List<CertificationDTO> retrieveAll() {

        return repo.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public void archive(Long id) {

        Certification certification = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certification introuvable avec ID : " + id
                ));

        certification.setStatut(StatutCertification.ARCHIVEE);

        repo.save(certification);
    }


    @Override
    public CertificationDTO changerStatut(
            Long id,
            StatutCertification statut) {

        Certification certification = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certification introuvable avec ID : " + id
                ));

        if (statut == null) {
            throw new BusinessException("Le statut est obligatoire.");
        }

        // Une certification archivée est définitive
        if (certification.getStatut() == StatutCertification.ARCHIVEE) {
            throw new BusinessException(
                    "Impossible de modifier une certification archivée."
            );
        }

        // L'archivage possède son propre endpoint
        if (statut == StatutCertification.ARCHIVEE) {
            throw new BusinessException(
                    "Utilisez l'endpoint d'archivage pour archiver une certification."
            );
        }

        if (certification.getDateExpiration() == null) {
            throw new BusinessException(
                    "La date d'expiration de la certification est obligatoire."
            );
        }

        // Vérifier si la date d'expiration est dépassée
        boolean estExpiree = certification.getDateExpiration()
                .isBefore(java.time.LocalDate.now());

        // Une certification ne peut pas expirer avant sa date
        if (statut == StatutCertification.EXPIREE && !estExpiree) {
            throw new BusinessException(
                    "Impossible d'expirer une certification dont la date d'expiration n'est pas dépassée."
            );
        }

        // Une certification déjà expirée ne peut pas être réactivée manuellement
        if (statut == StatutCertification.VALIDEE && estExpiree) {
            throw new BusinessException(
                    "Impossible de valider une certification dont la date d'expiration est dépassée."
            );
        }

        certification.setStatut(statut);

        return mapper.toDTO(repo.save(certification));
    }


    @Override
    public List<CertificationDTO> rechercherParNom(String nom) {

        return repo.findByNomCertificationContainingIgnoreCase(nom)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<CertificationDTO> rechercherParRecolte(UUID recolteId) {

        return repo.findByRecolteId(recolteId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}
