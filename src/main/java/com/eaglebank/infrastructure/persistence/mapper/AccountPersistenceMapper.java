package com.eaglebank.infrastructure.persistence.mapper;

import com.eaglebank.domain.model.account.*;
import com.eaglebank.domain.model.user.UserId;
import com.eaglebank.infrastructure.persistence.entity.AccountEntity;
import org.springframework.stereotype.Component;

import java.util.Currency;

/**
 * Mapper between Account domain model and AccountEntity
 */
@Component
public class AccountPersistenceMapper {

    public AccountEntity toEntity(Account account) {
        AccountEntity entity = new AccountEntity();
        entity.setAccountNumber(account.getAccountNumber().getValue());
        entity.setSortCode(account.getSortCode().getValue());
        entity.setOwnerId(account.getOwnerId().getValue());
        entity.setAccountName(account.getName());
        entity.setAccountType(toEntityAccountType(account.getType()));
        entity.setStatus(toEntityAccountStatus(account.getStatus()));
        entity.setBalance(account.getBalance().getAmount());
        entity.setCurrency(account.getBalance().getCurrency().getCurrencyCode());
        entity.setCreatedAt(account.getCreatedAt());
        entity.setUpdatedAt(account.getUpdatedAt());
        return entity;
    }

    public void updateEntity(Account account, AccountEntity entity) {
        entity.setAccountName(account.getName());
        entity.setStatus(toEntityAccountStatus(account.getStatus()));
        entity.setBalance(account.getBalance().getAmount());
        entity.setCurrency(account.getBalance().getCurrency().getCurrencyCode());
        entity.setUpdatedAt(account.getUpdatedAt());
    }


    public Account toDomain(AccountEntity entity) {
        return Account.reconstitute(
                AccountNumber.of(entity.getAccountNumber()),
                SortCode.of(entity.getSortCode()),
                UserId.of(entity.getOwnerId()),
                entity.getAccountName(),
                toDomainAccountType(entity.getAccountType()),
                toDomainAccountStatus(entity.getStatus()),
                Money.of(entity.getBalance(), Currency.getInstance(entity.getCurrency())),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private AccountEntity.AccountTypeEntity toEntityAccountType(AccountType type) {
        return AccountEntity.AccountTypeEntity.valueOf(type.name());
    }

    private AccountType toDomainAccountType(AccountEntity.AccountTypeEntity type) {
        return AccountType.valueOf(type.name());
    }

    private AccountEntity.AccountStatusEntity toEntityAccountStatus(AccountStatus status) {
        return AccountEntity.AccountStatusEntity.valueOf(status.name());
    }

    private AccountStatus toDomainAccountStatus(AccountEntity.AccountStatusEntity status) {
        return AccountStatus.valueOf(status.name());
    }
}
