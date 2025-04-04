/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.web.model;

import java.util.ArrayList;
import java.util.List;

import com.obsidiansoln.database.model.ICMessage;

public class PortalInfo {

	private String logLevel;
	private String adminPassword;
	private List<ICMessage> customMessages;
	private ArrayList<String> terms;
	
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

	public List<ICMessage> getCustomMessages() {
		return customMessages;
	}

	public void setCustomMessages(List<ICMessage> customMessages) {
		this.customMessages = customMessages;
	}

	public ArrayList<String> getTerms() {
		return terms;
	}

	public void setTerms(ArrayList<String> terms) {
		this.terms = terms;
	}

	
}
