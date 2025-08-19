package com.dmart.oms.analytics.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

//analytics/model/DailySalesAgg.java
@Entity
@Table(name = "daily_sales_agg", indexes = { @Index(name = "idx_dsa_date", columnList = "date"),
		@Index(name = "idx_dsa_vendor_date", columnList = "vendorName,date"),
		@Index(name = "idx_dsa_product_date", columnList = "productCode,date") })
public class DailySalesAgg {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDate date; // partition key
	private String vendorName; // null = global
	private String productCode; // optional
	private double revenue; // sum(payments.SUCCESS.amount)
	private long orders; // count(orders)
	private double aov; // revenue / orders
	private long cancellations; // count(orders.CANCELLED)
	private long backorders; // count(orders.BACKORDERED)

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public double getRevenue() {
		return revenue;
	}

	public void setRevenue(double revenue) {
		this.revenue = revenue;
	}

	public long getOrders() {
		return orders;
	}

	public void setOrders(long orders) {
		this.orders = orders;
	}

	public double getAov() {
		return aov;
	}

	public void setAov(double aov) {
		this.aov = aov;
	}

	public long getCancellations() {
		return cancellations;
	}

	public void setCancellations(long cancellations) {
		this.cancellations = cancellations;
	}

	public long getBackorders() {
		return backorders;
	}

	public void setBackorders(long backorders) {
		this.backorders = backorders;
	}

	public DailySalesAgg(Long id, LocalDate date, String vendorName, String productCode, double revenue, long orders,
			double aov, long cancellations, long backorders) {
		super();
		this.id = id;
		this.date = date;
		this.vendorName = vendorName;
		this.productCode = productCode;
		this.revenue = revenue;
		this.orders = orders;
		this.aov = aov;
		this.cancellations = cancellations;
		this.backorders = backorders;
	}

	public DailySalesAgg() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "DailySalesAgg [id=" + id + ", date=" + date + ", vendorName=" + vendorName + ", productCode="
				+ productCode + ", revenue=" + revenue + ", orders=" + orders + ", aov=" + aov + ", cancellations="
				+ cancellations + ", backorders=" + backorders + "]";
	}

}
