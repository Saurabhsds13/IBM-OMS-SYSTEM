package com.dmart.oms.Inventory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory")
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String productCode;
	private int availableQty;
	private int reservedQty;
	private String vendorName;
	private String location;
	private int demand;
	private int supply;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public int getAvailableQty() {
		return availableQty;
	}

	public void setAvailableQty(int availableQty) {
		this.availableQty = availableQty;
	}

	public int getReservedQty() {
		return reservedQty;
	}

	public void setReservedQty(int reservedQty) {
		this.reservedQty = reservedQty;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getDemand() {
		return demand;
	}

	public void setDemand(int demand) {
		this.demand = demand;
	}

	public int getSupply() {
		return supply;
	}

	public void setSupply(int supply) {
		this.supply = supply;
	}

	public Inventory(Long id, String productCode, int availableQty, int reservedQty, String vendorName, String location,
			int demand, int supply) {
		super();
		this.id = id;
		this.productCode = productCode;
		this.availableQty = availableQty;
		this.reservedQty = reservedQty;
		this.vendorName = vendorName;
		this.location = location;
		this.demand = demand;
		this.supply = supply;
	}

	public Inventory() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Inventory [id=" + id + ", productCode=" + productCode + ", availableQty=" + availableQty
				+ ", reservedQty=" + reservedQty + ", vendorName=" + vendorName + ", location=" + location + ", demand="
				+ demand + ", supply=" + supply + "]";
	}

}
