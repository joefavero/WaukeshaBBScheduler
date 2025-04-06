/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.web.model;

import java.util.List;

public class FilterInfo {
	
	String name;
	boolean disableContains;
	List<String> options;

	public FilterInfo() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isDisableContains() {
		return disableContains;
	}

	public void setDisableContains(boolean disableContains) {
		this.disableContains = disableContains;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	
}
