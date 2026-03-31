package com.segrego.segrego.controller;

import com.segrego.segrego.dto.CreatePickupRequestDto;
import com.segrego.segrego.dto.PickupRequestResponse;
import com.segrego.segrego.service.PickupRequestService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {

    private final PickupRequestService pickupRequestService;

    @PostMapping
    public ResponseEntity<PickupRequestResponse> createRequest(@Valid @RequestBody CreatePickupRequestDto request,
                                                               Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pickupRequestService.createRequest(principal.getName(), request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PickupRequestResponse>> getMyRequests(Principal principal) {
        return ResponseEntity.ok(pickupRequestService.getMyRequests(principal.getName()));
    }
}
