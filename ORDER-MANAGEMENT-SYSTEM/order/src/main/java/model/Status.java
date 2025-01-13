package model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vsp_status_details")
public class Status {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long statusKey;
	private String status;
	private String description;
	private String statusName;
	private String statusType;
	private String reference;
	private LocalDateTime createTs;
	private LocalDateTime modifyTs;
	private String createUserId;
	private String modifyUserId;
	private String createProgId;
	private String modifyProgId;

	public Long getStatusKey() {
		return statusKey;
	}

	public void setStatusKey(Long statusKey) {
		this.statusKey = statusKey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
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
