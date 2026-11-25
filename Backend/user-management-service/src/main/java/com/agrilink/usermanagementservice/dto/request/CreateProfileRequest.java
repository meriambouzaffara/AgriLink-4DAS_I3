
package com.agrilink.usermanagementservice.dto.request;

import com.agrilink.usermanagementservice.enumeration.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data used to create a user profile")
public record CreateProfileRequest(

        @Schema(description = "User first name", example = "Yosr")
        @NotBlank(message = "First name is required")
        @Size(max = 100)
        String firstName,

        @Schema(description = "User last name", example = "Ben Ammar")
        @NotBlank(message = "Last name is required")
        @Size(max = 100)
        String lastName,

        @Schema(description = "User phone number", example = "22123456")
        @Size(max = 20)
        String phone,

        @Schema(description = "User address", example = "Beja")
        @Size(max = 255)
        String address,

        @Schema(description = "Business profile type", example = "AGRICULTEUR")
        @NotNull(message = "User role is required")
        UserRole userRole

) {
}
