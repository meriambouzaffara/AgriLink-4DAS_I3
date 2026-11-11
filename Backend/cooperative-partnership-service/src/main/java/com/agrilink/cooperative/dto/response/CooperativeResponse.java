package com.agrilink.cooperative.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CooperativeResponse {
    private Long id;
    private String name;
    private String description;
    private Long adminUserId;
    private String location;
    private String contactEmail;
    private String contactPhone;
    private int memberCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
