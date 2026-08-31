package com.dmart.oms.order.service;

import java.time.Instant;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmart.oms.order.dto.OrderStatusHistoryDTO;
import com.dmart.oms.order.model.OrderStatusHistory;
import com.dmart.oms.order.repository.OrderStatusHistoryRepository;

/**
 * Records and reads order status transitions (the audit trail). {@code record}
 * is expected to be called inside the same transaction as the status change.
 */
@Service
public class OrderHistoryService {

	static final String SYSTEM_ACTOR = "system";

	private final OrderStatusHistoryRepository repo;

	public OrderHistoryService(OrderStatusHistoryRepository repo) {
		this.repo = repo;
	}

	/**
	 * Appends a transition record. {@code fromStatus} may be null (e.g. on order
	 * creation). The actor is resolved from the security context, falling back to
	 * a system marker for unauthenticated/automated flows.
	 */
	@Transactional
	public void record(Long orderId, String orderNumber, String fromStatus, String toStatus) {
		OrderStatusHistory entry = new OrderStatusHistory(
				orderId, orderNumber, fromStatus, toStatus, currentActor(), Instant.now());
		repo.save(entry);
	}

	@Transactional(readOnly = true)
	public List<OrderStatusHistoryDTO> getHistory(String orderNumber) {
		return repo.findByOrderNumberOrderByChangedAtAsc(orderNumber).stream()
				.map(h -> new OrderStatusHistoryDTO(h.getId(), h.getFromStatus(), h.getToStatus(), h.getChangedBy(),
						h.getChangedAt()))
				.toList();
	}

	private String currentActor() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && auth.getName() != null
				&& !"anonymousUser".equals(auth.getName())) {
			return auth.getName();
		}
		return SYSTEM_ACTOR;
	}
}
