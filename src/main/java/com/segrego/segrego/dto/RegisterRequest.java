package com.segrego.segrego.dto;

import com.segrego.segrego.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotNull
    private Role role;

    @AssertTrue(message = "Only HOUSEHOLD and VENDOR registrations are allowed")
    public boolean isAllowedRole() {
        return role == Role.HOUSEHOLD || role == Role.VENDOR;
    }
}
