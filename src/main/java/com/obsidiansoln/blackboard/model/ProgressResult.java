/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.model;

import java.util.List;


public class ProgressResult {
	private double progressReportScore;	
	private List<CourseProgressResult> courseResults;
	private String firstName;
	private String lastName;
	
	public ProgressResult(){
		
	}	

	public ProgressResult(double score, List<CourseProgressResult> results, String firstName, String lastName){
		this.progressReportScore = score;
		this.courseResults = results;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public double getProgressReportScore() {
		return progressReportScore;
	}
	public void setProgressReportScore(double progressReportScore) {
		this.progressReportScore = progressReportScore;
	}
	public List<CourseProgressResult> getCourseResults() {
		return courseResults;
	}
	public void setCourseResults(List<CourseProgressResult> courseResults) {
		this.courseResults = courseResults;
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
}
