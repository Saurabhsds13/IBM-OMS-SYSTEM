package com.dmart.oms.order.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dmart.oms.common.dto.ApiResponse;
import com.dmart.oms.common.exception.BusinessException;
import com.dmart.oms.common.exception.ErrorCode;
import com.dmart.oms.order.dto.OrderDTO;
import com.dmart.oms.order.dto.OrderIntakeRequest;
import com.dmart.oms.order.dto.OrderIntakeResult;
import com.dmart.oms.order.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/admin/orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);
	private final OrderService service;

	public OrderController(OrderService service) {
		this.service = service;
	}

	/**
	 * Ingests an order placed upstream (e.g. by the QuickBasket storefront).
	 * Idempotent on orderNumber: returns 201 when a new order is created, or 200
	 * when an order with the same number already exists, so upstream retries are
	 * safe. Requires OPS_MANAGER or ADMIN.
	 */
	@Operation(summary = "Ingest an order placed by an upstream system (idempotent by orderNumber)")
	@PostMapping("/intake")
	@PreAuthorize("hasAnyRole('OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<OrderDTO>> intakeOrder(@Valid @RequestBody OrderIntakeRequest request) {
		log.info("Ingesting order with orderNumber={}", request.orderNumber());

		OrderIntakeResult result = service.ingestOrder(request);

		if (result.created()) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(ApiResponse.success(result.order(), "Order ingested successfully"));
		}
		return ResponseEntity.ok(ApiResponse.success(result.order(), "Order already exists; no changes made"));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('VIEWER','OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<List<OrderDTO>>> getAllOrders() {
		log.info("Fetching all orders");

		List<OrderDTO> orders = service.getAllOrders();

		if (orders.isEmpty()) {
			log.info("No orders found in the system");
			return ResponseEntity.ok(ApiResponse.success(orders, "No orders found"));
		}
		return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('VIEWER','OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long id) {
		log.info("Fetching order with id={}", id);

		OrderDTO order = service.getOrderById(id)
				.orElseThrow(() -> new BusinessException("Order with id=" + id + " not found", ErrorCode.ORD_001));

		return ResponseEntity.ok(ApiResponse.success(order, "Order retrieved successfully"));
	}

	@PostMapping("/{id}/approve")
	@PreAuthorize("hasAnyRole('OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<OrderDTO>> approveOrder(@PathVariable Long id) {
		log.info("Approving order with id={}", id);

		OrderDTO dto = service.approveOrder(id);
		return ResponseEntity.ok(ApiResponse.success(dto, "Order approved successfully"));
	}

	@PostMapping("/{id}/cancel")
	@PreAuthorize("hasAnyRole('OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(@PathVariable Long id) {
		log.info("Cancelling order with id={}", id);

		OrderDTO dto = service.cancelOrder(id);
		return ResponseEntity.ok(ApiResponse.success(dto, "Order cancelled successfully"));
	}

	@PostMapping("/{id}/partial-ship")
	@PreAuthorize("hasAnyRole('OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<OrderDTO>> partialShip(@PathVariable Long id,
			@RequestParam @Min(value = 1, message = "Quantity must be at least 1") int qty) {

		log.info("Partially shipping {} units for order with id={}", qty, id);
		OrderDTO dto = service.shipPartially(id, qty);
		return ResponseEntity.ok(ApiResponse.success(dto, "Order partially shipped"));
	}

}