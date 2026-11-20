package tn.agrilink.backend.qualitycertificationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.agrilink.backend.qualitycertificationservice.enums.TypeRapport;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rapports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRapport;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeRapport typeRapport;

    @Column(nullable = false)
    private LocalDateTime dateGeneration;

    @Column(nullable = false, length = 1000)
    private String urlRapport;

    @Column(nullable = false)
    private Long idResponsable;

    // Association avec les contrôles qualité

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rapport_controles",
            joinColumns = @JoinColumn(name = "rapport_id"),
            inverseJoinColumns = @JoinColumn(name = "controle_id")
    )
    private List<ControleQualite> controles = new ArrayList<>();

    // Association avec les certifications

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rapport_certifications",
            joinColumns = @JoinColumn(name = "rapport_id"),
            inverseJoinColumns = @JoinColumn(name = "certification_id")
    )
    private List<Certification> certifications = new ArrayList<>();

    // Méthodes pour maintenir les deux côtés des associations

    public void ajouterControle(ControleQualite controle) {
        if (!this.controles.contains(controle)) {
            this.controles.add(controle);
            controle.getRapports().add(this);
        }
    }

    public void ajouterCertification(Certification certification) {
        if (!this.certifications.contains(certification)) {
            this.certifications.add(certification);
            certification.getRapports().add(this);
        }
    }
}

