/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.coursecopy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseIdParamProxy {
	
	@JsonProperty("name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty("in")
	private String in;

	public String getIn() {
		return in;
	}

	public void setIn(String in) {
		this.in = in;
	}
	
	@JsonProperty("description")
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonProperty("required")
	private boolean required;
	
	public boolean isRequired() {
	    return required;
	}
	
	public void setRequired(boolean required) {
	    this.required = required;
	}
	
	@JsonProperty("type")
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
