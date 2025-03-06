package com.obsidiansoln.database.model;

public class ICBBSection {
	private String sectionId;
	private String courseName;
	private String sectionNumber;
	private String teacherName;
	private Long studentNumber;
	private Long teacherNumber;
	
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
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
