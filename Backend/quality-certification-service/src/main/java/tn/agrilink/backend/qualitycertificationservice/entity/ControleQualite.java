
package tn.agrilink.backend.qualitycertificationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.agrilink.backend.qualitycertificationservice.enums.DecisionQualite;
import tn.agrilink.backend.qualitycertificationservice.enums.StatutControle;
import tn.agrilink.backend.qualitycertificationservice.enums.TypeControle;





import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "controles_qualite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControleQualite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idControle;

    @Column(nullable = false)
    private LocalDateTime dateControle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeControle typeControle;

    @Enumerated(EnumType.STRING)
    private DecisionQualite decision;

    @Column(length = 2000)
    private String observations;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutControle statut = StatutControle.EN_COURS;

    @Column(nullable = false)
    private UUID recolteId;

    @Column(nullable = false)
    private Long idResponsable;

    @ManyToMany(mappedBy = "controles", fetch = FetchType.LAZY)
    private List<Rapport> rapports = new ArrayList<>();
}

