package com.stellantis.SUPREL.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="suprel_audit_log")
public class SuprelAuditLog {
	
	@Id
	@Column(name="id")
	private int id;
	
	@Column(name="SUPPLIER_MANUF_CODE")
	private String supplierManufCode;
	
	@Column(name="DATE_AND_TIME")
	private Timestamp dateAndTime;
	
	@Column(name="USER_TID")
	private String userTid;
	
	@Column(name="ACTIVITY")
	private String activity;
	
	@Column(name="PRIMARY_NON_CONFORMITY")
	private String primaryNonConformity;
	
	@Column(name="SECONDARY_NON_CONFORMITY")
	private String secondaryNonConformity;
	
	@Column(name="AMOUNT")
	private String amount;
	
	@Column(name="CURRENCY")
	private String currency;
	
	@Column(name="DETAILS")
	private String details;
	
	@Column(name="TECHNICAL_AREA")
	private String technicalArea;
	
	@Column(name="MANAGER_NAME")
	private String managerName;
	
	@Column(name="SUPREL_STATUS")
	private String suprelStatus;
	
	@Column(name="SUPREL_NO")
	private String suprelNo;
	
	@Column(name="MANAGER_COMMENTS")
	private String managerComments;
	
	@Column(name="SUPPLIER_COMMENTS")
	private String supplierComments;
	
	@Column(name="ORIGINATIOR_NAME")
	private String originatorName;
	
	@Column(name="ATTACHMENT_NAME")
	private String attachmentNames;
	
	@Column(name="EMAIL_1")
	private String supplierEmail1;
	
	@Column(name="EMAIL_2")
	private String supplierEmail2;
	
	@Column(name="EMAIL_3")
	private String supplierEmail3;
	
	@Column(name="EMAIL_4")
	private String supplierEmail4;
	
	@Column(name="EMAIL_5")
	private String supplierEmail5;
	
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
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSupplierManufCode() {
		return supplierManufCode;
	}

	public void setSupplierManufCode(String supplierManufCode) {
		this.supplierManufCode = supplierManufCode;
	}

	public Timestamp getDateAndTime() {
		return dateAndTime;
	}

	public void setDateAndTime(Timestamp dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

	public String getUserTid() {
		return userTid;
	}

	public void setUserTid(String userTid) {
		this.userTid = userTid;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
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

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
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

	public String getSuprelStatus() {
		return suprelStatus;
	}

	public void setSuprelStatus(String suprelStatus) {
		this.suprelStatus = suprelStatus;
	}

	public String getSuprelNo() {
		return suprelNo;
	}

	public void setSuprelNo(String suprelNo) {
		this.suprelNo = suprelNo;
	}

	public String getManagerComments() {
		return managerComments;
	}

	public void setManagerComments(String managerComments) {
		this.managerComments = managerComments;
	}

	public String getSupplierComments() {
		return supplierComments;
	}

	public void setSupplierComments(String supplierComments) {
		this.supplierComments = supplierComments;
	}

	public String getOriginatorName() {
		return originatorName;
	}

	public void setOriginatorName(String originatorName) {
		this.originatorName = originatorName;
	}

	public String getAttachmentNames() {
		return attachmentNames;
	}

	public void setAttachmentNames(String attachmentNames) {
		this.attachmentNames = attachmentNames;
	}

	public String getSupplierEmail1() {
		return supplierEmail1;
	}

	public void setSupplierEmail1(String supplierEmail1) {
		this.supplierEmail1 = supplierEmail1;
	}

	public String getSupplierEmail2() {
		return supplierEmail2;
	}

	public void setSupplierEmail2(String supplierEmail2) {
		this.supplierEmail2 = supplierEmail2;
	}

	public String getSupplierEmail3() {
		return supplierEmail3;
	}

	public void setSupplierEmail3(String supplierEmail3) {
		this.supplierEmail3 = supplierEmail3;
	}

	public String getSupplierEmail4() {
		return supplierEmail4;
	}

	public void setSupplierEmail4(String supplierEmail4) {
		this.supplierEmail4 = supplierEmail4;
	}

	public String getSupplierEmail5() {
		return supplierEmail5;
	}

	public void setSupplierEmail5(String supplierEmail5) {
		this.supplierEmail5 = supplierEmail5;
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
