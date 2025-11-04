package com.eaglebank.infrastructure.persistence.adapter;

import com.eaglebank.domain.model.account.Account;
import com.eaglebank.domain.model.account.AccountNumber;
import com.eaglebank.domain.model.user.UserId;
import com.eaglebank.domain.repository.AccountRepository;
import com.eaglebank.infrastructure.persistence.entity.AccountEntity;
import com.eaglebank.infrastructure.persistence.mapper.AccountPersistenceMapper;
import com.eaglebank.infrastructure.persistence.repository.AccountJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation of AccountRepository
 * Translates between domain model and persistence layer
 */
@Component
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJpaRepository jpaRepository;
    private final AccountPersistenceMapper mapper;

    public AccountRepositoryAdapter(AccountJpaRepository jpaRepository,
                                    AccountPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Account save(Account account) {
        Optional<AccountEntity> existingEntity = jpaRepository
                .findByAccountNumber(account.getAccountNumber().getValue());

        AccountEntity entityToSave;
        if (existingEntity.isPresent()) {
            entityToSave = existingEntity.get();
            mapper.updateEntity(account, entityToSave);
        } else {
            entityToSave = mapper.toEntity(account);
        }

        AccountEntity savedEntity = jpaRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findByAccountNumber(AccountNumber accountNumber) {
        return jpaRepository.findById(accountNumber.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<Account> findByOwnerId(UserId ownerId) {
        return jpaRepository.findByOwnerId(ownerId.getValue())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByAccountNumber(AccountNumber accountNumber) {
        return jpaRepository.existsById(accountNumber.getValue());
    }

    @Override
    public long countByOwnerId(UserId ownerId) {
        return jpaRepository.countByOwnerId(ownerId.getValue());
    }

    @Override
    public void deleteByAccountNumber(AccountNumber accountNumber) {
        jpaRepository.deleteById(accountNumber.getValue());
    }
}
