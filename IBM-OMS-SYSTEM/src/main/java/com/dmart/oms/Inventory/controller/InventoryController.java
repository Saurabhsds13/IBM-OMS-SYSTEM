package com.dmart.oms.Inventory.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dmart.oms.Inventory.dto.InventoryUpdateRequest;
import com.dmart.oms.Inventory.model.Inventory;
import com.dmart.oms.Inventory.service.InventoryService;
import com.dmart.oms.common.dto.ApiResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

	private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

	private final InventoryService service;

	public InventoryController(InventoryService service) {
		this.service = service;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('VIEWER','OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<List<Inventory>>> getAllStock() {

		log.info("Fetching all inventory stock");
		List<Inventory> stock = service.getAllStock();
		return ResponseEntity.ok(ApiResponse.success(stock));

	}

	@GetMapping("/inventory/{productCode}")
	@PreAuthorize("hasAnyRole('VIEWER','OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<Inventory>> getInventory(
			@PathVariable @NotBlank(message = "Product code cannot be blank") String productCode) {

		log.info("Fetching inventory for product [{}]", productCode);
		Inventory stock = service.getInventory(productCode);

		return ResponseEntity.ok(ApiResponse.success(stock, "Inventory fetched successfully"));
	}

	@PostMapping("/reserve")
	@PreAuthorize("hasAnyRole('OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<Inventory>> reserveStock(
			@RequestParam @NotBlank(message = "Product code cannot be blank") String productCode,
			@RequestParam @Min(value = 1, message = "Quantity must be at least 1") int qty) {

		log.info("Reserving {} units of product [{}]", qty, productCode);
		Inventory reserved = service.reserveStock(productCode, qty);

		return ResponseEntity.ok(ApiResponse.success(reserved, "Stock reserved successfully"));
	}

	@PostMapping("/release")
	@PreAuthorize("hasAnyRole('OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<Inventory>> releaseStock(
			@RequestParam @NotBlank(message = "Product code cannot be blank") String productCode,
			@RequestParam @Min(value = 1, message = "Quantity must be at least 1") int qty) {

		log.info("Releasing {} units of product [{}]", qty, productCode);
		Inventory released = service.releaseStock(productCode, qty);

		return ResponseEntity.ok(ApiResponse.success(released, "Stock released successfully"));
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<Inventory>> addStock(@Valid @RequestBody Inventory inventory) {

		log.info("Adding new stock: {}", inventory);
		Inventory added = service.addStock(inventory);
		return ResponseEntity.ok(ApiResponse.success(added, "Stock added successfully"));
	}

	@PutMapping("/inventory/{productCode}")
	@PreAuthorize("hasAnyRole('OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<Inventory>> updateInventory(
			@PathVariable @NotBlank(message = "Product code cannot be blank") String productCode,
			@RequestBody InventoryUpdateRequest request) {

		log.info("Updating inventory for product [{}]", productCode);
		Inventory updated = service.updateInventory(productCode, request);

		return ResponseEntity.ok(ApiResponse.success(updated, "Inventory updated successfully"));
	}

}
