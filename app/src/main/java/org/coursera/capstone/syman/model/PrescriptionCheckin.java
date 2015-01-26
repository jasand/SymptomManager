package org.coursera.capstone.syman.model;

import java.util.Date;

public class PrescriptionCheckin {
	private long id;
	private long checkinId;
	private Date timeTaken;
	private Medication medicine;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCheckinId() {
		return checkinId;
	}

	public void setCheckinId(long checkinId) {
		this.checkinId = checkinId;
	}

	public Date getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(Date timeTaken) {
		this.timeTaken = timeTaken;
	}

	public Medication getMedicine() {
		return medicine;
	}

	public void setMedicine(Medication medicine) {
		this.medicine = medicine;
	}

}
