package com.segrego.segrego.dto;

import com.segrego.segrego.model.PickupStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PickupRequestResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String location;
    private Double latitude;
    private Double longitude;
    private PickupStatus status;
    private Long assignedVendorId;
    private String assignedVendorName;
    private String assignedVendorEmail;
    private LocalDateTime createdAt;
}
