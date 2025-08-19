package com.dmart.oms.analytics.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//analytics/model/KpiSnapshot.java
@Entity
@Table(name = "kpi_snapshot")
public class KpiSnapshot {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDateTime computedAt;
	private double revenueLast24h;
	private long ordersLast24h;
	private double aovLast24h;
	private double refundRateLast7d; // refunds/orders
	private double fulfillmentSlaHitPct; // delivered<=X days / delivered

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getComputedAt() {
		return computedAt;
	}

	public void setComputedAt(LocalDateTime computedAt) {
		this.computedAt = computedAt;
	}

	public double getRevenueLast24h() {
		return revenueLast24h;
	}

	public void setRevenueLast24h(double revenueLast24h) {
		this.revenueLast24h = revenueLast24h;
	}

	public long getOrdersLast24h() {
		return ordersLast24h;
	}

	public void setOrdersLast24h(long ordersLast24h) {
		this.ordersLast24h = ordersLast24h;
	}

	public double getAovLast24h() {
		return aovLast24h;
	}

	public void setAovLast24h(double aovLast24h) {
		this.aovLast24h = aovLast24h;
	}

	public double getRefundRateLast7d() {
		return refundRateLast7d;
	}

	public void setRefundRateLast7d(double refundRateLast7d) {
		this.refundRateLast7d = refundRateLast7d;
	}

	public double getFulfillmentSlaHitPct() {
		return fulfillmentSlaHitPct;
	}

	public void setFulfillmentSlaHitPct(double fulfillmentSlaHitPct) {
		this.fulfillmentSlaHitPct = fulfillmentSlaHitPct;
	}

	public KpiSnapshot(Long id, LocalDateTime computedAt, double revenueLast24h, long ordersLast24h, double aovLast24h,
			double refundRateLast7d, double fulfillmentSlaHitPct) {
		super();
		this.id = id;
		this.computedAt = computedAt;
		this.revenueLast24h = revenueLast24h;
		this.ordersLast24h = ordersLast24h;
		this.aovLast24h = aovLast24h;
		this.refundRateLast7d = refundRateLast7d;
		this.fulfillmentSlaHitPct = fulfillmentSlaHitPct;
	}

	public KpiSnapshot() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "KpiSnapshot [id=" + id + ", computedAt=" + computedAt + ", revenueLast24h=" + revenueLast24h
				+ ", ordersLast24h=" + ordersLast24h + ", aovLast24h=" + aovLast24h + ", refundRateLast7d="
				+ refundRateLast7d + ", fulfillmentSlaHitPct=" + fulfillmentSlaHitPct + "]";
	}

}
