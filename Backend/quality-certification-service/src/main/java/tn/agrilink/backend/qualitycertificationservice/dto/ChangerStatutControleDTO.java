package tn.agrilink.backend.qualitycertificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.agrilink.backend.qualitycertificationservice.enums.StatutControle;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangerStatutControleDTO {

    private StatutControle statut;
}
