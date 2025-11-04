package com.eaglebank.interfaces.rest.controller;

import com.eaglebank.application.account.AccountService;
import com.eaglebank.interfaces.rest.dto.request.CreateBankAccountRequest;
import com.eaglebank.interfaces.rest.dto.request.UpdateBankAccountRequest;
import com.eaglebank.interfaces.rest.dto.response.BankAccountResponse;
import com.eaglebank.interfaces.rest.dto.response.ListBankAccountsResponse;
import com.eaglebank.interfaces.rest.mapper.AccountRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;


@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountRestMapper mapper;

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(
            @Valid @RequestBody CreateBankAccountRequest request,
            Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        var command = new AccountService.CreateAccountCommand(
                userId,
                request.name(),
                request.accountType()
        );

        var account = accountService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(account));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> getAccount(@PathVariable String accountNumber,
                                                          Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        var account = accountService.get(accountNumber, userId);
        return ResponseEntity.ok(mapper.toResponse(account));
    }

    @GetMapping
    public ResponseEntity<ListBankAccountsResponse> listAccounts(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        var accounts = accountService.list(userId);

        var response = new ListBankAccountsResponse(
                accounts.stream()
                        .map(mapper::toResponse)
                        .collect(Collectors.toList())
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> updateAccount(
            @PathVariable String accountNumber,
            @Valid @RequestBody UpdateBankAccountRequest request,
            Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        var command = new AccountService.UpdateAccountCommand(
                accountNumber,
                userId,
                request.name()
        );

        var account = accountService.update(command);
        return ResponseEntity.ok(mapper.toResponse(account));
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber,
                                              Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        accountService.delete(accountNumber, userId);
        return ResponseEntity.noContent().build();
    }
}
