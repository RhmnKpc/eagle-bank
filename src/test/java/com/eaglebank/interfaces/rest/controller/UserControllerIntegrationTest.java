package com.eaglebank.interfaces.rest.controller;

import com.eaglebank.config.IntegrationTest;
import com.eaglebank.domain.model.user.Email;
import com.eaglebank.domain.model.user.User;
import com.eaglebank.domain.model.user.UserId;
import com.eaglebank.domain.repository.UserRepository;
import com.eaglebank.infrastructure.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String authToken;
    private UserId userId;

    @BeforeEach
    void setUp() {
        userId = UserId.of("usr-test-123");
        authToken = jwtTokenProvider.createToken(userId.getValue(), "test@example.com");
    }

    @Test
    void shouldCreateUser() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "John Doe",
                    "email": "john.doe@example.com",
                    "phoneNumber": "+442012345678",
                    "address": {
                        "line1": "123 Main St",
                        "town": "London",
                        "county": "Greater London",
                        "postcode": "SW1A 1AA"
                    },
                    "password": "Password123!"
                }
                """;

        // when & then
        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+442012345678"))
                .andExpect(jsonPath("$.address.line1").value("123 Main St"))
                .andExpect(jsonPath("$.createdTimestamp").exists());
    }

    @Test
    void shouldRejectCreateUserWithInvalidEmail() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "John Doe",
                    "email": "invalid-email",
                    "phoneNumber": "+442012345678",
                    "address": {
                        "line1": "123 Main St",
                        "town": "London",
                        "county": "Greater London",
                        "postcode": "SW1A 1AA"
                    },
                    "password": "Password123!"
                }
                """;

        // when & then
        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetUserById() throws Exception {
        // given
        User user = createAndSaveTestUser();

        // when & then
        mockMvc.perform(get("/v1/users/" + user.getId().getValue())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().getValue()))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldReturn403WhenUserNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/v1/users/usr-nonexistent")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        // given
        User user = createAndSaveTestUser();

        String requestBody = """
                {
                    "name": "Updated Name",
                    "phoneNumber": "+443012345678",
                    "email": "test@example.com",
                    "address": {
                        "line1": "456 New St",
                        "town": "Manchester",
                        "county": "Greater Manchester",
                        "postcode": "M1 1AA"
                    }
                }
                """;

        // when & then
        mockMvc.perform(patch("/v1/users/" + user.getId().getValue())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.phoneNumber").value("+443012345678"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        // given
        User user = createAndSaveTestUser();

        // when & then
        mockMvc.perform(delete("/v1/users/" + user.getId().getValue())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn401WhenNoAuthToken() throws Exception {
        // when & then
        mockMvc.perform(get("/v1/users/usr-123"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    private User createAndSaveTestUser() {
        User user = User.create(
                userId,
                "Test User",
                Email.of("test@example.com"),
                com.eaglebank.domain.model.user.PhoneNumber.of("+442012345678"),
                com.eaglebank.domain.model.user.Address.of("123 Test St", null, null, "London", "Greater London", "SW1A 1AA"),
                "$2a$10$hashedPassword"
        );
        return userRepository.save(user);
    }
}
