package com.segrego.segrego.controller;

import com.segrego.segrego.dto.PickupRequestResponse;
import com.segrego.segrego.service.PickupRequestService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vendor")
@RequiredArgsConstructor
public class VendorController {

    private final PickupRequestService pickupRequestService;

    @GetMapping("/requests")
    public ResponseEntity<List<PickupRequestResponse>> getVendorRequests(Principal principal,
                                                                         @RequestParam double latitude,
                                                                         @RequestParam double longitude,
                                                                         @RequestParam(defaultValue = "10") double radiusKm) {
        return ResponseEntity.ok(
                pickupRequestService.getNearbyRequestsForVendors(principal.getName(), latitude, longitude, radiusKm)
        );
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<PickupRequestResponse> acceptRequest(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(pickupRequestService.acceptRequest(id, principal.getName()));
    }

    @PostMapping("/complete/{id}")
    public ResponseEntity<PickupRequestResponse> completeRequest(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(pickupRequestService.completeRequest(id, principal.getName()));
    }
}
