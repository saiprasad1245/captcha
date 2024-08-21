package com.stellantis.SUPREL.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "suprel_supplier_map")
public class SuprelSupplierMap {
	
	@Id
	@Column(name="SUPPLIER_ID")
	private String supplierId; //user_id
	
	@Column(name="SUPPLIER_MANUF_CODE")
	private String supplierCode; //role

	@Column(name="CONTACT_E_MAIL")
	private String contactEmail; //role
	
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

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	}
