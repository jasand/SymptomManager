package org.coursera.capstone.syman.model;

import java.util.Date;

public class GraphSamplePoint implements Comparable<GraphSamplePoint>{
	private Date sampleTime;
	private int painStatus;
	private int foodStatus;
	
	public Date getSampleTime() {
		return sampleTime;
	}
	public void setSampleTime(Date sampleTime) {
		this.sampleTime = sampleTime;
	}
	public int getPainStatus() {
		return painStatus;
	}
	public void setPainStatus(int painStatus) {
		this.painStatus = painStatus;
	}
	public int getFoodStatus() {
		return foodStatus;
	}
	public void setFoodStatus(int foodStatus) {
		this.foodStatus = foodStatus;
	}
	
	@Override
	public int compareTo(GraphSamplePoint another) {
		if (this.sampleTime.before(another.getSampleTime())) {
			return -1;
		} else if (this.sampleTime.after(another.getSampleTime())) {
			return 1;
		} else {
			return 0;
		}
	}
	
}
