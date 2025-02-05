/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.membership;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.AvailabilityProxy;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MembershipProxy {

	@JsonProperty("id")
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@JsonProperty("userId")
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@JsonProperty("courseId")
	private String courseId;

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	
	@JsonProperty("courseRoleId")
	private String courseRoleId;

	public String getCourseRoleId() {
		return courseRoleId;
	}

	public void setCourseRoleId(String courseRoleId) {
		this.courseRoleId = courseRoleId;
	}
	
	@JsonProperty("availability")
	private AvailabilityProxy availability;

	public AvailabilityProxy getAvailablity() {
		return availability;
	}

	public void setAvailability (AvailabilityProxy availability) {
		this.availability = availability;
	}

	
}
	