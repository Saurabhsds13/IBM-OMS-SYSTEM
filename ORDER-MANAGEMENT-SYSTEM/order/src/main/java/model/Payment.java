package model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vsp_payment_details")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int paymentID;
	private int orderHeaderKey;
	private String paymentType;
	private int customerKey;
	private String creditCardType;
	private String creditCardName;
	private String creditCardNo;
	private long displayCreditCardNo;
	private LocalDateTime creditCardExpiry;
	private int svcNo;
	private String debitCardNo;
	private String displayDebitCardNo;
	private String paymentReference1;
	private String paymentReference2;
	private Double totalAuthorized;
	private Double totalCharged;
	private Double requestedRefundAmount;
	private Double totalRefundedAmount;
	private Double totalAltRefundedAmount;
	private LocalDateTime createTs;
	private LocalDateTime modifyTs;
	private String createUserId;
	private String modifyUserId;
	private String createProgId;
	private String modifyProgId;

	public int getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(int paymentID) {
		this.paymentID = paymentID;
	}

	public int getOrderHeaderKey() {
		return orderHeaderKey;
	}

	public void setOrderHeaderKey(int orderHeaderKey) {
		this.orderHeaderKey = orderHeaderKey;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public int getCustomerKey() {
		return customerKey;
	}

	public void setCustomerKey(int customerKey) {
		this.customerKey = customerKey;
	}

	public String getCreditCardType() {
		return creditCardType;
	}

	public void setCreditCardType(String creditCardType) {
		this.creditCardType = creditCardType;
	}

	public String getCreditCardName() {
		return creditCardName;
	}

	public void setCreditCardName(String creditCardName) {
		this.creditCardName = creditCardName;
	}

	public String getCreditCardNo() {
		return creditCardNo;
	}

	public void setCreditCardNo(String creditCardNo) {
		this.creditCardNo = creditCardNo;
	}

	public long getDisplayCreditCardNo() {
		return displayCreditCardNo;
	}

	public void setDisplayCreditCardNo(long displayCreditCardNo) {
		this.displayCreditCardNo = displayCreditCardNo;
	}

	public LocalDateTime getCreditCardExpiry() {
		return creditCardExpiry;
	}

	public void setCreditCardExpiry(LocalDateTime creditCardExpiry) {
		this.creditCardExpiry = creditCardExpiry;
	}

	public int getSvcNo() {
		return svcNo;
	}

	public void setSvcNo(int svcNo) {
		this.svcNo = svcNo;
	}

	public String getDebitCardNo() {
		return debitCardNo;
	}

	public void setDebitCardNo(String debitCardNo) {
		this.debitCardNo = debitCardNo;
	}

	public String getDisplayDebitCardNo() {
		return displayDebitCardNo;
	}

	public void setDisplayDebitCardNo(String displayDebitCardNo) {
		this.displayDebitCardNo = displayDebitCardNo;
	}

	public String getPaymentReference1() {
		return paymentReference1;
	}

	public void setPaymentReference1(String paymentReference1) {
		this.paymentReference1 = paymentReference1;
	}

	public String getPaymentReference2() {
		return paymentReference2;
	}

	public void setPaymentReference2(String paymentReference2) {
		this.paymentReference2 = paymentReference2;
	}

	public Double getTotalAuthorized() {
		return totalAuthorized;
	}

	public void setTotalAuthorized(Double totalAuthorized) {
		this.totalAuthorized = totalAuthorized;
	}

	public Double getTotalCharged() {
		return totalCharged;
	}

	public void setTotalCharged(Double totalCharged) {
		this.totalCharged = totalCharged;
	}

	public Double getRequestedRefundAmount() {
		return requestedRefundAmount;
	}

	public void setRequestedRefundAmount(Double requestedRefundAmount) {
		this.requestedRefundAmount = requestedRefundAmount;
	}

	public Double getTotalRefundedAmount() {
		return totalRefundedAmount;
	}

	public void setTotalRefundedAmount(Double totalRefundedAmount) {
		this.totalRefundedAmount = totalRefundedAmount;
	}

	public Double getTotalAltRefundedAmount() {
		return totalAltRefundedAmount;
	}

	public void setTotalAltRefundedAmount(Double totalAltRefundedAmount) {
		this.totalAltRefundedAmount = totalAltRefundedAmount;
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