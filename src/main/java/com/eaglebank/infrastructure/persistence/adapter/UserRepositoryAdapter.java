package com.eaglebank.infrastructure.persistence.adapter;

import com.eaglebank.domain.model.user.Email;
import com.eaglebank.domain.model.user.User;
import com.eaglebank.domain.model.user.UserId;
import com.eaglebank.domain.repository.UserRepository;
import com.eaglebank.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.eaglebank.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter implementation of UserRepository
 * Translates between domain model and persistence layer
 */
@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserPersistenceMapper mapper;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository,
                                 UserPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return jpaRepository.findById(userId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(UserId userId) {
        return jpaRepository.existsById(userId.getValue());
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }

    @Override
    public void deleteById(UserId userId) {
        jpaRepository.deleteById(userId.getValue());
    }
}
