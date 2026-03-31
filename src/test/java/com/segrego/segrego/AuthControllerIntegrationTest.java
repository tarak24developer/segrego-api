package com.segrego.segrego;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.segrego.segrego.dto.AuthRequest;
import com.segrego.segrego.dto.RegisterRequest;
import com.segrego.segrego.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerShouldCreateHouseholdUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test Household");
        request.setEmail("household.test@segrego.com");
        request.setPassword("password123");
        request.setRole(Role.HOUSEHOLD);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("household.test@segrego.com"))
                .andExpect(jsonPath("$.role").value("HOUSEHOLD"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void registerShouldRejectAdminRole() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Unauthorized Admin");
        request.setEmail("admin.test@segrego.com");
        request.setPassword("password123");
        request.setRole(Role.ADMIN);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void loginShouldReturnJwtToken() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Vendor User");
        registerRequest.setEmail("vendor.test@segrego.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.VENDOR);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("vendor.test@segrego.com");
        authRequest.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("VENDOR"));
    }
}
