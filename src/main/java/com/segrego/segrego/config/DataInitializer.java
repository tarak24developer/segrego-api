package com.segrego.segrego.config;

import com.segrego.segrego.model.PickupRequest;
import com.segrego.segrego.model.PickupStatus;
import com.segrego.segrego.model.Role;
import com.segrego.segrego.model.User;
import com.segrego.segrego.repository.PickupRequestRepository;
import com.segrego.segrego.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PickupRequestRepository pickupRequestRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed-data:false}")
    private boolean seedDataEnabled;

    @Override
    public void run(String... args) {
        if (!seedDataEnabled || userRepository.count() > 0) {
            return;
        }

        User household = userRepository.save(User.builder()
                .name("Anika Sharma")
                .email("household@segrego.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.HOUSEHOLD)
                .build());

        User vendor = userRepository.save(User.builder()
                .name("Green Route Vendor")
                .email("vendor@segrego.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.VENDOR)
                .build());

        userRepository.save(User.builder()
                .name("SegreGo Admin")
                .email("admin@segrego.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.ADMIN)
                .build());

        pickupRequestRepository.saveAll(List.of(
                PickupRequest.builder()
                        .user(household)
                        .location("Koramangala 5th Block")
                        .latitude(12.9352)
                        .longitude(77.6245)
                        .status(PickupStatus.PENDING)
                        .build(),
                PickupRequest.builder()
                        .user(household)
                        .location("HSR Layout Sector 2")
                        .latitude(12.9116)
                        .longitude(77.6474)
                        .status(PickupStatus.ACCEPTED)
                        .assignedVendor(vendor)
                        .build()
        ));
    }
}
