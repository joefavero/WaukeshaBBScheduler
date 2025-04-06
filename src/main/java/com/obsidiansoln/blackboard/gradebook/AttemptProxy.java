/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.gradebook;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AttemptProxy {

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
	
	@JsonProperty("groupAttemptId")
	private String groupAttemptId;

	public String getGroupAttemptId() {
		return groupAttemptId;
	}

	public void setGroupAttemptId(String groupAttemptId) {
		this.groupAttemptId= groupAttemptId;
	}
	
	@JsonProperty("groupOverride")
	private boolean groupOverride;
	
	public boolean isGroupOverride() {
	    return groupOverride;
	}

	public void setGroupOverride(boolean groupOverride) {
	    this.groupOverride = groupOverride;
	}
	
	@JsonProperty("status")
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonProperty("displayGrade")
	private DisplayGradeProxy displayGrade;

	public DisplayGradeProxy getDisplayGrade() {
		return displayGrade;
	}

	public void setDisplayGrade(DisplayGradeProxy displayGrade) {
		this.displayGrade = displayGrade;
	}
	
	@JsonProperty("text")
	private String text;

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	@JsonProperty("score")
	private double score;

	public double getScore() {
		return score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	@JsonProperty("feedback")
	private String feedback;

	public String getFeedback() {
		return feedback;
	}
	
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	
	@JsonProperty("exempt")
	private boolean exempt;
	
	public boolean isExempt() {
	    return exempt;
	}

	public void setExempt(boolean exempt) {
	    this.exempt = exempt;
	}
	
	@JsonProperty("created")
	private Date created;

	public Date getCreated() {
		return created;
	}

	public void setCreated (Date created) {
		this.created = created;
	}
	
	@JsonProperty("attemptDate")
	private Date attemptDate;

	public Date getAttemptDate() {
		return attemptDate;
	}

	public void setAttemptDate (Date attemptDate) {
		this.attemptDate = attemptDate;
	}

	@JsonProperty("modified")
	private Date modified;

	public Date getModified() {
		return modified;
	}

	public void setModified (Date modified) {
		this.modified = modified;
	}
}

