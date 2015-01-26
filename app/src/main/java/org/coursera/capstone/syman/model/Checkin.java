package org.coursera.capstone.syman.model;

import java.util.Date;
import java.util.List;

public class Checkin {
	private long id;
	private Patient patient;
	private Date timestamp;
	private String painStatus;
	private String foodStatus;
	private List<PrescriptionCheckin> prescriptionCheckins;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getPainStatus() {
		return painStatus;
	}

	public void setPainStatus(String painStatus) {
		this.painStatus = painStatus;
	}

	public String getFoodStatus() {
		return foodStatus;
	}

	public void setFoodStatus(String foodStatus) {
		this.foodStatus = foodStatus;
	}
	
	public List<PrescriptionCheckin> getPrescriptionCheckins() {
		return prescriptionCheckins;
	}

	public void setPrescriptionCheckins(
			List<PrescriptionCheckin> prescriptionCheckins) {
		this.prescriptionCheckins = prescriptionCheckins;
	}

	@Override
	public String toString() {
		return "Checkin: ID: " + id + ", patient: XXX" + ", timestamp: "
				+ timestamp + ", Pain: " + painStatus + " Food: " + foodStatus;
	}
}
