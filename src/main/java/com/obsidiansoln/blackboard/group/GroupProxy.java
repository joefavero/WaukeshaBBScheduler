/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.group;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupProxy {

	@JsonProperty("id")
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@JsonProperty("externalId")
	private String externalId;

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	@JsonProperty("groupSetId")
	private String groupSetId;

	public String getGroupSetId() {
		return groupSetId;
	}

	public void setGroupSetId(String groupSetId) {
		this.groupSetId = groupSetId;
	}
	
	@JsonProperty("name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty("description")
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonProperty("uuid")
	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	@JsonProperty("created")
	private Date created;

	public Date getCreated() {
		return created;
	}

	public void setCreated (Date created) {
		this.created = created;
	}
	@JsonProperty("modified")
	private Date modified;

	public Date getModified() {
		return modified;
	}

	public void setModified (Date modified) {
		this.modified = modified;
	}
	
	@JsonProperty("availability")
	private Availability availablity;

	public Availability getAvailablity() {
		return availablity;
	}

	public void setAvailablity(Availability availablity) {
		this.availablity = availablity;
	}
	
	@JsonProperty("enrollment")
	private Enrollment enrollment;

	public Enrollment getEnrollment() {
		return enrollment;
	}

	public void setEnrollment(Enrollment enrollment) {
		this.enrollment = enrollment;
	}
	
	
}
	