/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.web.model;

import java.util.List;

public class PortalInfo {

	private String logLevel;
	private String adminPassword;
	private List<String> customMessages;
	private boolean includeNextYearCourses;
	
	public PortalInfo() {
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public List<String> getCustomMessages() {
		return customMessages;
	}

	public void setCustomMessages(List<String> customMessages) {
		this.customMessages = customMessages;
	}

	public boolean isIncludeNextYearCourses() {
		return includeNextYearCourses;
	}

	public void setIncludeNextYearCourses(boolean includeNextYearCourses) {
		this.includeNextYearCourses = includeNextYearCourses;
	}

	
}
