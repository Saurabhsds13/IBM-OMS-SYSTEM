package com.dmart.oms.shipping.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dmart.oms.shipping.model.Shipment;
import com.dmart.oms.shipping.service.ShipmentService;

@RestController
@RequestMapping("/api/admin/shipping")
public class ShipmentController {
	private final ShipmentService service;

	public ShipmentController(ShipmentService service) {
		this.service = service;
	}

	@PostMapping("/create")
	public Shipment create(@RequestParam String orderNumber, @RequestParam String carrier) {
		return service.createShipment(orderNumber, carrier);
	}

	@PostMapping("/{id}/status")
	public Shipment updateStatus(@PathVariable Long id, @RequestParam String status) {
		return service.updateStatus(id, status);
	}

	@PostMapping("/{id}/event")
	public Shipment addEvent(@PathVariable Long id, @RequestParam String description) {
		return service.addEvent(id, description);
	}

	@GetMapping("/order/{orderNumber}")
	public List<Shipment> getByOrder(@PathVariable String orderNumber) {
		return service.getShipmentsByOrder(orderNumber);
	}
}