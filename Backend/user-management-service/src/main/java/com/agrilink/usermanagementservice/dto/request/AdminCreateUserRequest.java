
package com.agrilink.usermanagementservice.dto.request;

import com.agrilink.usermanagementservice.enumeration.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminCreateUserRequest(

        @NotBlank
        @Size(max = 100)
        String username,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotBlank
        @Size(max = 100)
        String lastName,

        @Size(max = 20)
        String phone,

        @Size(max = 255)
        String address,

        @NotNull
        UserRole userRole
) {
    @Override
    public String toString() {
        return "AdminCreateUserRequest[username=" + username
                + ", userRole=" + userRole + "]";
    }
}
