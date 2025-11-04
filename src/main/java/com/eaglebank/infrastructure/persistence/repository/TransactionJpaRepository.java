package com.eaglebank.infrastructure.persistence.repository;

import com.eaglebank.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for TransactionEntity
 */
@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, String> {

    List<TransactionEntity> findByAccountNumber(String accountNumber);
}
