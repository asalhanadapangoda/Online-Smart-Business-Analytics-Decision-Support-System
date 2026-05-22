package com.sbadss.controller;

import com.sbadss.common.ApiResponse;
import com.sbadss.dto.AuthResponse;
import com.sbadss.dto.LoginRequest;
import com.sbadss.dto.RegisterRequest;
import com.sbadss.service.AuthService;
import com.sbadss.util.ApiEndpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping({ApiEndpoints.AUTH_BASE, ApiEndpoints.AUTH_V1_BASE})
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(ApiEndpoints.AUTH_REGISTER)
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("REST request to register user: {}", request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(authService.register(request), "User registered successfully"));
    }

    @PostMapping(ApiEndpoints.AUTH_LOGIN)
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("REST request to login user: {}", request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(authService.login(request), "Login successful"));
    }
}
