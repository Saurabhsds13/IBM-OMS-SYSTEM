package com.dmart.oms.Inventory.dto;

public class InventoryUpdateRequest {

	private Integer availableQty;
	private Integer reservedQty;
	private String vendorName;
	private String location;
	private Integer demand;
	private Integer supply;

	public Integer getAvailableQty() {
		return availableQty;
	}

	public void setAvailableQty(Integer availableQty) {
		this.availableQty = availableQty;
	}

	public Integer getReservedQty() {
		return reservedQty;
	}

	public void setReservedQty(Integer reservedQty) {
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

	public Integer getDemand() {
		return demand;
	}

	public void setDemand(Integer demand) {
		this.demand = demand;
	}

	public Integer getSupply() {
		return supply;
	}

	public void setSupply(Integer supply) {
		this.supply = supply;
	}

	public InventoryUpdateRequest(Integer availableQty, Integer reservedQty, String vendorName, String location,
			Integer demand, Integer supply) {
		super();
		this.availableQty = availableQty;
		this.reservedQty = reservedQty;
		this.vendorName = vendorName;
		this.location = location;
		this.demand = demand;
		this.supply = supply;
	}

	public InventoryUpdateRequest() {
		super();
	}

	@Override
	public String toString() {
		return "InventoryUpdateRequest [availableQty=" + availableQty + ", reservedQty=" + reservedQty + ", vendorName="
				+ vendorName + ", location=" + location + ", demand=" + demand + ", supply=" + supply + "]";
	}

}
