package com.dmart.oms.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dmart.oms.common.dto.ApiResponse;
import com.dmart.oms.security.dto.LoginRequest;
import com.dmart.oms.security.dto.RefreshRequest;
import com.dmart.oms.security.dto.TokenResponse;
import com.dmart.oms.security.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Public authentication endpoints (Requirements 1, 2). These are permitted
 * without a token by the Security_Filter.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Login and token refresh")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@Operation(summary = "Authenticate and obtain access and refresh tokens")
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
		TokenResponse tokens = authService.login(request.getUsername(), request.getPassword());
		return ResponseEntity.ok(ApiResponse.success(tokens, "Login successful"));
	}

	@Operation(summary = "Exchange a valid refresh token for a new access token")
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
		TokenResponse tokens = authService.refresh(request.getRefreshToken());
		return ResponseEntity.ok(ApiResponse.success(tokens, "Token refreshed"));
	}
}
