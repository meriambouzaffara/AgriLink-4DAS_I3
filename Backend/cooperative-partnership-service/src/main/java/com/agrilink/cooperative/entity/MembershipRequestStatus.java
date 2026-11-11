package com.agrilink.cooperative.entity;

/**
 * Statut d'une demande d'adhésion à une coopérative.
 */
public enum MembershipRequestStatus {
    /** En attente de traitement par l'administrateur */
    PENDING,
    /** Demande acceptée – l'agriculteur devient membre */
    ACCEPTED,
    /** Demande refusée */
    REJECTED
}
