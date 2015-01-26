package org.coursera.capstone.syman.model;

import java.io.Serializable;
import java.util.List;


public class Doctor implements Serializable {
	private long id;
	private String userid;
	private String firstName;
	private String middleName;
	private String lastName;
	private String hospital;
	private String phone;
	private String email;
	private String address;
	private String zip;
	private String state;
	private String country;
	private String picture;
	private String speciality;
	private List<Patient> patients;
	
	public long getId() {
		return id;
	}
	public void setId(long doctorId) {
		this.id = doctorId;
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
	public String getHospital() {
		return hospital;
	}
	public void setHospital(String hospital) {
		this.hospital = hospital;
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
	public String getSpeciality() {
		return speciality;
	}
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
	public List<Patient> getPatients() {
		return patients;
	}
	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}
	@Override
	public String toString() {
		String retval = "Doctor: ID: " + id + ", Name: " 
				+ firstName + " " + middleName + " " + lastName + ", Hospital: " + hospital;
		if (patients != null && patients.size() > 0) {
			for (Patient p : patients) {
				retval = retval + "\n  " + p.getFirstName() + " " + p.getMiddleName() + " " + p.getLastName();
			}
			
		}
		return retval;
	}
}
