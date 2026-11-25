package tn.agrilink.backend.qualitycertificationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.agrilink.backend.qualitycertificationservice.enums.StatutCertification;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCertification;

    @Column(nullable = false, length = 150)
    private String nomCertification;

    @Column(nullable = false, unique = true, length = 100)
    private String numeroCertification;

    @Column(nullable = false)
    private LocalDate dateDelivrance;

    @Column(nullable = false)
    private LocalDate dateExpiration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCertification statut = StatutCertification.VALIDEE;

    private String documentCertificat;

    @Column(nullable = false)
    private UUID recolteId;

    @Column(nullable = false)
    private Long idResponsable;

    @ManyToMany(mappedBy = "certifications", fetch = FetchType.LAZY)
    private List<Rapport> rapports = new ArrayList<>();
}

