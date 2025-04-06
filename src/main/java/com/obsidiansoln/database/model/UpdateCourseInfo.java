/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.database.model;

public class UpdateCourseInfo {
	
	private String bbCourseId;
	private String bbCourseName;
	private String bbCourseDescription;
	
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
	public String getBbCourseDescription() {
		return bbCourseDescription;
	}
	public void setBbCourseDescription(String bbCourseDescription) {
		this.bbCourseDescription = bbCourseDescription;
	}

}
