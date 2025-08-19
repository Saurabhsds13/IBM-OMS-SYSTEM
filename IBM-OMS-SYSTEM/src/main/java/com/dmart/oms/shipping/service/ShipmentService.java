package com.dmart.oms.shipping.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dmart.oms.shipping.model.Shipment;
import com.dmart.oms.shipping.model.ShipmentEvent;
import com.dmart.oms.shipping.repository.ShipmentRepository;

@Service
public class ShipmentService {
	private final ShipmentRepository repo;

	public ShipmentService(ShipmentRepository repo) {
		this.repo = repo;
	}

	public Shipment createShipment(String orderNumber, String carrier) {
		Shipment shipment = new Shipment();
		shipment.setOrderNumber(orderNumber);
		shipment.setCarrier(carrier);
		shipment.setStatus("CREATED");
		shipment.setCreatedAt(LocalDateTime.now());
		return repo.save(shipment);
	}

	public Shipment updateStatus(Long id, String status) {
		Shipment shipment = repo.findById(id).orElseThrow();
		shipment.setStatus(status);
		return repo.save(shipment);
	}

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
