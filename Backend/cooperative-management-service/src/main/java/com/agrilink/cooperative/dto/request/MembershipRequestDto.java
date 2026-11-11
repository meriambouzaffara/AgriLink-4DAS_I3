package com.agrilink.cooperative.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MembershipRequestDto {

    @NotNull(message = "L'identifiant de l'agriculteur est obligatoire")
    private Long farmerUserId;

    private String message;
}
