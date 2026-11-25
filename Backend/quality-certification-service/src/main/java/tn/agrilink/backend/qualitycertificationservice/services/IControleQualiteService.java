package tn.agrilink.backend.qualitycertificationservice.services;


import tn.agrilink.backend.qualitycertificationservice.dto.ControleQualiteDTO;
import tn.agrilink.backend.qualitycertificationservice.enums.StatutControle;

import java.util.List;
import java.util.UUID;

public interface IControleQualiteService {

    ControleQualiteDTO add(ControleQualiteDTO dto);

    ControleQualiteDTO update(Long id, ControleQualiteDTO dto);

    ControleQualiteDTO retrieve(Long id);

    List<ControleQualiteDTO> retrieveAll();

    void cancel(Long id);

    ControleQualiteDTO changerStatut(
            Long id,
            StatutControle statut
    );

    List<ControleQualiteDTO> historique(UUID recolteId);
}
