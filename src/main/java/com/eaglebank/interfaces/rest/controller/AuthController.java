package com.eaglebank.interfaces.rest.controller;

import com.eaglebank.application.user.AuthService;
import com.eaglebank.interfaces.rest.dto.request.AuthenticationRequest;
import com.eaglebank.interfaces.rest.dto.response.AuthenticationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request) {
        var command = new AuthService.AuthenticationCommand(
                request.email(),
                request.password()
        );

        var result = authService.authenticate(command);

        var response = new AuthenticationResponse(
                result.userId(),
                result.email(),
                result.token()
        );

        return ResponseEntity.ok(response);
    }
}
