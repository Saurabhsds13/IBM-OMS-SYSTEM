package com.dmart.oms.order.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dmart.oms.order.model.Order;
import com.dmart.oms.order.service.OrderService;

@RestController
@RequestMapping("/api/admin/orders")
public class OrderController {
	private final OrderService service;

	public OrderController(OrderService service) {
		this.service = service;
	}

	@GetMapping
	public List<Order> getAllOrders() {
		return service.getAllOrders();
	}

	@PostMapping("/{id}/approve")
	public Order approveOrder(@PathVariable Long id) {
		return service.approveOrder(id);
	}

	@PostMapping("/{id}/cancel")
	public Order cancelOrder(@PathVariable Long id) {
		return service.cancelOrder(id);
	}

	@PostMapping("/{id}/partial-ship")
	public Order partialShip(@PathVariable Long id, @RequestParam int qty) {
		return service.shipPartially(id, qty);
	}
}
