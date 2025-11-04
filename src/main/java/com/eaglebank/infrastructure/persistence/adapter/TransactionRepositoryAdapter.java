package com.eaglebank.infrastructure.persistence.adapter;

import com.eaglebank.domain.model.account.AccountNumber;
import com.eaglebank.domain.model.transaction.Transaction;
import com.eaglebank.domain.model.transaction.TransactionId;
import com.eaglebank.domain.repository.TransactionRepository;
import com.eaglebank.infrastructure.persistence.mapper.TransactionPersistenceMapper;
import com.eaglebank.infrastructure.persistence.repository.TransactionJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation of TransactionRepository
 * Translates between domain model and persistence layer
 */
@Component
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionPersistenceMapper mapper;

    public TransactionRepositoryAdapter(TransactionJpaRepository jpaRepository,
                                        TransactionPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        var entity = mapper.toEntity(transaction);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Transaction> findById(TransactionId transactionId) {
        return jpaRepository.findById(transactionId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<Transaction> findByAccountNumber(AccountNumber accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber.getValue())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
