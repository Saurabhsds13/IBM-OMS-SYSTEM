package com.dmart.oms.order.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * An immutable audit record of a single order status transition: who changed
 * it, from what status to what status, and when. Written within the same
 * transaction as the status change so the trail can never diverge from the
 * order's actual state.
 */
@Entity
@Table(name = "order_status_history", indexes = {
		@Index(name = "idx_osh_order_number", columnList = "order_number"),
		@Index(name = "idx_osh_order_id", columnList = "order_id")
})
public class OrderStatusHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "order_number", nullable = false)
	private String orderNumber;

	/** Previous status; null when the order was first created. */
	@Column(name = "from_status")
	private String fromStatus;

	@Column(name = "to_status", nullable = false)
	private String toStatus;

	/** Username of the actor, or a system marker for automated transitions. */
	@Column(name = "changed_by", nullable = false)
	private String changedBy;

	@Column(name = "changed_at", nullable = false)
	private Instant changedAt;

	public OrderStatusHistory() {
	}

	public OrderStatusHistory(Long orderId, String orderNumber, String fromStatus, String toStatus, String changedBy,
			Instant changedAt) {
		this.orderId = orderId;
		this.orderNumber = orderNumber;
		this.fromStatus = fromStatus;
		this.toStatus = toStatus;
		this.changedBy = changedBy;
		this.changedAt = changedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getFromStatus() {
		return fromStatus;
	}

	public void setFromStatus(String fromStatus) {
		this.fromStatus = fromStatus;
	}

	public String getToStatus() {
		return toStatus;
	}

	public void setToStatus(String toStatus) {
		this.toStatus = toStatus;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	public Instant getChangedAt() {
		return changedAt;
	}

	public void setChangedAt(Instant changedAt) {
		this.changedAt = changedAt;
	}
}
