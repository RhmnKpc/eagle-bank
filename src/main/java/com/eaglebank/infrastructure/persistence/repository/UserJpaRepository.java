package com.eaglebank.infrastructure.persistence.repository;

import com.eaglebank.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for UserEntity
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
