package com.dmart.oms.shipping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dmart.oms.shipping.model.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
	List<Shipment> findByOrderNumber(String orderNumber);
}
