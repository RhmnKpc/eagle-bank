package com.eaglebank.interfaces.rest.controller;

import com.eaglebank.config.IntegrationTest;
import com.eaglebank.domain.model.account.*;
import com.eaglebank.domain.model.transaction.Transaction;
import com.eaglebank.domain.model.transaction.TransactionId;
import com.eaglebank.domain.model.transaction.TransactionReference;
import com.eaglebank.domain.model.transaction.TransactionType;
import com.eaglebank.domain.model.user.*;
import com.eaglebank.domain.repository.AccountRepository;
import com.eaglebank.domain.repository.TransactionRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String authToken;
    private UserId userId;
    private Account testAccount;

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

        // Create test account
        testAccount = Account.create(
                AccountNumber.of("01123456"),
                SortCode.defaultSortCode(),
                userId,
                "Test Account",
                AccountType.PERSONAL
        );
        testAccount.deposit(Money.gbp(1000.00));
        accountRepository.save(testAccount);
    }

    @Test
    void shouldCreateDepositTransaction() throws Exception {
        // given
        String requestBody = """
                {
                    "amount": 100.00,
                    "currency": "GBP",
                    "type": "deposit",
                    "reference": "Test deposit"
                }
                """;

        // when & then
        mockMvc.perform(post("/v1/accounts/01123456/transactions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.currency").value("GBP"))
                .andExpect(jsonPath("$.type").value("deposit"))
                .andExpect(jsonPath("$.reference").value("Test deposit"))
                .andExpect(jsonPath("$.userId").value(userId.getValue()))
                .andExpect(jsonPath("$.createdTimestamp").exists());
    }

    @Test
    void shouldCreateWithdrawalTransaction() throws Exception {
        // given
        String requestBody = """
                {
                    "amount": 50.00,
                    "currency": "GBP",
                    "type": "withdrawal",
                    "reference": "Test withdrawal"
                }
                """;

        // when & then
        mockMvc.perform(post("/v1/accounts/01123456/transactions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(50.0))
                .andExpect(jsonPath("$.type").value("withdrawal"));
    }

    @Test
    void shouldRejectWithdrawalWithInsufficientFunds() throws Exception {
        // given
        String requestBody = """
                {
                    "amount": 2000.00,
                    "currency": "GBP",
                    "type": "withdrawal",
                    "reference": "Too much"
                }
                """;

        // when & then
        mockMvc.perform(post("/v1/accounts/01123456/transactions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldListTransactions() throws Exception {
        // given
        Transaction tx1 = Transaction.create(
                TransactionId.generate(),
                testAccount.getAccountNumber(),
                TransactionType.DEPOSIT,
                Money.gbp(100.00),
                testAccount.getBalance().add(Money.gbp(100.00)),
                TransactionReference.of("Deposit 1")
        );
        Transaction tx2 = Transaction.create(
                TransactionId.generate(),
                testAccount.getAccountNumber(),
                TransactionType.DEPOSIT,
                Money.gbp(50.00),
                testAccount.getBalance().add(Money.gbp(150.00)),
                TransactionReference.of("Deposit 2")
        );
        transactionRepository.save(tx1);
        transactionRepository.save(tx2);

        // when & then
        mockMvc.perform(get("/v1/accounts/01123456/transactions")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(2));
    }

    @Test
    void shouldGetTransactionById() throws Exception {
        // given
        Transaction tx = Transaction.create(
                TransactionId.of("tan-test-123"),
                testAccount.getAccountNumber(),
                TransactionType.DEPOSIT,
                Money.gbp(100.00),
                testAccount.getBalance().add(Money.gbp(100.00)),
                TransactionReference.of("Test")
        );
        transactionRepository.save(tx);

        // when & then
        mockMvc.perform(get("/v1/accounts/01123456/transactions/tan-test-123")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("tan-test-123"))
                .andExpect(jsonPath("$.reference").value("Test"));
    }

    @Test
    void shouldReturn404WhenTransactionNotFound() throws Exception {
        // when & then
        mockMvc.perform(get("/v1/accounts/01123456/transactions/tan-nonexistent")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }
}
