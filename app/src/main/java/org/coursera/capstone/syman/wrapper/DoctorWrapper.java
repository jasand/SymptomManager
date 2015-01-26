package org.coursera.capstone.syman.wrapper;

import org.coursera.capstone.syman.model.Doctor;

import android.net.Uri;

public class DoctorWrapper {
	private Uri photoFileUri;
	private Doctor doctor;
	
	public Uri getPhotoFileUri() {
		return photoFileUri;
	}
	public void setPhotoFileUri(Uri photoFileUri) {
		this.photoFileUri = photoFileUri;
	}
	public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
}
