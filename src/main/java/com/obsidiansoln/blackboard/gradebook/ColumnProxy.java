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
public class ColumnProxy {

	@JsonProperty("id")
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@JsonProperty("name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty("created")
	private Date created;

	public Date getCreated() {
		return created;
	}

	public void setCreated (Date created) {
		this.created = created;
	}
	
	@JsonProperty("externalGrade")
	private boolean externalGrade;
	
	public boolean isExternalGrade() {
	    return externalGrade;
	}

	public void setExternalGrade(boolean isExternalGrade) {
	    this.externalGrade = isExternalGrade;
	}

	
	@JsonProperty("score")
	private ScoreProxy score;

	public ScoreProxy getScore() {
		return score;
	}

	public void setScore (ScoreProxy score) {
		this.score = score;
	}
	
	@JsonProperty("grading")
	private GradingProxy grading;

	public GradingProxy getGrading() {
		return grading;
	}

	public void setGrading (GradingProxy grading) {
		this.grading = grading;
	}
	
	@JsonProperty("availability")
	private AvailabilityProxy availability;

	public AvailabilityProxy getAvailablity() {
		return availability;
	}

	public void setAvailability (AvailabilityProxy availability) {
		this.availability = availability;
	}
	
	@JsonProperty("gradebookCategoryId")
	private String gradebookCategoryId;

	public String getGradebookCategoryId() {
		return gradebookCategoryId;
	}

	public void setGradebookCategoryId(String gradebookCategoryId) {
		this.gradebookCategoryId = gradebookCategoryId;
	}
	
	@JsonProperty("formula")
	private FormulaProxy formula;

	public FormulaProxy getFormula() {
		return formula;
	}

	public void setFormula(FormulaProxy formula) {
		this.formula = formula;
	}
	
	@JsonProperty("includeInCalculations")
	private boolean includeInCalculations;
	
	public boolean isIncludeInCalculations() {
	    return includeInCalculations;
	}

	public void setIncludeInCalculations(boolean isIncludeInCalculations) {
	    this.includeInCalculations = isIncludeInCalculations;
	}
	
	@JsonProperty("showStatisticsToStudents")
	private boolean showStatisticsToStudents;
	
	public boolean isShowStatisticsToStudents() {
	    return showStatisticsToStudents;
	}

	public void setShowStatisticsToStudents(boolean isShowStatisticsToStudents) {
	    this.showStatisticsToStudents = isShowStatisticsToStudents;
	}
}
