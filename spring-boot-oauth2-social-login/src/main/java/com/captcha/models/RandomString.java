package com.captcha.models;

import java.util.Date;

import javax.persistence.*;

@Entity(name="randoms")
@Table(name="randoms")
public class RandomString {

    @Id
    @Column(name="id")
    private long id;

    @Column(name="randomString")
    private String randomString;
    
    @Column(name="time")
    private int time;
    
    @Column(name="createdTime")
    private Date createdTime;
    
    public RandomString() { }

    public RandomString(String randomString) {
        this.randomString = randomString;
    }

    public void setId(long value) {
        this.id = value;
    }

    public String getRandomString() {
        return randomString;
    }

    public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

//	public Date getCurrentTime() {
//		return currentTime;
//	}
//
//	public void setCurrentTime(Date currentTime) {
//		this.currentTime = currentTime;
//	}

	public long getId() {
		return id;
	}

	public void setRandomString(String randomString) { this.randomString = randomString; }
}
