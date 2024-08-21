package com.stellantis.SUPREL.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="suprel_attachments")
public class SuprelAttachments {

	@Id
	@Column(name="SUPREL_FILENAME")
	private String suprelFileName;
	
	@Column(name="SUPREL_NO")
	private String suprelNo;
	
	@Column(name="SUPPLIER_CODE")
	private String supplierCode;
	
	@Column(name="ATTACHMENT_NAME")
	private String fileName;
	
	@Lob
	@Column(name="ATTACHMENT")
	private byte[] file;
	
	public String getSuprelNo() {
		return suprelNo;
	}

	public void setSuprelNo(String suprelNo) {
		this.suprelNo = suprelNo;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getSuprelFileName() {
		return suprelFileName;
	}

	public void setSuprelFileName(String suprelFileName) {
		this.suprelFileName = suprelFileName;
	}
	
	
	
}
