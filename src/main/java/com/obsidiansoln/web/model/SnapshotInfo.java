package com.obsidiansoln.web.model;

public class SnapshotInfo {
	private String bbInstanceId;
	private String sharedStudentUsername;
	private String sharedStudentPassword;
	private String studentDatasource;
	private String sharedStaffUsername;
	private String sharedStaffPassword;
	private String staffDatasource;
	private String sharedGuardianUsername;
	private String sharedGuardianPassword;
	private String guardianDatasource;
	private String email;

	public SnapshotInfo() {

	}

	public String getBbInstanceId() {
		return bbInstanceId;
	}

	public void setBbInstanceId(String bbInstanceId) {
		this.bbInstanceId = bbInstanceId;
	}

	public String getSharedStudentUsername() {
		return sharedStudentUsername;
	}

	public void setSharedStudentUsername(String sharedStudentUsername) {
		this.sharedStudentUsername = sharedStudentUsername;
	}

	public String getSharedStudentPassword() {
		return sharedStudentPassword;
	}

	public void setSharedStudentPassword(String sharedStudentPassword) {
		this.sharedStudentPassword = sharedStudentPassword;
	}

	public String getStudentDatasource() {
		return studentDatasource;
	}

	public void setStudentDatasource(String studentDatasource) {
		this.studentDatasource = studentDatasource;
	}

	public String getSharedStaffUsername() {
		return sharedStaffUsername;
	}

	public void setSharedStaffUsername(String sharedStaffUsername) {
		this.sharedStaffUsername = sharedStaffUsername;
	}

	public String getSharedStaffPassword() {
		return sharedStaffPassword;
	}

	public void setSharedStaffPassword(String sharedStaffPassword) {
		this.sharedStaffPassword = sharedStaffPassword;
	}

	public String getStaffDatasource() {
		return staffDatasource;
	}

	public void setStaffDatasource(String staffDatasource) {
		this.staffDatasource = staffDatasource;
	}

	public String getSharedGuardianUsername() {
		return sharedGuardianUsername;
	}

	public void setSharedGuardianUsername(String sharedGuardianUsername) {
		this.sharedGuardianUsername = sharedGuardianUsername;
	}

	public String getSharedGuardianPassword() {
		return sharedGuardianPassword;
	}

	public void setSharedGuardianPassword(String sharedGuardianPassword) {
		this.sharedGuardianPassword = sharedGuardianPassword;
	}

	public String getGuardianDatasource() {
		return guardianDatasource;
	}

	public void setGuardianDatasource(String guardianDatasource) {
		this.guardianDatasource = guardianDatasource;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}