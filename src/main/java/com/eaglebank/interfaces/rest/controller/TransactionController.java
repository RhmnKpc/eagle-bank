package com.eaglebank.interfaces.rest.controller;

import com.eaglebank.application.transaction.TransactionService;
import com.eaglebank.interfaces.rest.dto.request.CreateTransactionRequest;
import com.eaglebank.interfaces.rest.dto.response.ListTransactionsResponse;
import com.eaglebank.interfaces.rest.dto.response.TransactionResponse;
import com.eaglebank.interfaces.rest.mapper.TransactionRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;


@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRestMapper mapper;


    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @PathVariable String accountNumber,
            @Valid @RequestBody CreateTransactionRequest request,
            Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        var command = new TransactionService.CreateTransactionCommand(
                accountNumber,
                userId,
                request.type(),
                request.amount(),
                request.reference()
        );

        var transaction = transactionService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(transaction));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable String accountNumber,
            @PathVariable String transactionId,
            Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        var transaction = transactionService.get(accountNumber, transactionId, userId);
        return ResponseEntity.ok(mapper.toResponse(transaction));
    }

    @GetMapping
    public ResponseEntity<ListTransactionsResponse> listTransactions(
            @PathVariable String accountNumber,
            Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        var transactions = transactionService.list(accountNumber, userId);

        var response = new ListTransactionsResponse(
                transactions.stream()
                        .map(mapper::toResponse)
                        .collect(Collectors.toList())
        );

        return ResponseEntity.ok(response);
    }
}
