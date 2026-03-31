package com.segrego.segrego.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class VendorPerformanceResponse {
    private Long vendorId;
    private String vendorName;
    private String vendorEmail;
    private long acceptedRequests;
    private long completedRequests;
}
