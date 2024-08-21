package com.stellantis.SUPREL.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Non_Conformity_Details")
public class NonConformityDetails {
	
	
	@Id
	@Column(name="ID")
	private String id; 
	
	@Column(name="primary_conformity")
	private String primaryConformity;
	
	@Column(name="secondary_conformity")
	private String secondaryConformity;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrimaryConformity() {
		return primaryConformity;
	}

	public void setPrimaryConformity(String primaryConformity) {
		this.primaryConformity = primaryConformity;
	}

	public String getSecondaryConformity() {
		return secondaryConformity;
	}

	public void setSecondaryConformity(String secondaryConformity) {
		this.secondaryConformity = secondaryConformity;
	}
	
	
	

}
