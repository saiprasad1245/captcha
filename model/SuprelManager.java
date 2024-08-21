package com.stellantis.SUPREL.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "suprel_managers")
public class SuprelManager {

	@Id
	@Column(name="TID")
	private String managerTid;
	
	@Column(name="MANAGER_NAME")
	private String managerName;
	
	@Column(name="MANAGER_EMAIL")
	private String managerEmail;

	public String getManagerTid() {
		return managerTid;
	}

	public void setManagerTid(String managerTid) {
		this.managerTid = managerTid;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getManagerEmail() {
		return managerEmail;
	}

	public void setManagerEmail(String managerEmail) {
		this.managerEmail = managerEmail;
	}

	
	
	
	
	
}
