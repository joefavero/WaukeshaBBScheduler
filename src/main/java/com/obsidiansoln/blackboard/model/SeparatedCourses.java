/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.obsidiansoln.blackboard.algorithm.ProgressArea;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SeparatedCourses {
	private String id;
	private String firstName;
	private String lastName;
	private String userId;
	private String courseBatchId;
	private String courseId;
	private String studentSisId;
	private double programGrade;
	private List<ProgressArea> progressAreas;
	private List<NonProgramCourse> nonProgramCourses;


	public SeparatedCourses() {
		this.progressAreas = new ArrayList<ProgressArea>();
		this.nonProgramCourses = new ArrayList<NonProgramCourse>();
	}

	public SeparatedCourses(String firstName, String lastName, String userId) {
		this.progressAreas = new ArrayList<ProgressArea>();
		this.nonProgramCourses = new ArrayList<NonProgramCourse>();
		this.firstName = firstName;
		this.lastName = lastName;
		this.userId = userId;
	}

	public List<ProgressArea> getProgressAreas() {
		return progressAreas;
	}

	public void setProgressAreas(List<ProgressArea> progressAreas) {
		this.progressAreas = progressAreas;
	}

	public List<NonProgramCourse> getNonProgramCourses() {
		return nonProgramCourses;
	}

	public void setNonProgramCourses(List<NonProgramCourse> nonProgramCourses) {
		this.nonProgramCourses = nonProgramCourses;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCourseBatchId() {
		return courseBatchId;
	}

	public void setCourseBatchId(String courseBatchId) {
			this.courseBatchId = courseBatchId;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
			this.courseId = courseId;
	}

	public String getStudentSisId() {
		return studentSisId;
	}

	public void setStudentSisId(String studentSisId) {
			this.studentSisId = studentSisId;
	}

	public double getProgramGrade() {
		return programGrade;
	}

	public void setProgramGrade(double programGrade) {
		this.programGrade = programGrade;
	}

	
}
