package com.captcha.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="login")
@Table(name="login")
public class LoginData {

    @Id
    @Column(name="id")
    private long id;
    
    @Column(name="email")
    private String email;
    @Column(name="password")
    private String password;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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


    

}
