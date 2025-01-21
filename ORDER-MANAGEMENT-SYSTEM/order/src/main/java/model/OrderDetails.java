package model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vsp_order_line")
public class OrderDetails {

	@Id
	private Long orderLineId;
	private Long orderHeaderKey;
	private Integer primeLineNo;
	private String itemId;
	private Double itemPrice;
	private String uom;
	private String productClass;
	private Double unitPrice;
	private Integer orderedQty;
	private Long shipNodeKey;
	private LocalDateTime reqCancelDate;
	private LocalDateTime reqShipDate;
	private String carrierServiceCode;
	private Double lineTotal;
	private Boolean isReturn;
	private String reference;
	private LocalDateTime createTs;
	private LocalDateTime modifyTs;
	private String createUserId;
	private String modifyUserId;
	private String createProgId;
	private String modifyProgId;

	public Long getOrderLineId() {
		return orderLineId;
	}

	public void setOrderLineId(Long orderLineId) {
		this.orderLineId = orderLineId;
	}

	public Long getOrderHeaderKey() {
		return orderHeaderKey;
	}

	public void setOrderHeaderKey(Long orderHeaderKey) {
		this.orderHeaderKey = orderHeaderKey;
	}

	public Integer getPrimeLineNo() {
		return primeLineNo;
	}

	public void setPrimeLineNo(Integer primeLineNo) {
		this.primeLineNo = primeLineNo;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Double getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(Double itemPrice) {
		this.itemPrice = itemPrice;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getProductClass() {
		return productClass;
	}

	public void setProductClass(String productClass) {
		this.productClass = productClass;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getOrderedQty() {
		return orderedQty;
	}

	public void setOrderedQty(Integer orderedQty) {
		this.orderedQty = orderedQty;
	}

	public Long getShipNodeKey() {
		return shipNodeKey;
	}

	public void setShipNodeKey(Long shipNodeKey) {
		this.shipNodeKey = shipNodeKey;
	}

	public LocalDateTime getReqCancelDate() {
		return reqCancelDate;
	}

	public void setReqCancelDate(LocalDateTime reqCancelDate) {
		this.reqCancelDate = reqCancelDate;
	}

	public LocalDateTime getReqShipDate() {
		return reqShipDate;
	}

	public void setReqShipDate(LocalDateTime reqShipDate) {
		this.reqShipDate = reqShipDate;
	}

	public String getCarrierServiceCode() {
		return carrierServiceCode;
	}

	public void setCarrierServiceCode(String carrierServiceCode) {
		this.carrierServiceCode = carrierServiceCode;
	}

	public Double getLineTotal() {
		return lineTotal;
	}

	public void setLineTotal(Double lineTotal) {
		this.lineTotal = lineTotal;
	}

	public Boolean getIsReturn() {
		return isReturn;
	}

	public void setIsReturn(Boolean isReturn) {
		this.isReturn = isReturn;
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