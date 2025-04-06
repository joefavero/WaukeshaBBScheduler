/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.gradebook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreProxy {

	@JsonProperty("possible")
	private double possible;

	public double getPossible() {
		return possible;
	}
	
	public void setPossible(double possible) {
		this.possible = possible;
	}
}
