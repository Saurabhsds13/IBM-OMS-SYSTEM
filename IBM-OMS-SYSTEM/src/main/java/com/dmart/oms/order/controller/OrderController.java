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
import com.dmart.oms.order.dto.BulkActionResult;
import com.dmart.oms.order.dto.BulkOrderActionRequest;
import com.dmart.oms.order.dto.OrderDTO;
import com.dmart.oms.order.dto.OrderIntakeRequest;
import com.dmart.oms.order.dto.OrderIntakeResult;
import com.dmart.oms.order.dto.OrderStatusHistoryDTO;
import com.dmart.oms.order.service.OrderHistoryService;
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
	private final OrderHistoryService historyService;

	public OrderController(OrderService service, OrderHistoryService historyService) {
		this.service = service;
		this.historyService = historyService;
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

	@Operation(summary = "Apply an action (APPROVE/CANCEL) to multiple orders; per-order results")
	@PostMapping("/bulk")
	@PreAuthorize("hasAnyRole('OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<BulkActionResult>> bulkAction(
			@Valid @RequestBody BulkOrderActionRequest request) {
		log.info("Bulk {} on {} orders", request.action(), request.orderIds().size());
		BulkActionResult result = service.bulkAction(request);
		return ResponseEntity.ok(ApiResponse.success(result,
				String.format("%d/%d succeeded", result.succeeded(), result.total())));
	}

	@Operation(summary = "List orders, optionally filtered by status and/or order-number substring")
	@GetMapping
	@PreAuthorize("hasAnyRole('VIEWER','OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<List<OrderDTO>>> getAllOrders(
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String orderNumber) {
		log.info("Fetching orders (status={}, orderNumber={})", status, orderNumber);

		List<OrderDTO> orders = service.searchOrders(status, orderNumber);

		if (orders.isEmpty()) {
			return ResponseEntity.ok(ApiResponse.success(orders, "No orders found"));
		}
		return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));
	}

	@Operation(summary = "Fetch a single order (with items) by its order number")
	@GetMapping("/by-number/{orderNumber}")
	@PreAuthorize("hasAnyRole('VIEWER','OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<OrderDTO>> getOrderByNumber(@PathVariable String orderNumber) {
		OrderDTO order = service.getOrderByNumber(orderNumber)
				.orElseThrow(() -> new BusinessException("Order " + orderNumber + " not found", ErrorCode.ORD_001));
		return ResponseEntity.ok(ApiResponse.success(order, "Order retrieved successfully"));
	}

	@Operation(summary = "Status transition audit trail for an order")
	@GetMapping("/by-number/{orderNumber}/history")
	@PreAuthorize("hasAnyRole('VIEWER','OPS_MANAGER','ADMIN')")
	public ResponseEntity<ApiResponse<List<OrderStatusHistoryDTO>>> getOrderHistory(@PathVariable String orderNumber) {
		List<OrderStatusHistoryDTO> history = historyService.getHistory(orderNumber);
		return ResponseEntity.ok(ApiResponse.success(history, "Order history retrieved"));
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