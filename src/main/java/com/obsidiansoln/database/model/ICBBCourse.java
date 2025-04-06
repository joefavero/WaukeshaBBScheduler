/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.database.model;

public class ICBBCourse {
	private Long id;
	private String bbCourseId;
	private String bbCourseName;
	private String bbCourseDescription;
	private String schoolYear;
	private String calendarName;
	private String userName;
	private String groupSetId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getBbCourseDescription() {
		return bbCourseDescription;
	}

	public void setBbCourseDescription(String bbCourseDescription) {
		this.bbCourseDescription = bbCourseDescription;
	}

	public String getSchoolYear() {
		return schoolYear;
	}

	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}

	public String getCalendarName() {
		return calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGroupSetId() {
		return groupSetId;
	}

	public void setGroupSetId(String groupSetId) {
		this.groupSetId = groupSetId;
	}
	

}
