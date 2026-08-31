package com.dmart.oms.shipping.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmart.oms.order.service.OrderService;
import com.dmart.oms.shipping.model.Shipment;
import com.dmart.oms.shipping.model.ShipmentEvent;
import com.dmart.oms.shipping.repository.ShipmentRepository;

@Service
public class ShipmentService {
	private final ShipmentRepository repo;
	private final OrderService orderService;

	public ShipmentService(ShipmentRepository repo, OrderService orderService) {
		this.repo = repo;
		this.orderService = orderService;
	}

	/**
	 * Creates a shipment and advances the associated order to SHIPPED, which emits
	 * an ORDER_SHIPPED lifecycle event (outbox -> Kafka + SSE). markShipped is
	 * idempotent and a no-op for terminal order states.
	 */
	@Transactional
	public Shipment createShipment(String orderNumber, String carrier) {
		Shipment shipment = new Shipment();
		shipment.setOrderNumber(orderNumber);
		shipment.setCarrier(carrier);
		shipment.setStatus("CREATED");
		shipment.setCreatedAt(LocalDateTime.now());
		Shipment saved = repo.save(shipment);

		orderService.markShipped(orderNumber);
		return saved;
	}

	/**
	 * Updates a shipment's own status. When a shipment reaches DELIVERED, the
	 * order is (idempotently) ensured to be SHIPPED in OMS vocabulary.
	 */
	@Transactional
	public Shipment updateStatus(Long id, String status) {
		Shipment shipment = repo.findById(id).orElseThrow();
		shipment.setStatus(status);
		Shipment saved = repo.save(shipment);

		if ("DELIVERED".equalsIgnoreCase(status) || "IN_TRANSIT".equalsIgnoreCase(status)) {
			orderService.markShipped(shipment.getOrderNumber());
		}
		return saved;
	}

	@Transactional
	public Shipment addEvent(Long shipmentId, String description) {
		Shipment shipment = repo.findById(shipmentId).orElseThrow();
		ShipmentEvent event = new ShipmentEvent();
		event.setDescription(description);
		event.setEventTime(LocalDateTime.now());
		event.setShipment(shipment);
		shipment.getEvents().add(event);
		return repo.save(shipment);
	}

	public List<Shipment> getShipmentsByOrder(String orderNumber) {
		return repo.findByOrderNumber(orderNumber);
	}
}
