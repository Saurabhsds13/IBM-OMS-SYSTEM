package com.dmart.oms.payment.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String orderNumber;
	private String status; // PENDING, SUCCESS, FAILED, REFUNDED, DEAD_LETTER
	private double amount;
	private LocalDateTime createdAt;
	private int retryCount = 0;

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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public Payment(Long id, String orderNumber, String status, double amount, LocalDateTime createdAt, int retryCount) {
		super();
		this.id = id;
		this.orderNumber = orderNumber;
		this.status = status;
		this.amount = amount;
		this.createdAt = createdAt;
		this.retryCount = retryCount;
	}

	public Payment() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Payment [id=" + id + ", orderNumber=" + orderNumber + ", status=" + status + ", amount=" + amount
				+ ", createdAt=" + createdAt + ", retryCount=" + retryCount + "]";
	}
}
