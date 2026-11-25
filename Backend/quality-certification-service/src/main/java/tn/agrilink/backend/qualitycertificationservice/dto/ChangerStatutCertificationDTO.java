package tn.agrilink.backend.qualitycertificationservice.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.agrilink.backend.qualitycertificationservice.enums.StatutCertification;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangerStatutCertificationDTO {

    private StatutCertification statut;
}
