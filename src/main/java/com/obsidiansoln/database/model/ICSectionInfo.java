/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.database.model;

public class ICSectionInfo {

	private Long calendarID;
	private Long sectionID;
	private Long courseID;
	private int sectionNumber;

	public Long getCalendarID() {
		return calendarID;
	}
	public void setCalendarID(Long calendarID) {
		this.calendarID = calendarID;
	}
	public Long getSectionID() {
		return sectionID;
	}
	public void setSectionID(Long sectionID) {
		this.sectionID = sectionID;
	}
	public Long getCourseID() {
		return courseID;
	}
	public void setCourseID(Long courseID) {
		this.courseID = courseID;
	}
	public int getSectionNumber() {
		return sectionNumber;
	}
	public void setSectionNumber(int sectionNumber) {
		this.sectionNumber = sectionNumber;
	}
	


}
