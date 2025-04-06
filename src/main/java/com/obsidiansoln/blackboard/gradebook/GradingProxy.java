/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.gradebook;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GradingProxy {

	@JsonProperty("type")
	private String type;

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@JsonProperty("due")
	private Date due;

	public Date getDue() {
		return due;
	}

	public void setDue (Date due) {
		this.due = due;
	}
	
	@JsonProperty("schemaId")
	private String schemaId;

	public String getSchemaId() {
		return schemaId;
	}
	
	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}

}
