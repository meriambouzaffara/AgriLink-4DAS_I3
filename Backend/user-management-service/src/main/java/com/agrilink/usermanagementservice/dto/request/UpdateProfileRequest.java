
package com.agrilink.usermanagementservice.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName,

        @Size(max = 20)
        String phone,

        @Size(max = 255)
        String address

) {
}
