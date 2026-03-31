package com.segrego.segrego.dto;

import com.segrego.segrego.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String token;
}
