package com.stellantis.SUPREL.dto;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

@Component
public class StatusChangeDetails {

	private String suprelNo;
	
	
	private String suprelStatus;
	

	private String comments;
	

	private String managerName;
	
	
	private String supplierId;
	
	
	private Timestamp modifiedOn;
	
	
	private String modifiedBy;
	
	
	private String role;
	
	private String primaryNonConformity;
	
	
	private String secondaryNonConformity;
	
	
	private String technicalArea;
	
	
	private String details;
	
	private String amount;
	
	
	private String currencyType;


	public String getSuprelNo() {
		return suprelNo;
	}


	public void setSuprelNo(String suprelNo) {
		this.suprelNo = suprelNo;
	}


	public String getSuprelStatus() {
		return suprelStatus;
	}


	public void setSuprelStatus(String suprelStatus) {
		this.suprelStatus = suprelStatus;
	}


	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}


	public String getManagerName() {
		return managerName;
	}


	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}


	public String getSupplierId() {
		return supplierId;
	}


	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}


	public Timestamp getModifiedOn() {
		return modifiedOn;
	}


	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}


	public String getModifiedBy() {
		return modifiedBy;
	}


	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getPrimaryNonConformity() {
		return primaryNonConformity;
	}


	public void setPrimaryNonConformity(String primaryNonConformity) {
		this.primaryNonConformity = primaryNonConformity;
	}


	public String getSecondaryNonConformity() {
		return secondaryNonConformity;
	}


	public void setSecondaryNonConformity(String secondaryNonConformity) {
		this.secondaryNonConformity = secondaryNonConformity;
	}


	public String getTechnicalArea() {
		return technicalArea;
	}


	public void setTechnicalArea(String technicalArea) {
		this.technicalArea = technicalArea;
	}


	public String getDetails() {
		return details;
	}


	public void setDetails(String details) {
		this.details = details;
	}


	public String getAmount() {
		return amount;
	}


	public void setAmount(String amount) {
		this.amount = amount;
	}


	public String getCurrencyType() {
		return currencyType;
	}


	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}


	
}
