package com.agrilink.agriculturalmanagementservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record AgriculteurDTO(

        @JsonProperty("agriculteurId")
        UUID identifiantAgriculteur,

        @JsonProperty("exists")
        boolean existe,

        String role,

        @JsonProperty("status")
        String statut

) {}