package com.eaglebank.application.user;

import com.eaglebank.domain.exception.UnauthorizedAccessException;
import com.eaglebank.domain.exception.UserHasAccountsException;
import com.eaglebank.domain.exception.UserNotFoundException;
import com.eaglebank.domain.model.user.*;
import com.eaglebank.domain.repository.AccountRepository;
import com.eaglebank.domain.repository.UserRepository;
import com.eaglebank.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements
        UserService,
        AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public User create(CreateUserCommand command) {
        // Check if email already exists
        Email email = Email.of(command.email());
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        // Create value objects
        PhoneNumber phoneNumber = PhoneNumber.of(command.phoneNumber());
        Address address = Address.of(
                command.line1(),
                command.line2(),
                command.line3(),
                command.town(),
                command.county(),
                command.postcode()
        );

        // Hash password
        String passwordHash = passwordEncoder.encode(command.password());

        // Create user
        User user = User.create(
                UserId.generate(),
                command.name(),
                email,
                phoneNumber,
                address,
                passwordHash
        );

        // Save and return
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User get(String userId, Authentication authentication) {
        UserId id = UserId.of(userId);
        checkUserRequestingOwnProfile(id, authentication);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User update(UpdateUserCommand command, Authentication authentication) {
        UserId userId = UserId.of(command.userId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        checkUserRequestingOwnProfile(userId, authentication);

        PhoneNumber phoneNumber = command.phoneNumber() != null
                ? PhoneNumber.of(command.phoneNumber())
                : null;

        Address address = (command.line1() != null && command.town() != null && command.county() != null &&
                command.postcode() != null)
                ? Address.of(command.line1(), command.line2(), command.line3(), command.town(), command.county(), command.postcode())
                : null;

        user.updatePersonalInfo(
                command.name(),
                phoneNumber,
                address
        );

        return userRepository.save(user);
    }

    @Override
    public void delete(String userId, Authentication authentication) {
        UserId id = UserId.of(userId);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        checkUserRequestingOwnProfile(id, authentication);

        // Check if user has accounts
        long accountCount = accountRepository.countByOwnerId(id);
        if (accountCount > 0) {
            throw new UserHasAccountsException(id, accountCount);
        }

        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResult authenticate(AuthenticationCommand command) {
        Email email = Email.of(command.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Verify password
        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Generate JWT token
        String token = jwtTokenProvider.createToken(
                user.getId().getValue(),
                user.getEmail().getValue()
        );

        return new AuthenticationResult(
                user.getId().getValue(),
                user.getEmail().getValue(),
                token
        );
    }

    private void checkUserRequestingOwnProfile(UserId userId, Authentication authentication) {
        String requestingUserId = (String) authentication.getPrincipal();
        if (!userId.getValue().equals(requestingUserId)) {
            throw new UnauthorizedAccessException("You do not have permission for this action");
        }
    }
}
