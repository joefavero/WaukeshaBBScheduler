package com.obsidiansoln.database.model;

public class ICBBSection {
	private int sectionID;
	private String courseName;
	private String sectionNumber;
	private String termName;
	private String period;
	private String teacherName;
	private Long studentNumber;
	private Long teacherNumber;
	
	public int getSectionID() {
		return sectionID;
	}
	public void setSectionID(int sectionID) {
		this.sectionID = sectionID;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getSectionNumber() {
		return sectionNumber;
	}
	public void setSectionNumber(String sectionNumber) {
		this.sectionNumber = sectionNumber;
	}
	public String getTermName() {
		return termName;
	}
	public void setTermName(String termName) {
		this.termName = termName;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public Long getStudentNumber() {
		return studentNumber;
	}
	public void setStudentNumber(Long studentNumber) {
		this.studentNumber = studentNumber;
	}
	public Long getTeacherNumber() {
		return teacherNumber;
	}
	public void setTeacherNumber(Long teacherNumber) {
		this.teacherNumber = teacherNumber;
	}
	
	

}
