package com.dmart.oms.shipping.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shipments")
public class Shipment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String orderNumber;
	private String status; // CREATED, IN_TRANSIT, DELIVERED, RETURNED
	private String carrier; // FedEx, DHL, etc.
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL)
	private List<ShipmentEvent> events;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<ShipmentEvent> getEvents() {
		return events;
	}

	public void setEvents(List<ShipmentEvent> events) {
		this.events = events;
	}

	public Shipment(Long id, String orderNumber, String status, String carrier, LocalDateTime createdAt,
			List<ShipmentEvent> events) {
		super();
		this.id = id;
		this.orderNumber = orderNumber;
		this.status = status;
		this.carrier = carrier;
		this.createdAt = createdAt;
		this.events = events;
	}

	public Shipment() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Shipment [id=" + id + ", orderNumber=" + orderNumber + ", status=" + status + ", carrier=" + carrier
				+ ", createdAt=" + createdAt + ", events=" + events + "]";
	}

}
