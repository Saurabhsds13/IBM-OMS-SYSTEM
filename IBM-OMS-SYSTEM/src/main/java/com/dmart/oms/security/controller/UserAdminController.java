package com.dmart.oms.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dmart.oms.common.dto.ApiResponse;
import com.dmart.oms.security.dto.CreateUserRequest;
import com.dmart.oms.security.dto.UserResponse;
import com.dmart.oms.security.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Admin-only user provisioning API (Requirement 7). Every endpoint requires the
 * ADMIN role (Requirement 7.5).
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User Management", description = "Admin-only user provisioning")
public class UserAdminController {

	private final UserService userService;

	public UserAdminController(UserService userService) {
		this.userService = userService;
	}

	@Operation(summary = "Create a new user with one or more roles (ADMIN only)")
	@PostMapping
	public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
		UserResponse created = userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(created, "User created successfully"));
	}
}
