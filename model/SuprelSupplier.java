package com.stellantis.SUPREL.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "suprel_supplier")
public class SuprelSupplier {

	@Id
	@Column(name="SUPPLIER_ID")
	private String supplierId;
	
	@Column(name="SUPPLIER_LOCATION_CODE")
	private String supplierCode;
	
	@Column(name="SUPPLIER_NAME")
	private String supplierName;
	
	@Column(name="SUPPLIER_ADDRESS1")
	private String supplierAddress1;
	
	@Column(name="SUPPLIER_ADDRESS2")
	private String supplierAddress2;
	
	@Column(name="CNTRY")
	private String country;
	
	@Column(name="STATE")
	private String state;
	
	@Column(name="CITY")
	private String city;
	
	@Column(name="POSTAL_CODE")
	private String postalcode;
	
	@Column(name="PH_OFFICE")
	private String office;
	
	@Column(name="PH_CELL")
	private String phoneCell;
	
	@Column(name="CONTACT_E_MAIL")
	private String ctEmail;

	
	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	public String getPhoneCell() {
		return phoneCell;
	}

	public void setPhoneCell(String phoneCell) {
		this.phoneCell = phoneCell;
	}

	public String getCtEmail() {
		return ctEmail;
	}

	public void setCtEmail(String ctEmail) {
		this.ctEmail = ctEmail;
	}

	public String getSupplierAddress1() {
		return supplierAddress1;
	}

	public void setSupplierAddress1(String supplierAddress1) {
		this.supplierAddress1 = supplierAddress1;
	}

	public String getSupplierAddress2() {
		return supplierAddress2;
	}

	public void setSupplierAddress2(String supplierAddress2) {
		this.supplierAddress2 = supplierAddress2;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}
	

	
}
