/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.membership;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrollmentOptionProxy {
	
	
	@JsonProperty("dataSourceId")
	private String dataSourceId;

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}
	
	@JsonProperty("availability")
	private Available availability;

	public Available getavailability() {
		return availability;
	}

	public void setavailabilityd(Available availability) {
		this.availability = availability;
	}
	
	@JsonProperty("courseRoleId")
	private String courseRoleId;

	public String getCourseRoleId() {
		return courseRoleId;
	}

	public void setCourseRoleId(String courseRoleId) {
		this.courseRoleId = courseRoleId;
	}
	
	@JsonProperty("displayOrder")
	private int displayOrder;

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

}
