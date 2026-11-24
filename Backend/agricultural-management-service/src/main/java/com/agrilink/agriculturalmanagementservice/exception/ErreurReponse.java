package com.agrilink.agriculturalmanagementservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErreurReponse(
        LocalDateTime horodatage,
        int statut,
        String erreur,
        String message,
        String chemin,
        Map<String, String> erreursValidation
) {
    public ErreurReponse(int statut, String erreur, String message, String chemin) {
        this(LocalDateTime.now(), statut, erreur, message, chemin, null);
    }

    public ErreurReponse(int statut, String erreur, String message, String chemin, Map<String, String> erreursValidation) {
        this(LocalDateTime.now(), statut, erreur, message, chemin, erreursValidation);
    }
}
