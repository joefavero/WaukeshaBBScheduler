/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
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
