package com.eaglebank.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for Account
 */
@Data
@Entity
@Table(name = "accounts")
@EntityListeners(AuditingEntityListener.class)
public class AccountEntity {

    @Id
    private String accountNumber;

    @Column(nullable = false)
    private String sortCode;

    @Column(nullable = false)
    private String ownerId;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountTypeEntity accountType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatusEntity status;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, length = 3)
    private String currency;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public AccountEntity() {
    }

    public enum AccountTypeEntity {
        PERSONAL, BUSINESS
    }

    public enum AccountStatusEntity {
        ACTIVE, SUSPENDED, CLOSED, PENDING
    }

}
