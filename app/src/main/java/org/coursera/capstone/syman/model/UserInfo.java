package org.coursera.capstone.syman.model;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
	private String userName;
	private List<String> grants;
	
	public UserInfo() {
		grants = new ArrayList<String>();
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public List<String> getGrants() {
		return grants;
	}
	public void setGrants(List<String> grants) {
		this.grants = grants;
	}
}
