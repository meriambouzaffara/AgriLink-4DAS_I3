package tn.agrilink.backend.qualitycertificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.agrilink.backend.qualitycertificationservice.enums.StatutCertification;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDTO {

    private Long idCertification;
    private String nomCertification;
    private String numeroCertification;
    private LocalDate dateDelivrance;
    private LocalDate dateExpiration;
    private StatutCertification statut;
    private String documentCertificat;
    private UUID recolteId;
    private Long idResponsable;
}
