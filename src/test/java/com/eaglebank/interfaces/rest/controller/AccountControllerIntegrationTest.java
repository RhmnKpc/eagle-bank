package com.eaglebank.interfaces.rest.controller;

import com.eaglebank.config.IntegrationTest;
import com.eaglebank.domain.model.account.*;
import com.eaglebank.domain.model.user.*;
import com.eaglebank.domain.repository.AccountRepository;
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
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

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

        // Create test user
        User user = User.create(
            userId,
            "Test User",
            Email.of("test@example.com"),
            PhoneNumber.of("+442012345678"),
            Address.of("123 Test St", null, null, "London", "Greater London", "SW1A 1AA"),
            "$2a$10$hashedPassword"
        );
        userRepository.save(user);
    }

    @Test
    void shouldCreateAccount() throws Exception {
        // given
        String requestBody = """
            {
                "name": "My Savings Account",
                "accountType": "personal"
            }
            """;

        // when & then
        mockMvc.perform(post("/v1/accounts")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accountNumber").exists())
            .andExpect(jsonPath("$.accountNumber").value(org.hamcrest.Matchers.matchesPattern("^01\\d{6}$")))
            .andExpect(jsonPath("$.sortCode").value("10-10-10"))
            .andExpect(jsonPath("$.name").value("My Savings Account"))
            .andExpect(jsonPath("$.accountType").value("personal"))
            .andExpect(jsonPath("$.balance").value(0.0))
            .andExpect(jsonPath("$.currency").value("GBP"));
    }

    @Test
    void shouldListAccounts() throws Exception {
        // given
        Account account1 = Account.create(
            AccountNumber.of("01111111"),
            SortCode.defaultSortCode(),
            userId,
            "Account 1",
            AccountType.PERSONAL
        );
        Account account2 = Account.create(
            AccountNumber.of("01222222"),
            SortCode.defaultSortCode(),
            userId,
            "Account 2",
            AccountType.PERSONAL
        );
        accountRepository.save(account1);
        accountRepository.save(account2);

        // when & then
        mockMvc.perform(get("/v1/accounts")
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accounts").isArray())
            .andExpect(jsonPath("$.accounts.length()").value(2))
            .andExpect(jsonPath("$.accounts[0].name").exists())
            .andExpect(jsonPath("$.accounts[1].name").exists());
    }

    @Test
    void shouldGetAccountByNumber() throws Exception {
        // given
        Account account = Account.create(
            AccountNumber.of("01123456"),
            SortCode.defaultSortCode(),
            userId,
            "Test Account",
            AccountType.PERSONAL
        );
        accountRepository.save(account);

        // when & then
        mockMvc.perform(get("/v1/accounts/01123456")
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountNumber").value("01123456"))
            .andExpect(jsonPath("$.name").value("Test Account"));
    }

    @Test
    void shouldUpdateAccountName() throws Exception {
        // given
        Account account = Account.create(
            AccountNumber.of("01123456"),
            SortCode.defaultSortCode(),
            userId,
            "Old Name",
            AccountType.PERSONAL
        );
        accountRepository.save(account);

        String requestBody = """
            {
                "name": "New Name"
            }
            """;

        // when & then
        mockMvc.perform(patch("/v1/accounts/01123456")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void shouldDeleteAccount() throws Exception {
        // given
        Account account = Account.create(
            AccountNumber.of("01123456"),
            SortCode.defaultSortCode(),
            userId,
            "Test Account",
            AccountType.PERSONAL
        );
        accountRepository.save(account);

        // when & then
        mockMvc.perform(delete("/v1/accounts/01123456")
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenAccountNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/v1/accounts/01999999")
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn403WhenAccessingOtherUsersAccount() throws Exception {
        // given
        UserId differentUserId = UserId.of("usr-different");
        Account account = Account.create(
            AccountNumber.of("01123456"),
            SortCode.defaultSortCode(),
            differentUserId,
            "Other User Account",
            AccountType.PERSONAL
        );
        accountRepository.save(account);

        // when & then
        mockMvc.perform(get("/v1/accounts/01123456")
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isForbidden());
    }
}
