/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.gradebook;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.AvailabilityProxy;


@JsonIgnoreProperties(ignoreUnknown = true)
public class GradebookProxy {

	@JsonProperty("userId")
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@JsonProperty("columnId")
	private String columnId;

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	
	@JsonProperty("status")
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonProperty("exempt")
	private boolean exempt;
	
	public boolean isExempt() {
	    return exempt;
	}
	public void setExempt(boolean exempt) {
	    this.exempt = exempt;
	}

	@JsonProperty("changeIndex")
	private String changeIndex;

	public String getChangeIndex() {
		return changeIndex;
	}

	public void setIChangeIndex(String changeIndex) {
		this.changeIndex = changeIndex;
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
	
	@JsonProperty("overridden")
	private Date overridden;

	public Date getOverridden() {
		return overridden;
	}

	public void setOverridden (Date overridden) {
		this.overridden = overridden;
	}

	@JsonProperty("feedback")
	private String feedback;

	public String getFeedback() {
		return feedback;
	}
	
	public void setFeedback(String feedback) {
		this.feedback = feedback;
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
