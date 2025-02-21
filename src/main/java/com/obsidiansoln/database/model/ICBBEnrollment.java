package com.obsidiansoln.database.model;

public class ICBBEnrollment {
	
	private String bbCourseId;
	private String courseId;
	private String sectionId;
	private String personId;
	private String studentNumber;
	private String userName;
	private String role;
	private String rowStatus;
	private String availableInd;
	
	public String getBbCourseId() {
		return bbCourseId;
	}
	public void setBbCourseId(String bbCourseId) {
		this.bbCourseId = bbCourseId;
	}
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRole() {
		return "Student";
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getRowStatus() {
		return "Enabled";
	}
	public void setRowStatus(String rowStatus) {
		this.rowStatus = rowStatus;
	}
	public String getAvailableInd() {
		return "Y";
	}
	public void setAvailableInd(String availableInd) {
		this.availableInd = availableInd;
	}
	public String getStudentNumber() {
		return studentNumber;
	}
	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}

}
