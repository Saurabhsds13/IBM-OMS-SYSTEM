package com.dmart.oms.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dmart.oms.order.model.OrderStatusHistory;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

	/** Full transition trail for an order, oldest first. */
	List<OrderStatusHistory> findByOrderNumberOrderByChangedAtAsc(String orderNumber);
}
