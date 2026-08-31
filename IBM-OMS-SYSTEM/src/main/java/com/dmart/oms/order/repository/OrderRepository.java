package com.dmart.oms.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dmart.oms.order.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findByOrderNumber(String orderNumber);

	long countByStatus(String status);

	List<Order> findByStatus(String status);

	List<Order> findByOrderNumberContainingIgnoreCase(String orderNumber);

	List<Order> findByStatusAndOrderNumberContainingIgnoreCase(String status, String orderNumber);

	/** Counts grouped by status in a single query: [status, count]. */
	@Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
	List<Object[]> countGroupedByStatus();
}
