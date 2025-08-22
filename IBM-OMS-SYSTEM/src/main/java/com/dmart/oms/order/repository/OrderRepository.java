package com.dmart.oms.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dmart.oms.order.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	Optional<Order> findByOrderNumber(String orderNumber);
}
