/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.model;

import java.util.List;

public class ErrorProgressResult extends ProgressResult {
	
	private String name;
	private List<String> badCourses;
	private String studentId;
	
	public ErrorProgressResult(String studentId, String name, List<String> badCourses) {
		
		this.name = name;
		this.studentId = studentId;
		this.badCourses = badCourses;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getBadCourses() {
		return badCourses;
	}

	public void setBadCourses(List<String> badCourses) {
		this.badCourses = badCourses;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	
	

}
