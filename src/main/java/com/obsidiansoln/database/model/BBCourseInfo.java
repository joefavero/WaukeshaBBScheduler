/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.database.model;

import java.util.List;

public class BBCourseInfo {
	String courseId;
	String targetCourseId;
	String courseName;
	List<String> studentEnrollments;
	List<String> instructorEnrollments;
	List<String> observerEnrollments;
	List<String> guestEnrollments;
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public String getTargetCourseId() {
		return targetCourseId;
	}
	public void setTargetCourseId(String targetCourseId) {
		this.targetCourseId = targetCourseId;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public List<String> getStudentEnrollments() {
		return studentEnrollments;
	}
	public void setStudentEnrollments(List<String> studentEnrollments) {
		this.studentEnrollments = studentEnrollments;
	}
	public List<String> getInstructorEnrollments() {
		return instructorEnrollments;
	}
	public void setInstructorEnrollments(List<String> instructorEnrollments) {
		this.instructorEnrollments = instructorEnrollments;
	}
	public List<String> getObserverEnrollments() {
		return observerEnrollments;
	}
	public void setObserverEnrollments(List<String> observerEnrollments) {
		this.observerEnrollments = observerEnrollments;
	}
	public List<String> getGuestEnrollments() {
		return guestEnrollments;
	}
	public void setGuestEnrollments(List<String> guestEnrollments) {
		this.guestEnrollments = guestEnrollments;
	}
	
	

}
