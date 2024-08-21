package com.stellantis.SUPREL.model;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "suprel_master")
public class SuprelMaster {

	@Id
	@Column(name="SUPREL_NO")
	private String suprelNo;
	
	@Column(name="SUPPLIER_CODE")
	private String supplierCode;
	
	@Column(name="SUPPLIER_NAME")
	private String supplierName;
	
	@Column(name="SUPPLIER_ADDRESS")
	private String supplierAddress;
	
	@Column(name="CITY")
	private String city;
	
	@Column(name="STATE")
	private String state;
	
	@Column(name="POSTAL_CODE")
	private String postalCode;
	
	@Column(name="COUNTRY")
	private String country;
	
	@Column(name="SUPREL_STATUS")
	private String suprelStatus;
	
	@Column(name="AMOUNT")
	private String amount;
	
	@Column(name="TECHNICAL_AREA")
	private String technicalArea;
	
	@Column(name="MANAGER_NAME")
	private String managerName;
	
	@Column(name="CURRENCY_TYPE")
	private String currencyType;
	
	@Column(name="DETAILS")
	private String details;
	
	@Column(name="EMAIL_1")
	private String email1;
	
	@Column(name="EMAIL_2")
	private String email2;
	
	@Column(name="EMAIL_3")
	private String email3;
	
	@Column(name="EMAIL_4")
	private String email4;
	
	@Column(name="EMAIL_5")
	private String email5;
	
	@Column(name="PRIMARY_NON_CONFORMITY")
	private String primaryNonConformity;
	
	@Column(name="SECONDARY_NON_CONFORMITY")
	private String secondaryNonConformity;
	
	@Column(name="SUPPLIER_COMMENTS")
	private String supplierComments;
	
	@Column(name="ISSUED_DATE")
	private Date issuedDate;
	
	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="CREATED_ON")
	private Timestamp createdOn;
	
	@Column(name="MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name="MODIFIED_ON")
	private Timestamp modifiedOn;
	
	@Column(name="ORIGINATOR_E_MAIL")
	private String originatorEmail;
	
	@Column(name="ORIGINATIOR_NAME")
	private String originatorName;
	
	@Column(name="MANAGER_COMMENTS")
	private String managerComments;
	
	@Column(name="ATTACHMENT_NAME")
	private String attachmentNames;
	
	@Column(name="STELLANTIS_USEREMAIL1")
	private String userEmail1;
	
	@Column(name="STELLANTIS_USEREMAIL2")
	private String userEmail2;
	
	@Column(name="STELLANTIS_USEREMAIL3")
	private String userEmail3;
	
	@Column(name="STELLANTIS_USEREMAIL4")
	private String userEmail4;
	
	@Column(name="STELLANTIS_USEREMAIL5")
	private String userEmail5;
	
	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierAddress() {
		return supplierAddress;
	}

	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

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

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getEmail3() {
		return email3;
	}

	public void setEmail3(String email3) {
		this.email3 = email3;
	}

	public String getEmail4() {
		return email4;
	}

	public void setEmail4(String email4) {
		this.email4 = email4;
	}

	public String getEmail5() {
		return email5;
	}

	public void setEmail5(String email5) {
		this.email5 = email5;
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

	public Date getIssuedDate() {
		return issuedDate;
	}

	public void setIssuedDate(Date issuedDate) {
		this.issuedDate = issuedDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	

	public String getSupplierComments() {
		return supplierComments;
	}

	public void setSupplierComments(String supplierComments) {
		this.supplierComments = supplierComments;
	}

	public String getTechnicalArea() {
		return technicalArea;
	}

	public void setTechnicalArea(String technicalArea) {
		this.technicalArea = technicalArea;
	}
	

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getOriginatorEmail() {
		return originatorEmail;
	}

	public void setOriginatorEmail(String originatorEmail) {
		this.originatorEmail = originatorEmail;
	}

	public String getOriginatorName() {
		return originatorName;
	}

	public void setOriginatorName(String originatorName) {
		this.originatorName = originatorName;
	}

	public String getManagerComments() {
		return managerComments;
	}

	public void setManagerComments(String managerComments) {
		this.managerComments = managerComments;
	}

	public String getAttachmentNames() {
		return attachmentNames;
	}

	public void setAttachmentNames(String attachmentNames) {
		this.attachmentNames = attachmentNames;
	}

	public String getUserEmail1() {
		return userEmail1;
	}

	public void setUserEmail1(String userEmail1) {
		this.userEmail1 = userEmail1;
	}

	public String getUserEmail2() {
		return userEmail2;
	}

	public void setUserEmail2(String userEmail2) {
		this.userEmail2 = userEmail2;
	}

	public String getUserEmail3() {
		return userEmail3;
	}

	public void setUserEmail3(String userEmail3) {
		this.userEmail3 = userEmail3;
	}

	public String getUserEmail4() {
		return userEmail4;
	}

	public void setUserEmail4(String userEmail4) {
		this.userEmail4 = userEmail4;
	}

	public String getUserEmail5() {
		return userEmail5;
	}

	public void setUserEmail5(String userEmail5) {
		this.userEmail5 = userEmail5;
	}
	
	
}
