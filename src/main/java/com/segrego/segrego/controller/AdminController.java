package com.segrego.segrego.controller;

import com.segrego.segrego.dto.AdminAnalyticsResponse;
import com.segrego.segrego.dto.PickupRequestResponse;
import com.segrego.segrego.dto.UserResponse;
import com.segrego.segrego.service.AdminService;
import com.segrego.segrego.service.PickupRequestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final PickupRequestService pickupRequestService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/requests")
    public ResponseEntity<List<PickupRequestResponse>> getRequests() {
        return ResponseEntity.ok(pickupRequestService.getAllRequests());
    }

    @GetMapping("/analytics")
    public ResponseEntity<AdminAnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(adminService.getAnalytics());
    }
}
