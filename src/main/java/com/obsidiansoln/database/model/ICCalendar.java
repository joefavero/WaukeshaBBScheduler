/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.database.model;

public class ICCalendar {
	
    private Long calendarID;
    private String name;
    private String schoolName;


    public ICCalendar() {
    }

    public ICCalendar(Long calendarId, String name, String schoolName) {
        this.calendarID = calendarId;
        this.name = name;
        this.schoolName = schoolName;
    }

    public Long getCalendarID() {
        return calendarID;
    }

    public void setCalendarID(Long calendarID) {
        this.calendarID = calendarID;
    }
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}


}
