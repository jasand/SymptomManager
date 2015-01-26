package org.coursera.capstone.syman.model;

import java.io.Serializable;
import java.util.Date;

public class PatientCompact implements Serializable, Comparable<PatientCompact> {
	private long id;
	private String userid;
	private String firstName;
	private String middleName;
	private String lastName;
	private Date dateOfBirth;
	private String currentState;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getCurrentState() {
		return currentState;
	}
	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}
	
	public String getFullName() {
		if (middleName != null && !middleName.equals("")) {
			return firstName + " " + middleName + " " + lastName;
		} else {
			return firstName + " " + lastName;
		}
	}
	
	@Override
	public int compareTo(PatientCompact another) {
		if (this.currentState.equalsIgnoreCase(another.getCurrentState())) {
			return 0;
		} else if (this.currentState.equalsIgnoreCase("RED")) {
			return -1;
		} else if (another.getCurrentState().equalsIgnoreCase("RED")) {
			return 1;
		} else if (this.currentState.equalsIgnoreCase("ORANGE")) {
			return-1;
		} else if (another.getCurrentState().equalsIgnoreCase("ORANGE")) {
			return 1;
		} else if (this.currentState.equalsIgnoreCase("YELLOW")) {
			return-1;
		} else if (another.getCurrentState().equalsIgnoreCase("YELLOW")) {
			return 1;
		} else if (this.currentState.equalsIgnoreCase("GREEN")) {
			return-1;
		} else {
			return 1;
		}
	}
}
