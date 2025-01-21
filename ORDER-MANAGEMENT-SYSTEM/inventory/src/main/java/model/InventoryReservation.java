package model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vsp_inventory_reservation")
public class InventoryReservation {

	@Id
	private Long inventoryReservationKey;
	private Long inventoryItemKey;
	private String organizationCode;
	private Long shipNodeKey;
	private Long ownerKey;
	private Integer quantity;
	private LocalDateTime expirationDate;
	private LocalDateTime reservationTs;
	private String reference;
	private LocalDateTime createTs;
	private LocalDateTime modifyTs;
	private String createUserId;
	private String modifyUserId;
	private String createProgId;
	private String modifyProgId;

	public Long getInventoryReservationKey() {
		return inventoryReservationKey;
	}

	public void setInventoryReservationKey(Long inventoryReservationKey) {
		this.inventoryReservationKey = inventoryReservationKey;
	}

	public Long getInventoryItemKey() {
		return inventoryItemKey;
	}

	public void setInventoryItemKey(Long inventoryItemKey) {
		this.inventoryItemKey = inventoryItemKey;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public Long getShipNodeKey() {
		return shipNodeKey;
	}

	public void setShipNodeKey(Long shipNodeKey) {
		this.shipNodeKey = shipNodeKey;
	}

	public Long getOwnerKey() {
		return ownerKey;
	}

	public void setOwnerKey(Long ownerKey) {
		this.ownerKey = ownerKey;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

	public LocalDateTime getReservationTs() {
		return reservationTs;
	}

	public void setReservationTs(LocalDateTime reservationTs) {
		this.reservationTs = reservationTs;
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