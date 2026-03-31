package com.segrego.segrego.service;

import com.segrego.segrego.dto.CreatePickupRequestDto;
import com.segrego.segrego.dto.PickupRequestResponse;
import com.segrego.segrego.model.PickupRequest;
import com.segrego.segrego.model.PickupStatus;
import com.segrego.segrego.model.Role;
import com.segrego.segrego.model.User;
import com.segrego.segrego.repository.PickupRequestRepository;
import com.segrego.segrego.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PickupRequestService {

    private final PickupRequestRepository pickupRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public PickupRequestResponse createRequest(String userEmail, CreatePickupRequestDto requestDto) {
        User household = getUserByEmail(userEmail);
        ensureRole(household, Role.HOUSEHOLD);

        PickupRequest pickupRequest = PickupRequest.builder()
                .user(household)
                .location(requestDto.getLocation())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .status(PickupStatus.PENDING)
                .build();

        return mapToResponse(pickupRequestRepository.save(pickupRequest));
    }

    @Transactional(readOnly = true)
    public List<PickupRequestResponse> getMyRequests(String userEmail) {
        User household = getUserByEmail(userEmail);
        ensureRole(household, Role.HOUSEHOLD);

        return pickupRequestRepository.findByUserOrderByCreatedAtDesc(household)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PickupRequestResponse> getNearbyRequestsForVendors(String vendorEmail,
                                                                   double vendorLatitude,
                                                                   double vendorLongitude,
                                                                   double radiusKm) {
        User vendor = getUserByEmail(vendorEmail);
        ensureRole(vendor, Role.VENDOR);

        List<PickupRequest> requests = new ArrayList<>(pickupRequestRepository.findByStatusOrderByCreatedAtAsc(PickupStatus.PENDING));
        requests.addAll(pickupRequestRepository.findByAssignedVendorOrderByCreatedAtDesc(vendor));

        return requests.stream()
                .filter(request -> request.getAssignedVendor() != null
                        || calculateDistanceKm(vendorLatitude, vendorLongitude, request.getLatitude(), request.getLongitude()) <= radiusKm)
                .distinct()
                .sorted(Comparator.comparing(PickupRequest::getCreatedAt).reversed())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public PickupRequestResponse acceptRequest(Long requestId, String vendorEmail) {
        User vendor = getUserByEmail(vendorEmail);
        ensureRole(vendor, Role.VENDOR);

        PickupRequest pickupRequest = pickupRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Pickup request not found"));

        if (pickupRequest.getStatus() != PickupStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be accepted");
        }

        pickupRequest.setStatus(PickupStatus.ACCEPTED);
        pickupRequest.setAssignedVendor(vendor);
        return mapToResponse(pickupRequestRepository.save(pickupRequest));
    }

    @Transactional
    public PickupRequestResponse completeRequest(Long requestId, String vendorEmail) {
        User vendor = getUserByEmail(vendorEmail);
        ensureRole(vendor, Role.VENDOR);

        PickupRequest pickupRequest = pickupRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Pickup request not found"));

        if (pickupRequest.getStatus() != PickupStatus.ACCEPTED) {
            throw new IllegalStateException("Only accepted requests can be completed");
        }

        if (pickupRequest.getAssignedVendor() == null
                || !pickupRequest.getAssignedVendor().getId().equals(vendor.getId())) {
            throw new IllegalStateException("Request is assigned to another vendor");
        }

        pickupRequest.setStatus(PickupStatus.COMPLETED);
        return mapToResponse(pickupRequestRepository.save(pickupRequest));
    }

    @Transactional(readOnly = true)
    public List<PickupRequestResponse> getAllRequests() {
        return pickupRequestRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void ensureRole(User user, Role role) {
        if (user.getRole() != role) {
            throw new IllegalStateException("User does not have required role");
        }
    }

    private PickupRequestResponse mapToResponse(PickupRequest pickupRequest) {
        User assignedVendor = pickupRequest.getAssignedVendor();
        return PickupRequestResponse.builder()
                .id(pickupRequest.getId())
                .userId(pickupRequest.getUser().getId())
                .userName(pickupRequest.getUser().getName())
                .userEmail(pickupRequest.getUser().getEmail())
                .location(pickupRequest.getLocation())
                .latitude(pickupRequest.getLatitude())
                .longitude(pickupRequest.getLongitude())
                .status(pickupRequest.getStatus())
                .assignedVendorId(assignedVendor != null ? assignedVendor.getId() : null)
                .assignedVendorName(assignedVendor != null ? assignedVendor.getName() : null)
                .assignedVendorEmail(assignedVendor != null ? assignedVendor.getEmail() : null)
                .createdAt(pickupRequest.getCreatedAt())
                .build();
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double originLat = Math.toRadians(lat1);
        double destinationLat = Math.toRadians(lat2);

        double haversine = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(originLat) * Math.cos(destinationLat)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double arc = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
        return earthRadiusKm * arc;
    }
}
