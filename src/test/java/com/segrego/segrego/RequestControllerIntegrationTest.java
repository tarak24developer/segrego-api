package com.segrego.segrego;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.segrego.segrego.dto.RegisterRequest;
import com.segrego.segrego.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void householdShouldCreateAndReadOwnRequest() throws Exception {
        String token = registerAndExtractToken("requester@segrego.com", Role.HOUSEHOLD);

        mockMvc.perform(post("/request")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "location": "Indiranagar 12th Main",
                                  "latitude": 12.971891,
                                  "longitude": 77.641151
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.location").value("Indiranagar 12th Main"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        mockMvc.perform(get("/request/my")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location").value("Indiranagar 12th Main"));
    }

    @Test
    void vendorShouldNotAccessHouseholdRequestEndpoint() throws Exception {
        String token = registerAndExtractToken("blockedvendor@segrego.com", Role.VENDOR);

        mockMvc.perform(post("/request")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "location": "Test",
                                  "latitude": 12.0,
                                  "longitude": 77.0
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource"));
    }

    private String registerAndExtractToken(String email, Role role) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Integration User");
        request.setEmail(email);
        request.setPassword("password123");
        request.setRole(role);

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }
}
