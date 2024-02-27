package com.captcha.models;

import java.util.Date;

import javax.persistence.*;

@Entity(name="registration")
@Table(name="registration")
public class Registration {

    @Id
    @Column(name="id")
    private long id;

    @Column(name="name")
    private String name;
    
    @Column(name="phone")
    private String phone;
    
    
    @Column(name="email")
    private String email;
    @Column(name="password")
    private String password;
    @Column(name="confirmpassword")
    private String confirmpassword;
    
    @Column(name="createdTime")
    private Date createdTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmpassword() {
		return confirmpassword;
	}

	public void setConfirmpassword(String confirmpassword) {
		this.confirmpassword = confirmpassword;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
    

}
