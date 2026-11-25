
package tn.agrilink.backend.qualitycertificationservice.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.agrilink.backend.qualitycertificationservice.dto.ControleQualiteDTO;
import tn.agrilink.backend.qualitycertificationservice.entity.ControleQualite;
import tn.agrilink.backend.qualitycertificationservice.enums.StatutControle;
import tn.agrilink.backend.qualitycertificationservice.exception.BusinessException;
import tn.agrilink.backend.qualitycertificationservice.exception.ResourceNotFoundException;
import tn.agrilink.backend.qualitycertificationservice.mapper.ControleQualiteMapper;
import tn.agrilink.backend.qualitycertificationservice.repository.ControleQualiteRepository;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ControleQualiteService implements IControleQualiteService {

    private final ControleQualiteRepository repo;
    private final ControleQualiteMapper mapper;

    // Vérifier les informations obligatoires
    private void validerDonnees(ControleQualiteDTO dto) {

        if (dto == null) {
            throw new BusinessException("Les données du contrôle sont obligatoires.");
        }

        if (dto.getDateControle() == null) {
            throw new BusinessException("La date du contrôle est obligatoire.");
        }

        if (dto.getTypeControle() == null) {
            throw new BusinessException("Le type de contrôle est obligatoire.");
        }

        if (dto.getRecolteId() == null) {
            throw new BusinessException("L'identifiant de la récolte est obligatoire.");
        }

        if (dto.getIdResponsable() == null || dto.getIdResponsable() <= 0) {
            throw new BusinessException("L'identifiant du responsable est invalide.");
        }
    }

    // Ajouter un contrôle
    @Override
    public ControleQualiteDTO add(ControleQualiteDTO dto) {

        validerDonnees(dto);

        // Un nouveau contrôle ne peut pas être directement terminé ou annulé
        if (dto.getStatut() != null
                && dto.getStatut() != StatutControle.EN_COURS) {

            throw new BusinessException(
                    "Un nouveau contrôle doit commencer avec le statut EN_COURS."
            );
        }

        // La décision sera renseignée pendant le contrôle
        if (dto.getDecision() != null) {
            throw new BusinessException(
                    "La décision qualité doit être renseignée pendant le contrôle."
            );
        }

        ControleQualite controle = mapper.toEntity(dto);

        // Génération automatique de l'identifiant
        controle.setIdControle(null);

        // Statut initial obligatoire
        controle.setStatut(StatutControle.EN_COURS);

        ControleQualite saved = repo.save(controle);

        return mapper.toDTO(saved);
    }

    // Modifier un contrôle
    @Override
    public ControleQualiteDTO update(Long id, ControleQualiteDTO dto) {

        ControleQualite existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contrôle qualité introuvable avec ID : " + id
                ));

        // Seul un contrôle en cours peut être modifié
        if (existing.getStatut() != StatutControle.EN_COURS) {
            throw new BusinessException(
                    "Impossible de modifier un contrôle terminé ou annulé."
            );
        }

        validerDonnees(dto);

        // PUT ne permet pas de changer le statut
        if (dto.getStatut() != null
                && dto.getStatut() != StatutControle.EN_COURS) {

            throw new BusinessException(
                    "Utilisez l'endpoint de changement de statut."
            );
        }

        // La récolte associée ne peut pas être changée
        if (!existing.getRecolteId().equals(dto.getRecolteId())) {
            throw new BusinessException(
                    "La récolte associée à un contrôle ne peut pas être modifiée."
            );
        }

        // Le responsable initial est conservé pour la traçabilité
        if (!existing.getIdResponsable().equals(dto.getIdResponsable())) {
            throw new BusinessException(
                    "Le responsable initial du contrôle ne peut pas être modifié."
            );
        }

        existing.setDateControle(dto.getDateControle());
        existing.setTypeControle(dto.getTypeControle());
        existing.setDecision(dto.getDecision());
        existing.setObservations(dto.getObservations());

        ControleQualite saved = repo.save(existing);

        return mapper.toDTO(saved);
    }

    // Consulter un contrôle par ID
    @Override
    public ControleQualiteDTO retrieve(Long id) {

        ControleQualite controle = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contrôle qualité introuvable avec ID : " + id
                ));

        return mapper.toDTO(controle);
    }

    // Consulter tous les contrôles
    @Override
    public List<ControleQualiteDTO> retrieveAll() {

        return repo.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // Annuler un contrôle
    @Override
    public void cancel(Long id) {

        ControleQualite controle = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contrôle qualité introuvable avec ID : " + id
                ));

        // Une annulation répétée ne modifie pas les données
        if (controle.getStatut() == StatutControle.ANNULE) {
            return;
        }

        // Un contrôle terminé ne peut plus être annulé
        if (controle.getStatut() == StatutControle.TERMINE) {
            throw new BusinessException(
                    "Impossible d'annuler un contrôle déjà terminé."
            );
        }

        controle.setStatut(StatutControle.ANNULE);

        repo.save(controle);
    }

    // Changer le statut d'un contrôle
    @Override
    public ControleQualiteDTO changerStatut(
            Long id,
            StatutControle statut) {

        ControleQualite controle = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contrôle qualité introuvable avec ID : " + id
                ));

        if (statut == null) {
            throw new BusinessException("Le statut est obligatoire.");
        }

        // Éviter une modification inutile si le statut est déjà identique
        if (controle.getStatut() == statut) {
            return mapper.toDTO(controle);
        }

        // Seul EN_COURS peut évoluer
        if (controle.getStatut() != StatutControle.EN_COURS) {
            throw new BusinessException(
                    "Impossible de modifier le statut d'un contrôle terminé ou annulé."
            );
        }

        // L'annulation possède son propre endpoint
        if (statut == StatutControle.ANNULE) {
            throw new BusinessException(
                    "Utilisez l'endpoint d'annulation pour annuler un contrôle."
            );
        }

        // La seule transition autorisée est EN_COURS vers TERMINE
        if (statut != StatutControle.TERMINE) {
            throw new BusinessException(
                    "La seule transition autorisée est EN_COURS vers TERMINE."
            );
        }

        // Une décision est obligatoire avant de terminer
        if (controle.getDecision() == null) {
            throw new BusinessException(
                    "Une décision qualité est obligatoire pour terminer le contrôle."
            );
        }

        controle.setStatut(StatutControle.TERMINE);

        return mapper.toDTO(repo.save(controle));
    }

    // Consulter l'historique d'une récolte
    @Override
    public List<ControleQualiteDTO> historique(UUID recolteId) {

        if (recolteId == null) {
            throw new BusinessException(
                    "L'identifiant de la récolte est obligatoire."
            );
        }

        return repo.findByRecolteId(recolteId)
                .stream()
                .map(mapper::toDTO)
                .sorted(Comparator.comparing(
                        ControleQualiteDTO::getDateControle,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }
}
