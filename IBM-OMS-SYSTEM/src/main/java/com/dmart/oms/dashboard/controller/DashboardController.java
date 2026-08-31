package com.dmart.oms.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dmart.oms.common.dto.ApiResponse;
import com.dmart.oms.dashboard.dto.DashboardSummary;
import com.dmart.oms.dashboard.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Read-only operational overview for the admin dashboard. Available to all
 * authenticated roles (VIEWER and above).
 */
@RestController
@RequestMapping("/api/v1/admin/dashboard")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dashboard", description = "Aggregated operational metrics")
public class DashboardController {

	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@Operation(summary = "Aggregated operational summary for the admin dashboard")
	@GetMapping("/summary")
	@PreAuthorize("hasAnyRole('VIEWER','OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<DashboardSummary>> summary() {
		return ResponseEntity.ok(ApiResponse.success(dashboardService.buildSummary(), "Dashboard summary"));
	}
}
