/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
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
