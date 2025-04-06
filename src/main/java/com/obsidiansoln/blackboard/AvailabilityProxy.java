/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailabilityProxy {

	@JsonProperty("available")
	private String available;

	public String getAvailable() {
		return available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}
	
	@JsonProperty("duration")
	private DurationProxy duration;

	public DurationProxy getDuration() {
		return duration;
	}

	public void setDuration(DurationProxy duration) {
		this.duration = duration;
	}

}
