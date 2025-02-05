package com.obsidiansoln.web.model;

public class SnapshotInfo {
	private String bbInstanceId;
	private String sharedUsername;
	private String sharedPassword;
	private String email;

	public SnapshotInfo() {

	}

	public String getBbInstanceId() {
		return bbInstanceId;
	}

	public void setBbInstanceId(String bbInstanceId) {
		this.bbInstanceId = bbInstanceId;
	}

	public String getSharedUsername() {
		return sharedUsername;
	}

	public void setSharedUsername(String sharedUsername) {
		this.sharedUsername = sharedUsername;
	}

	public String getSharedPassword() {
		return sharedPassword;
	}

	public void setSharedPassword(String sharedPassword) {
		this.sharedPassword = sharedPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}