package com.stellantis.SUPREL.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "suprel_sis")
public class SuprelSISData {
	
	@Id
	@Column(name="SUPPLIER_MANUF_CODE")
	private String supplierCode;
	
	@Column(name="SUPPLIER_NAME")
	private String supplierName;
	
	@Column(name="SUPPLIER_ADDRESS")
	private String supplierAddress;
	
	@Column(name="COUNTRY")
	private String country;
	
	@Column(name="STATE")
	private String state;
	
	@Column(name="CITY")
	private String city;
	
	@Column(name="POSTAL_CODE")
	private String postalcode;
	
	@Column(name="PH_MOBILE")
	private String phoneMobile;
	
	@Column(name="PH_CELL")
	private String phoneCell;
	
	@Column(name="CONTACT_E_MAIL")
	private String ctEmail;

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

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		this.phoneMobile = phoneMobile;
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
	

	
}
