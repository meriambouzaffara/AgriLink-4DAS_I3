package tn.agrilink.backend.qualitycertificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.agrilink.backend.qualitycertificationservice.enums.TypeRapport;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RapportDTO {

    private Long idRapport;
    private TypeRapport typeRapport;
    private LocalDateTime dateGeneration;
    private String urlRapport;
    private Long idResponsable;
}
