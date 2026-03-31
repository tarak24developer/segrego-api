package com.segrego.segrego.service;

import com.segrego.segrego.dto.AdminAnalyticsResponse;
import com.segrego.segrego.dto.UserResponse;
import com.segrego.segrego.dto.VendorPerformanceResponse;
import com.segrego.segrego.model.PickupRequest;
import com.segrego.segrego.model.PickupStatus;
import com.segrego.segrego.model.Role;
import com.segrego.segrego.model.User;
import com.segrego.segrego.repository.PickupRequestRepository;
import com.segrego.segrego.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PickupRequestRepository pickupRequestRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminAnalyticsResponse getAnalytics() {
        List<PickupRequest> requests = pickupRequestRepository.findAll();
        List<User> vendors = userRepository.findAllByRole(Role.VENDOR);

        List<VendorPerformanceResponse> vendorPerformance = vendors.stream()
                .map(vendor -> VendorPerformanceResponse.builder()
                        .vendorId(vendor.getId())
                        .vendorName(vendor.getName())
                        .vendorEmail(vendor.getEmail())
                        .acceptedRequests(requests.stream()
                                .filter(request -> request.getAssignedVendor() != null)
                                .filter(request -> request.getAssignedVendor().getId().equals(vendor.getId()))
                                .count())
                        .completedRequests(requests.stream()
                                .filter(request -> request.getAssignedVendor() != null)
                                .filter(request -> request.getAssignedVendor().getId().equals(vendor.getId()))
                                .filter(request -> request.getStatus() == PickupStatus.COMPLETED)
                                .count())
                        .build())
                .toList();

        return AdminAnalyticsResponse.builder()
                .totalUsers(userRepository.count())
                .totalRequests(pickupRequestRepository.count())
                .pendingRequests(pickupRequestRepository.countByStatus(PickupStatus.PENDING))
                .acceptedRequests(pickupRequestRepository.countByStatus(PickupStatus.ACCEPTED))
                .completedRequests(pickupRequestRepository.countByStatus(PickupStatus.COMPLETED))
                .vendorPerformance(vendorPerformance)
                .build();
    }
}
