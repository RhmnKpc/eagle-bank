package com.eaglebank.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for Transaction
 */
@Setter
@Getter
@Entity
@Table(name = "transactions")
public class TransactionEntity {

    // Getters and Setters
    @Id
    private String id;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionTypeEntity type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private String reference;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public TransactionEntity() {
    }

    public enum TransactionTypeEntity {
        DEPOSIT, WITHDRAWAL
    }

}
