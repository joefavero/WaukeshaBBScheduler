/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.model;
import java.util.ArrayList;
import java.util.List;


public class StudentReportProgress {

	private String studentId;
	private String name;
	private String externalId;
	private ProgressResult progress;
	private List<String> badCourses;
	private List<CourseProgressResult> programCourses;
	
	public StudentReportProgress(){
		
	}
	
	public StudentReportProgress(String studentId, String externalId, String name, ProgressResult progress, List<CourseProgressResult> programCourses){
		this.studentId = studentId;
		this.externalId = externalId;
		this.name = name;
		this.progress = progress;
		this.badCourses = new ArrayList<String>();
		this.programCourses = programCourses;
	}
	
	public StudentReportProgress(String studentId, String externalId, String name, List<String> badCourses) {
		this.badCourses = badCourses;
		this.name = name;
		this.studentId = studentId;
		this.externalId = externalId;
		this.progress = new ProgressResult();
	}
	
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ProgressResult getProgress() {
		return progress;
	}
	public void setProgress(ProgressResult progress) {
		this.progress = progress;
	}

	public List<String> getBadCourses() {
		return badCourses;
	}

	public void setBadCourses(List<String> badCourses) {
		this.badCourses = badCourses;
	}

	public List<CourseProgressResult> getProgramCourses() {
		return programCourses;
	}

	public void setProgramCourses(List<CourseProgressResult> programCourses) {
		this.programCourses = programCourses;
	}
	
	
	
}