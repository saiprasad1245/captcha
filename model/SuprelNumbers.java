package com.stellantis.SUPREL.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "suprel_numbers")
public class SuprelNumbers {

	@Id
	@Column(name="SUPREL_NO")
	private String suprelNo;
	
	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="CREATED_ON")
	private Timestamp createdOn;

	public String getSuprelNo() {
		return suprelNo;
	}

	public void setSuprelNo(String suprelNo) {
		this.suprelNo = suprelNo;
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

		
	
	
}
