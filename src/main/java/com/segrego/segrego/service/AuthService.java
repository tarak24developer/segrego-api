package com.segrego.segrego.service;

import com.segrego.segrego.dto.AuthRequest;
import com.segrego.segrego.dto.AuthResponse;
import com.segrego.segrego.dto.RegisterRequest;
import com.segrego.segrego.model.Role;
import com.segrego.segrego.model.User;
import com.segrego.segrego.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (request.getRole() != Role.HOUSEHOLD && request.getRole() != Role.VENDOR) {
            throw new IllegalArgumentException("Registration role is not allowed");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser, jwtService.generateToken(savedUser));
    }

    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase(), request.getPassword())
            );
        } catch (BadCredentialsException exception) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        return mapToResponse(user, jwtService.generateToken(user));
    }

    private AuthResponse mapToResponse(User user, String token) {
        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .token(token)
                .build();
    }
}
