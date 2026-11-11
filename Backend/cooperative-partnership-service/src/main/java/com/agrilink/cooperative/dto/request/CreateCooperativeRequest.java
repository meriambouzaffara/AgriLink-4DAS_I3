package com.agrilink.cooperative.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCooperativeRequest {

    @NotBlank(message = "Le nom de la coopérative est obligatoire")
    private String name;

    private String description;

    @NotNull(message = "L'identifiant de l'administrateur est obligatoire")
    private Long adminUserId;

    private String location;

    @Email(message = "Format d'email invalide")
    private String contactEmail;

    private String contactPhone;
}
