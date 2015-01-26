package org.coursera.capstone.syman.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Patient implements Serializable {
	private long id;
	private String userid;
	private String firstName;
	private String middleName;
	private String lastName;
	private Date dateOfBirth;
	private String diagnosis;
	private String phone;
	private String email;
	private String address;
	private String zip;
	private String state;
	private String country;
	private String picture;
	private String currentState;
	private List<Doctor> doctors;
	private List<Checkin> checkins;
	private List<Prescription> prescriptions;
	
	public long getId() {
		return id;
	}

	public void setId(long patientId) {
		this.id = patientId;
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

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnose) {
		this.diagnosis = diagnose;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getCurrentState() {
		return currentState;
	}

	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}

	public List<Doctor> getDoctors() {
		return doctors;
	}

	public void setDoctors(List<Doctor> doctors) {
		this.doctors = doctors;
	}

	public List<Checkin> getCheckins() {
		return checkins;
	}

	public void setCheckins(List<Checkin> checkins) {
		this.checkins = checkins;
	}

	public List<Prescription> getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(List<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}

	@Override
	public String toString() {
		String retval = "Patient: ID: " + id + ", Name: " + 
				firstName + " " + middleName + " " + lastName + ", Diagnosed: " + diagnosis;
		if (doctors != null) {
			for (Doctor d : doctors) {
				retval = retval + "\n  Doctor: " + d.getFirstName() + " " + d.getMiddleName() + " " + d.getLastName();
			}
		}
		return retval;
	}
}
