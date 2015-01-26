package org.coursera.capstone.syman.model;

import java.io.Serializable;


public class Prescription implements Serializable {
	private long id;
	private Medication medicine;
	private String usage;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Medication getMedicine() {
		return medicine;
	}

	public void setMedicine(Medication medicine) {
		this.medicine = medicine;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

//	id					bigserial	not null,
//	medicine_id			bigint not null,
//	usage				text	not null
}
