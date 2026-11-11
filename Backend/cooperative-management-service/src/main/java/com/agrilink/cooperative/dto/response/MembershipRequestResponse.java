package com.agrilink.cooperative.dto.response;

import com.agrilink.cooperative.entity.MembershipRequestStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MembershipRequestResponse {
    private Long id;
    private Long cooperativeId;
    private String cooperativeName;
    private Long farmerUserId;
    private MembershipRequestStatus status;
    private String message;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}
