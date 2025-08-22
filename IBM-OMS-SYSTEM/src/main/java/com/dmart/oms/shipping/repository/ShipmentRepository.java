package com.dmart.oms.shipping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dmart.oms.shipping.model.Shipment;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
	List<Shipment> findByOrderNumber(String orderNumber);
}
