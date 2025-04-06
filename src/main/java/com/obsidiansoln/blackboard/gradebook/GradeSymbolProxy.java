/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.gradebook;


import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class GradeSymbolProxy {

	@JsonProperty("text")
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@JsonProperty("absoluteValue")
	private double absoluteValue;

	public double getAbsoluteValue() {
		return absoluteValue;
	}

	public void setAbsoluteValue(double absoluteValue) {
		this.absoluteValue = absoluteValue;
	}
	
	@JsonProperty("lowerBound")
	private double lowerBound;

	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}
	
	@JsonProperty("upperBound")
	private double upperBound;

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}
}