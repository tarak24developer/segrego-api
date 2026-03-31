package com.segrego.segrego.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AdminAnalyticsResponse {
    private long totalUsers;
    private long totalRequests;
    private long pendingRequests;
    private long acceptedRequests;
    private long completedRequests;
    private List<VendorPerformanceResponse> vendorPerformance;
}
