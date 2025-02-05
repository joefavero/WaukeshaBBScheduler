/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.gradebook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.course.CourseListProxy;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SelectedProxy {

	@JsonProperty("average")
	private boolean average;
	
	public boolean isAverage() {
	    return average;
	}

	public void setAverage(boolean average) {
	    this.average = average;
	}
	
	@JsonProperty("elements")
	private ElementListProxy elements;

	public ElementListProxy getElements() {
		return elements;
	}
	public void setResults(ElementListProxy results) {
		this.elements = elements;
	}
	
	@JsonProperty("running")
	private boolean running;
	
	public boolean isRunning() {
	    return running;
	}

	public void setRunning(boolean running) {
	    this.running = running;
	}
}