
package com.agrilink.order.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ligne_commande")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long produitId;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantite;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal prixUnitaire;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal sousTotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commande_id", nullable = false)
    private Order commande;
}
