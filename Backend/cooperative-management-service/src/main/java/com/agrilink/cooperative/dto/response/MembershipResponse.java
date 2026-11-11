package com.agrilink.cooperative.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MembershipResponse {
    private Long id;
    private Long cooperativeId;
    private String cooperativeName;
    private Long farmerUserId;
    private LocalDateTime joinedAt;
}
