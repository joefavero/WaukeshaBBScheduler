/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.obsidiansoln.blackboard.algorithm.Score;

public class GradeData implements Serializable {
	
	private static final long serialVersionUID = -8106382531858166505L;
	
	private List<Score> scores;
	private List<Score> missingScores;
	private String id;
	private String name;
	private String lastName;
	private String firstName;
	private String userId;
	private String courseBatchId;
	private String courseId;
	private String studentSisId;
	private String studentBatchId;

	public List<Score> getScores() {
		return scores;
	}

	public List<Score> getMissingScores() {
		return missingScores;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getUserId() {
		return userId;
	}

	public String getStudentBatchId() {
		return studentBatchId;
	}

	public String getCourseBatchId() {
		return courseBatchId;
	}

	public void setCourseBatchId(String courseBatchId) {
		if (courseBatchId == null)
			return;
		this.courseBatchId = courseBatchId;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		if (courseId == null)
			return;
		this.courseId = courseId;
	}

	public String getStudentSisId() {
		return studentSisId;
	}

	public void setStudentSisId(String studentSisId) {
		if (studentSisId == null)
			return;
		this.studentSisId = studentSisId;
	}

	public void setStudentBatchId(String studentBatchId) {
		this.studentBatchId = studentBatchId;
	}

	public GradeData(String firstName, String lastName, String userName, String name) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.userId = userName;
		this.name = name;
		this.scores = new ArrayList<Score>();
		this.missingScores = new ArrayList<Score>();
	}
	
	public GradeData(String id, String firstName, String lastName, String userName, String name) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userId = userName;
		this.name = name;
		this.scores = new ArrayList<Score>();
		this.missingScores = new ArrayList<Score>();
	}
}
