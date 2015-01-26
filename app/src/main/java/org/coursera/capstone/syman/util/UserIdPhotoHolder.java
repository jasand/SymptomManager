package org.coursera.capstone.syman.util;

import android.net.Uri;

public class UserIdPhotoHolder {
	private String userId;
	private Uri photoUri;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Uri getPhotoUri() {
		return photoUri;
	}
	public void setPhotoUri(Uri photoUri) {
		this.photoUri = photoUri;
	}
}
