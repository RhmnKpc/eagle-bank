package com.eaglebank.infrastructure.persistence.repository;

import com.eaglebank.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for AccountEntity
 */
@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, String> {

    List<AccountEntity> findByOwnerId(String ownerId);

    long countByOwnerId(String ownerId);

    Optional<AccountEntity> findByAccountNumber(String accountNumber);
}
