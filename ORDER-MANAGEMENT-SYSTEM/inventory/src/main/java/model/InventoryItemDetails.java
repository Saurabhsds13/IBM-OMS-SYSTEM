package model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vsp_item_details")
public class InventoryItemDetails {

	@Id
	private String itemId;
	private Long fkInventoryItem;
	private Long fkOrganization;
	private String shortDescription;
	private String description;
	private Double unitCost;
	private String manufacturerName;
	private String manufacturerItemDesc;
	private String countryOfOrigin;
	private Boolean isReturnable;
	private String organizationCode;
	private String itemType;
	private String uom;
	private String status;
	private Double unitWeight;
	private String unitWeightUom;
	private Double unitHeight;
	private String unitHeightUom;
	private Double unitLength;
	private String unitLengthUom;
	private Double unitWidth;
	private String unitWidthUom;
	private Integer minOrderQuantity;
	private Integer maxOrderQuantity;
	private String storageType;
	private Integer onhandSafetyFactorQty;
	private Boolean isHazmat;
	private Boolean isFreezerRequired;
	private Long imageId;
	private String reference;
	private LocalDateTime createTs;
	private LocalDateTime modifyTs;
	private String createUserId;
	private String modifyUserId;
	private String createProgId;
	private String modifyProgId;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Long getFkInventoryItem() {
		return fkInventoryItem;
	}

	public void setFkInventoryItem(Long fkInventoryItem) {
		this.fkInventoryItem = fkInventoryItem;
	}

	public Long getFkOrganization() {
		return fkOrganization;
	}

	public void setFkOrganization(Long fkOrganization) {
		this.fkOrganization = fkOrganization;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getManufacturerItemDesc() {
		return manufacturerItemDesc;
	}

	public void setManufacturerItemDesc(String manufacturerItemDesc) {
		this.manufacturerItemDesc = manufacturerItemDesc;
	}

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	public Boolean getIsReturnable() {
		return isReturnable;
	}

	public void setIsReturnable(Boolean isReturnable) {
		this.isReturnable = isReturnable;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getUnitWeight() {
		return unitWeight;
	}

	public void setUnitWeight(Double unitWeight) {
		this.unitWeight = unitWeight;
	}

	public String getUnitWeightUom() {
		return unitWeightUom;
	}

	public void setUnitWeightUom(String unitWeightUom) {
		this.unitWeightUom = unitWeightUom;
	}

	public Double getUnitHeight() {
		return unitHeight;
	}

	public void setUnitHeight(Double unitHeight) {
		this.unitHeight = unitHeight;
	}

	public String getUnitHeightUom() {
		return unitHeightUom;
	}

	public void setUnitHeightUom(String unitHeightUom) {
		this.unitHeightUom = unitHeightUom;
	}

	public Double getUnitLength() {
		return unitLength;
	}

	public void setUnitLength(Double unitLength) {
		this.unitLength = unitLength;
	}

	public String getUnitLengthUom() {
		return unitLengthUom;
	}

	public void setUnitLengthUom(String unitLengthUom) {
		this.unitLengthUom = unitLengthUom;
	}

	public Double getUnitWidth() {
		return unitWidth;
	}

	public void setUnitWidth(Double unitWidth) {
		this.unitWidth = unitWidth;
	}

	public String getUnitWidthUom() {
		return unitWidthUom;
	}

	public void setUnitWidthUom(String unitWidthUom) {
		this.unitWidthUom = unitWidthUom;
	}

	public Integer getMinOrderQuantity() {
		return minOrderQuantity;
	}

	public void setMinOrderQuantity(Integer minOrderQuantity) {
		this.minOrderQuantity = minOrderQuantity;
	}

	public Integer getMaxOrderQuantity() {
		return maxOrderQuantity;
	}

	public void setMaxOrderQuantity(Integer maxOrderQuantity) {
		this.maxOrderQuantity = maxOrderQuantity;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

	public Integer getOnhandSafetyFactorQty() {
		return onhandSafetyFactorQty;
	}

	public void setOnhandSafetyFactorQty(Integer onhandSafetyFactorQty) {
		this.onhandSafetyFactorQty = onhandSafetyFactorQty;
	}

	public Boolean getIsHazmat() {
		return isHazmat;
	}

	public void setIsHazmat(Boolean isHazmat) {
		this.isHazmat = isHazmat;
	}

	public Boolean getIsFreezerRequired() {
		return isFreezerRequired;
	}

	public void setIsFreezerRequired(Boolean isFreezerRequired) {
		this.isFreezerRequired = isFreezerRequired;
	}

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
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
