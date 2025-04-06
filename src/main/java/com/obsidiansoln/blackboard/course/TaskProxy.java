/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.course;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskProxy {

	@JsonProperty("id")
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("status")
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonProperty("started")
	private Date started;

	public Date getStarted() {
		return started;
	}

	public void setStarted (Date started) {
		this.started= started;
	}
	
	private CourseProxy course;
	
	public CourseProxy getCourse() {
		return course;
	}

	public void setCourse (CourseProxy course) {
		this.course= course;
	}
	

}
