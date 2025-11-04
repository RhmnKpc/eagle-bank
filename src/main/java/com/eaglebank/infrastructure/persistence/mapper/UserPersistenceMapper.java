package com.eaglebank.infrastructure.persistence.mapper;

import com.eaglebank.domain.model.user.*;
import com.eaglebank.infrastructure.persistence.entity.AddressEmbeddable;
import com.eaglebank.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between User domain model and UserEntity
 */
@Component
public class UserPersistenceMapper {

    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId().getValue());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail().getValue());
        entity.setPhoneNumber(user.getPhoneNumber().getValue());
        entity.setAddress(toAddressEmbeddable(user.getAddress()));
        entity.setPasswordHash(user.getPasswordHash());
        entity.setCreatedAt(user.getCreatedTimestamp());
        entity.setUpdatedAt(user.getUpdatedTimestamp());
        return entity;
    }

    public User toDomain(UserEntity entity) {
        return User.reconstitute(
                UserId.of(entity.getId()),
                entity.getName(),
                Email.of(entity.getEmail()),
                PhoneNumber.of(entity.getPhoneNumber()),
                toAddress(entity.getAddress()),
                entity.getPasswordHash(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private AddressEmbeddable toAddressEmbeddable(Address address) {
        return new AddressEmbeddable(
                address.getLine1(),
                address.getLine2(),
                address.getLine3(),
                address.getTown(),
                address.getCounty(),
                address.getPostcode()
        );
    }

    private Address toAddress(AddressEmbeddable embeddable) {
        return Address.of(
                embeddable.getLine1(),
                embeddable.getLine2(),
                embeddable.getLine3(),
                embeddable.getTown(),
                embeddable.getCounty(),
                embeddable.getPostcode()
        );
    }
}
