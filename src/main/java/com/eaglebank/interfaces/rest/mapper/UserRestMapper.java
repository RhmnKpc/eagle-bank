package com.eaglebank.interfaces.rest.mapper;

import com.eaglebank.domain.model.user.User;
import com.eaglebank.interfaces.rest.dto.request.AddressDto;
import com.eaglebank.interfaces.rest.dto.response.UserResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

/**
 * Mapper between User domain model and REST DTOs
 */
@Component
public class UserRestMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId().getValue(),
            user.getName(),
            user.getEmail().getValue(),
            user.getPhoneNumber().getValue(),
            new AddressDto(
                user.getAddress().getLine1(),
                user.getAddress().getLine2(),
                user.getAddress().getLine3(),
                user.getAddress().getTown(),
                user.getAddress().getCounty(),
                user.getAddress().getPostcode()
            ),
            user.getCreatedTimestamp().atOffset(ZoneOffset.UTC),
            user.getUpdatedTimestamp().atOffset(ZoneOffset.UTC)
        );
    }
}
