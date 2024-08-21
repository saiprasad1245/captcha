package com.stellantis.SUPREL.dto;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class SearchSuprelMaster {

	private String supplierCode;
	
	private String supplierName;

	private String supplierAddress;
	
	private String incidentOriginator;
	
	private String city;
	
	private String state;
	
	private String country;
	
	private String suprelNo;
	
	private String primaryNonConformity;
	
	private String secondaryNonConformity;
	
	private String suprelStatus;
	
	private Date issueFromDate;
	
	private Date issueToDate;
	
	private Date issuedDate;
	
	private String role;
	
	private String userId;
	
	private String managerName;
	
	private String technicalArea;
	
	private String year;


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


	public Date getIssueFromDate() {
		return issueFromDate;
	}


	public void setIssueFromDate(Date issueFromDate) {
		this.issueFromDate = issueFromDate;
	}


	public Date getIssueToDate() {
		return issueToDate;
	}


	public void setIssueToDate(Date issueToDate) {
		this.issueToDate = issueToDate;
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


	public String getIncidentOriginator() {
		return incidentOriginator;
	}


	public void setIncidentOriginator(String incidentOriginator) {
		this.incidentOriginator = incidentOriginator;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
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


	public String getYear() {
		return year;
	}


	public void setYear(String year) {
		this.year = year;
	}
	
	
	
}
