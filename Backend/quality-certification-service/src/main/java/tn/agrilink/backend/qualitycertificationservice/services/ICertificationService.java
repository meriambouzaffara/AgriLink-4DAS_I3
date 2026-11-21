package tn.agrilink.backend.qualitycertificationservice.services;


import tn.agrilink.backend.qualitycertificationservice.dto.CertificationDTO;
import tn.agrilink.backend.qualitycertificationservice.enums.StatutCertification;

import java.util.List;
import java.util.UUID;

public interface ICertificationService {

    CertificationDTO add(CertificationDTO dto);

    CertificationDTO update(Long id, CertificationDTO dto);

    CertificationDTO retrieve(Long id);

    List<CertificationDTO> retrieveAll();

    void archive(Long id);

    CertificationDTO changerStatut(
            Long id,
            StatutCertification statut
    );

    List<CertificationDTO> rechercherParNom(String nom);

    List<CertificationDTO> rechercherParRecolte(UUID recolteId);
}
