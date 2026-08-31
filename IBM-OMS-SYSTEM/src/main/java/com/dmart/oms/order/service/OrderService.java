package com.dmart.oms.order.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmart.oms.common.event.OutboxEventPublisher;
import com.dmart.oms.common.exception.BusinessException;
import com.dmart.oms.common.exception.ErrorCode;
import com.dmart.oms.common.exception.ResourceNotFoundException;
import com.dmart.oms.common.utils.OrderMapper;
import com.dmart.oms.order.dto.OrderDTO;
import com.dmart.oms.order.dto.OrderIntakeRequest;
import com.dmart.oms.order.dto.OrderIntakeResult;
import com.dmart.oms.order.dto.OrderItemIntakeRequest;
import com.dmart.oms.order.model.Order;
import com.dmart.oms.order.model.OrderItem;
import com.dmart.oms.order.repository.OrderRepository;

@Service
public class OrderService {

	private final OrderRepository repo;
	private final OutboxEventPublisher publisher;

	public OrderService(OrderRepository repo, OutboxEventPublisher publisher) {
		this.repo = repo;
		this.publisher = publisher;
	}

	public List<OrderDTO> getAllOrders() {
		return repo.findAll().stream().map(OrderMapper::toDTO).toList();
	}

	/**
	 * Ingests an order handed off from an upstream system (e.g. the QuickBasket
	 * storefront). Idempotent on {@code orderNumber}: if an order with the same
	 * number already exists, it is returned unchanged and no new event is emitted,
	 * so upstream retries are safe. A newly ingested order is persisted with
	 * status {@code PENDING} and records exactly one {@code ORDER_PLACED} outbox
	 * event within the same transaction.
	 */
	@Transactional
	public OrderIntakeResult ingestOrder(OrderIntakeRequest request) {
		Optional<Order> existing = repo.findByOrderNumber(request.orderNumber());
		if (existing.isPresent()) {
			return new OrderIntakeResult(OrderMapper.toDTO(existing.get()), false);
		}

		Order order = new Order();
		order.setOrderNumber(request.orderNumber());
		order.setStatus("PENDING");
		order.setCreatedAt(Instant.now());

		List<OrderItem> items = new ArrayList<>();
		for (OrderItemIntakeRequest line : request.items()) {
			OrderItem item = new OrderItem();
			item.setProductCode(line.productCode());
			item.setQuantity(line.quantity());
			item.setShippedQuantity(0);
			item.setOrder(order);
			items.add(item);
		}
		order.setItems(items);

		Order saved = repo.save(order);
		OrderDTO dto = OrderMapper.toDTO(saved);

		// Single ORDER_PLACED event, atomic with the order insert.
		publisher.publish("ORDER", saved.getOrderNumber(), "ORDER_PLACED", dto);

		return new OrderIntakeResult(dto, true);
	}

	@Transactional
	public OrderDTO approveOrder(Long id) {

		Order order = repo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id=" + id));

		if (order.getStatus().equals("APPROVED")) {
			throw new BusinessException("Order is already approved", ErrorCode.ORD_002);
		}

		order.setStatus("APPROVED");
		repo.save(order);

		OrderDTO dto = OrderMapper.toDTO(order);

		// Record exactly one ORDER_APPROVED outbox event within this same
		// transaction (Requirements 12.1, 12.2). The publisher persists the outbox
		// row; the previous code also saved a duplicate row manually, which is
		// removed here to guarantee a single event.
		publisher.publish("ORDER", order.getOrderNumber(), "ORDER_APPROVED", dto);
		return dto;
	}

	@Transactional
	public OrderDTO cancelOrder(Long id) {
		Order order = repo.findById(id)
				.orElseThrow(() -> new BusinessException("Order not found with id=" + id, ErrorCode.ORD_001));

		String status = order.getStatus();

		// Already cancelled -> conflict, leave unchanged (Requirement 13.2).
		if ("CANCELLED".equals(status)) {
			throw new BusinessException("Order is already cancelled", ErrorCode.ORD_002);
		}

		// Shipped or partially shipped -> conflict, leave unchanged (Requirement 13.3).
		if ("SHIPPED".equals(status) || "PARTIALLY_SHIPPED".equals(status)) {
			throw new BusinessException("Order cannot be cancelled once shipped", ErrorCode.ORD_002);
		}

		// Valid transition: cancel within a single transaction (Requirement 13.1, 13.4).
		order.setStatus("CANCELLED");
		Order savedOrder = repo.save(order);

		return OrderMapper.toDTO(savedOrder);
	}

	public Optional<OrderDTO> getOrderById(Long id) {
		return repo.findById(id).map(OrderMapper::toDTO);
	}

	// Unique feature: Partial shipment
	public OrderDTO shipPartially(Long id, int quantity) {
		Order order = repo.findById(id).orElseThrow();
		order.setStatus("PARTIALLY_SHIPPED");
		// logic to adjust shipped quantities
		Order savedOrder = repo.save(order);

		return OrderMapper.toDTO(savedOrder);
	}
}