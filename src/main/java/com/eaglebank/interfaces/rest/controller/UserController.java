package com.eaglebank.interfaces.rest.controller;

import com.eaglebank.application.user.UserService;
import com.eaglebank.interfaces.rest.dto.request.CreateUserRequest;
import com.eaglebank.interfaces.rest.dto.request.UpdateUserRequest;
import com.eaglebank.interfaces.rest.dto.response.UserResponse;
import com.eaglebank.interfaces.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * User REST Controller
 * Endpoint: /v1/users
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRestMapper mapper;


    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        var command = new UserService.CreateUserCommand(
                request.name(),
                request.email(),
                request.phoneNumber(),
                request.address().line1(),
                request.address().line2(),
                request.address().line3(),
                request.address().town(),
                request.address().county(),
                request.address().postcode(),
                request.password()
        );

        var user = userService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(user));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userId,
                                                Authentication authentication) {
        var user = userService.get(userId, authentication);
        return ResponseEntity.ok(mapper.toResponse(user));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String userId,
                                                   @Valid @RequestBody UpdateUserRequest request,
                                                   Authentication authentication) {
        var command = new UserService.UpdateUserCommand(
                userId,
                request.name(),
                request.email(),
                request.phoneNumber(),
                request.address().line1(),
                request.address().line2(),
                request.address().line3(),
                request.address().town(),
                request.address().county(),
                request.address().postcode()
        );

        var user = userService.update(command, authentication);
        return ResponseEntity.ok(mapper.toResponse(user));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId,
                                           Authentication authentication) {
        userService.delete(userId, authentication);
        return ResponseEntity.noContent().build();
    }
}
