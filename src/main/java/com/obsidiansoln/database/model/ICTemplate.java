/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.database.model;

public class ICTemplate {
	
	
	public ICTemplate() {
		super();

	}

	private Long bbMasterId;
	private String bbCourseId;
	private String bbCourseName;
	private String masterLevel;
	private String masterSubjectArea;
	public Long getBbMasterId() {
		return bbMasterId;
	}
	public void setBbMasterId(Long bbMasterId) {
		this.bbMasterId = bbMasterId;
	}
	public String getBbCourseId() {
		return bbCourseId;
	}
	public void setBbCourseId(String bbCourseId) {
		this.bbCourseId = bbCourseId;
	}
	public String getBbCourseName() {
		return bbCourseName;
	}
	public void setBbCourseName(String bbCourseName) {
		this.bbCourseName = bbCourseName;
	}
	public String getMasterLevel() {
		return masterLevel;
	}
	public void setMasterLevel(String masterLevel) {
		this.masterLevel = masterLevel;
	}
	public String getMasterSubjectArea() {
		return masterSubjectArea;
	}
	public void setMasterSubjectArea(String masterSubjectArea) {
		this.masterSubjectArea = masterSubjectArea;
	}

	
	
	
}
