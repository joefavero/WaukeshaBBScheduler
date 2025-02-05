/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.gradebook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DisplayGradeProxy {

	@JsonProperty("scaleType")
	private String scaleType;

	public String getScaleType() {
		return scaleType;
	}
	
	public void setScaleType(String scaleType) {
		this.scaleType = scaleType;
	}
	
	@JsonProperty("score")
	private double score;

	public double getScore() {
		return score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	@JsonProperty("possible")
	private double possible;

	public double getPossible() {
		return possible;
	}
	
	public void setPossible(double possible) {
		this.possible = possible;
	}
	
	@JsonProperty("text")
	private String text;

	public String geText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
