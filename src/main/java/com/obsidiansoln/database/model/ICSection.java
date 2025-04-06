/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.database.model;

public class ICSection {
	
    private Long sectionID;
    private int sectionNumber;
    private Long courseID;
    private Long roomID;
    private Long teacherPersonID;
    private String termName;
    private String period;
    private String periodDay;
    private String linkedCourseName;
    private String linkedCourseId;
    private String linkedCourseURL;
    private Integer teacherNumber;
    private Integer studentNumber;
    private String schoolName;
    
	public Long getSectionID() {
		return sectionID;
	}
	public void setSectionID(Long sectionID) {
		this.sectionID = sectionID;
	}
	
	public int getSectionNumber() {
		return sectionNumber;
	}
	public void setSectionNumber(int sectionNumber) {
		this.sectionNumber = sectionNumber;
	}
	public Long getCourseID() {
		return courseID;
	}
	public void setCourseID(Long courseID) {
		this.courseID = courseID;
	}
	public Long getRoomID() {
		return roomID;
	}
	public void setRoomID(Long roomID) {
		this.roomID = roomID;
	}
	public Long getTeacherPersonID() {
		return teacherPersonID;
	}
	public void setTeacherPersonID(Long teacherPersonID) {
		this.teacherPersonID = teacherPersonID;
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
	public String getPeriodDay() {
		return periodDay;
	}
	public void setPeriodDay(String periodDay) {
		this.periodDay = periodDay;
	}
	public String getLinkedCourseName() {
		return linkedCourseName;
	}
	public void setLinkedCourseName(String linkedCourseName) {
		this.linkedCourseName = linkedCourseName;
	}
	public String getLinkedCourseId() {
		return linkedCourseId;
	}
	public void setLinkedCourseId(String linkedCourseId) {
		this.linkedCourseId = linkedCourseId;
	}
	public String getLinkedCourseURL() {
		return linkedCourseURL;
	}
	public void setLinkedCourseURL(String linkedCourseURL) {
		this.linkedCourseURL = linkedCourseURL;
	}
	public Integer getTeacherNumber() {
		return teacherNumber;
	}
	public void setTeacherNumber(Integer teacherNumber) {
		this.teacherNumber = teacherNumber;
	}
	public Integer getStudentNumber() {
		return studentNumber;
	}
	public void setStudentNumber(Integer studentNumber) {
		this.studentNumber = studentNumber;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}


}
