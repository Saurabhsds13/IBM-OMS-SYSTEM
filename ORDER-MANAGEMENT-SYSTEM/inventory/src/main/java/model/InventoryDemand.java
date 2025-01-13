package model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vsp_inventory_demand")
public class InventoryDemand {

	@Id
	private Long inventoryDemandKey;
	private Long inventoryItemKey;
	private String itemId;
	private String organizationCode;
	private String tagNumber;
	private Double unitCost;
	private Long shipNodeKey;
	private Integer quantity;
	private LocalDateTime demandShipDate;
	private LocalDateTime demandCancelDate;
	private String reference;
	private LocalDateTime createTs;
	private LocalDateTime modifyTs;
	private String createUserId;
	private String modifyUserId;
	private String createProgId;
	private String modifyProgId;

	public Long getInventoryDemandKey() {
		return inventoryDemandKey;
	}

	public void setInventoryDemandKey(Long inventoryDemandKey) {
		this.inventoryDemandKey = inventoryDemandKey;
	}

	public Long getInventoryItemKey() {
		return inventoryItemKey;
	}

	public void setInventoryItemKey(Long inventoryItemKey) {
		this.inventoryItemKey = inventoryItemKey;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public String getTagNumber() {
		return tagNumber;
	}

	public void setTagNumber(String tagNumber) {
		this.tagNumber = tagNumber;
	}

	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public Long getShipNodeKey() {
		return shipNodeKey;
	}

	public void setShipNodeKey(Long shipNodeKey) {
		this.shipNodeKey = shipNodeKey;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public LocalDateTime getDemandShipDate() {
		return demandShipDate;
	}

	public void setDemandShipDate(LocalDateTime demandShipDate) {
		this.demandShipDate = demandShipDate;
	}

	public LocalDateTime getDemandCancelDate() {
		return demandCancelDate;
	}

	public void setDemandCancelDate(LocalDateTime demandCancelDate) {
		this.demandCancelDate = demandCancelDate;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public LocalDateTime getCreateTs() {
		return createTs;
	}

	public void setCreateTs(LocalDateTime createTs) {
		this.createTs = createTs;
	}

	public LocalDateTime getModifyTs() {
		return modifyTs;
	}

	public void setModifyTs(LocalDateTime modifyTs) {
		this.modifyTs = modifyTs;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getModifyUserId() {
		return modifyUserId;
	}

	public void setModifyUserId(String modifyUserId) {
		this.modifyUserId = modifyUserId;
	}

	public String getCreateProgId() {
		return createProgId;
	}

	public void setCreateProgId(String createProgId) {
		this.createProgId = createProgId;
	}

	public String getModifyProgId() {
		return modifyProgId;
	}

	public void setModifyProgId(String modifyProgId) {
		this.modifyProgId = modifyProgId;
	}

}
