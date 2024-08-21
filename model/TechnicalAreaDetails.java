package com.stellantis.SUPREL.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Technical_Area_Details")
public class TechnicalAreaDetails {
	
	@Id
	@Column(name="ID")
	private String id; 
	
	@Column(name="TECHNICAL_AREA")
	private String technicalArea;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTechnicalArea() {
		return technicalArea;
	}

	public void setTechnicalArea(String technicalArea) {
		this.technicalArea = technicalArea;
	} 
	
	

}
