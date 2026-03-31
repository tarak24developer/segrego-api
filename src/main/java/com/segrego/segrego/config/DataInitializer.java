package com.segrego.segrego.config;

import com.segrego.segrego.model.PickupRequest;
import com.segrego.segrego.model.PickupStatus;
import com.segrego.segrego.model.Role;
import com.segrego.segrego.model.User;
import com.segrego.segrego.repository.PickupRequestRepository;
import com.segrego.segrego.repository.UserRepository;
import java.util.List;
import java.util.Optional;
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
        if (!seedDataEnabled) {
            return;
        }

        User household = ensureUser("Anika Sharma", "household@segrego.com", "password123", Role.HOUSEHOLD);
        User vendor = ensureUser("Green Route Vendor", "vendor@segrego.com", "password123", Role.VENDOR);
        ensureUser("SegreGo Admin", "admin@segrego.com", "password123", Role.ADMIN);

        if (pickupRequestRepository.count() == 0) {
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

    private User ensureUser(String name, String email, String rawPassword, Role role) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        return userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build());
    }
}
