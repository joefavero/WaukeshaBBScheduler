/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.gradebook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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