package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

@Entity
public class OrderHeader implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String orderHeaderId;
	private String orderNo;

	@JoinColumn(name = "vsp_organizationId")
	private String enterpriseKey;

	@JoinColumn(name = "vsp_customer_address")
	private String shipToKey;

	private String customerContactId;
	private String documentType;
	private String orderPurpose;
	private BigDecimal orderAmount;
	private String isDeliveryAmtApplied;
	private BigDecimal deliveryAmount;
	private BigDecimal originalTotalAmount;
	private String paymentType;
	private String currentOrderStatus;
	private LocalDateTime requestedReturnDate;
	private String isReturn;
	private String orderRefForReturn;
	private LocalDateTime reqCancelDate;
	private LocalDateTime reqShipDate;
	private LocalDateTime orderDate;
	private String isShipComplete;
	private LocalDateTime createdTimestamp;
	private LocalDateTime modifiedTimestamp;

	// Getters and Setters
	public String getOrderHeaderId() {
		return orderHeaderId;
	}

	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getEnterpriseKey() {
		return enterpriseKey;
	}

	public void setEnterpriseKey(String enterpriseKey) {
		this.enterpriseKey = enterpriseKey;
	}

	public String getShipToKey() {
		return shipToKey;
	}

	public void setShipToKey(String shipToKey) {
		this.shipToKey = shipToKey;
	}

	public String getCustomerContactId() {
		return customerContactId;
	}

	public void setCustomerContactId(String customerContactId) {
		this.customerContactId = customerContactId;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getOrderPurpose() {
		return orderPurpose;
	}

	public void setOrderPurpose(String orderPurpose) {
		this.orderPurpose = orderPurpose;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getIsDeliveryAmtApplied() {
		return isDeliveryAmtApplied;
	}

	public void setIsDeliveryAmtApplied(String isDeliveryAmtApplied) {
		this.isDeliveryAmtApplied = isDeliveryAmtApplied;
	}

	public BigDecimal getDeliveryAmount() {
		return deliveryAmount;
	}

	public void setDeliveryAmount(BigDecimal deliveryAmount) {
		this.deliveryAmount = deliveryAmount;
	}

	public BigDecimal getOriginalTotalAmount() {
		return originalTotalAmount;
	}

	public void setOriginalTotalAmount(BigDecimal originalTotalAmount) {
		this.originalTotalAmount = originalTotalAmount;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getCurrentOrderStatus() {
		return currentOrderStatus;
	}

	public void setCurrentOrderStatus(String currentOrderStatus) {
		this.currentOrderStatus = currentOrderStatus;
	}

	public LocalDateTime getRequestedReturnDate() {
		return requestedReturnDate;
	}

	public void setRequestedReturnDate(LocalDateTime requestedReturnDate) {
		this.requestedReturnDate = requestedReturnDate;
	}

	public String getIsReturn() {
		return isReturn;
	}

	public void setIsReturn(String isReturn) {
		this.isReturn = isReturn;
	}

	public String getOrderRefForReturn() {
		return orderRefForReturn;
	}

	public void setOrderRefForReturn(String orderRefForReturn) {
		this.orderRefForReturn = orderRefForReturn;
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

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public String getIsShipComplete() {
		return isShipComplete;
	}

	public void setIsShipComplete(String isShipComplete) {
		this.isShipComplete = isShipComplete;
	}

	public LocalDateTime getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public LocalDateTime getModifiedTimestamp() {
		return modifiedTimestamp;
	}

	public void setModifiedTimestamp(LocalDateTime modifiedTimestamp) {
		this.modifiedTimestamp = modifiedTimestamp;
	}

}