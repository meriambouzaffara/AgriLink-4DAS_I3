package tn.agrilink.backend.qualitycertificationservice.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.agrilink.backend.qualitycertificationservice.enums.DecisionQualite;
import tn.agrilink.backend.qualitycertificationservice.enums.StatutControle;
import tn.agrilink.backend.qualitycertificationservice.enums.TypeControle;


import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControleQualiteDTO {

    private Long idControle;
    private LocalDateTime dateControle;
    private TypeControle typeControle;
    private DecisionQualite decision;
    private String observations;
    private StatutControle statut;
    private UUID recolteId;
    private Long idResponsable;
}
