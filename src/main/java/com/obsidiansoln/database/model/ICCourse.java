package com.obsidiansoln.database.model;

import java.util.List;

public class ICCourse {
    private Long courseID;
    private Long calendarID;
    private String courseName;
    private String courseNumber;
    private String schoolName;
    private String endYear;
    private String teacherName;
    private List<String> linkedCourses;
	public Long getCourseID() {
		return courseID;
	}
	public void setCourseID(Long courseID) {
		this.courseID = courseID;
	}
	public String getCourseName() {
		return courseName;
	}
	public Long getCalendarID() {
		return calendarID;
	}
	public void setCalendarID(Long calendarID) {
		this.calendarID = calendarID;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getEndYear() {
		return endYear;
	}
	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public String getCourseNumber() {
		return courseNumber;
	}
	public void setCourseNumber(String courseNumber) {
		this.courseNumber = courseNumber;
	}
	public List<String> getLinkedCourses() {
		return linkedCourses;
	}
	public void setLinkedCourses(List<String> linkedCourses) {
		this.linkedCourses = linkedCourses;
	}
	
}
