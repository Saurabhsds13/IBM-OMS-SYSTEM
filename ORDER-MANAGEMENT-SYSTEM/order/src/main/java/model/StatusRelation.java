package model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vsp_status_relation")
public class StatusRelation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long statusRelationKey;
	private String status;
	private String dropStatus;
	private String reference;
	private LocalDateTime createTs;
	private LocalDateTime modifyTs;
	private String createUserId;
	private String modifyUserId;
	private String createProgId;
	private String modifyProgId;

	public Long getStatusRelationKey() {
		return statusRelationKey;
	}

	public void setStatusRelationKey(Long statusRelationKey) {
		this.statusRelationKey = statusRelationKey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDropStatus() {
		return dropStatus;
	}

	public void setDropStatus(String dropStatus) {
		this.dropStatus = dropStatus;
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
