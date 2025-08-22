package com.dmart.oms.common.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

//notification/model/OutboxEvent.java
@Entity
@Table(name = "outbox_events", indexes = { @Index(name = "idx_outbox_status", columnList = "status"),
		@Index(name = "idx_outbox_created", columnList = "created_at") })
public class OutboxEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String aggregateType; // ORDER, PAYMENT, SHIPMENT
	private String aggregateId; // orderNumber etc.
	private String eventType; // ORDER_PLACED, PAYMENT_FAILED...
	@Lob
	private String payload; // JSON payload
	private String status; // PENDING, SENDING, SENT, DEAD_LETTER
	private int attemptCount = 0;
	private Instant createdAt;
	private Instant lastAttemptAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAggregateType() {
		return aggregateType;
	}

	public void setAggregateType(String aggregateType) {
		this.aggregateType = aggregateType;
	}

	public String getAggregateId() {
		return aggregateId;
	}

	public void setAggregateId(String aggregateId) {
		this.aggregateId = aggregateId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getAttemptCount() {
		return attemptCount;
	}

	public void setAttemptCount(int attemptCount) {
		this.attemptCount = attemptCount;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getLastAttemptAt() {
		return lastAttemptAt;
	}

	public void setLastAttemptAt(Instant lastAttemptAt) {
		this.lastAttemptAt = lastAttemptAt;
	}

	public OutboxEvent(Long id, String aggregateType, String aggregateId, String eventType, String payload,
			String status, int attemptCount, Instant createdAt, Instant lastAttemptAt) {
		super();
		this.id = id;
		this.aggregateType = aggregateType;
		this.aggregateId = aggregateId;
		this.eventType = eventType;
		this.payload = payload;
		this.status = status;
		this.attemptCount = attemptCount;
		this.createdAt = createdAt;
		this.lastAttemptAt = lastAttemptAt;
	}

	public OutboxEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "OutboxEvent [id=" + id + ", aggregateType=" + aggregateType + ", aggregateId=" + aggregateId
				+ ", eventType=" + eventType + ", payload=" + payload + ", status=" + status + ", attemptCount="
				+ attemptCount + ", createdAt=" + createdAt + ", lastAttemptAt=" + lastAttemptAt + "]";
	}
}
