/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.web.model;

public class LocationInfo {
	
	private String id;
	private String location;
	private boolean available;

	public LocationInfo() {
	}

	public LocationInfo(String id, String location, boolean available) {
		this.id = id;
		this.location = location;
		this.available = available;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

}
