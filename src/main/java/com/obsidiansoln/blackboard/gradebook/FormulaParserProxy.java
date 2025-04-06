/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.gradebook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FormulaParserProxy {

	@JsonProperty("selected")
	private SelectedProxy selected;

	public SelectedProxy getSelected() {
		return selected;
	}
	
	public void setSelected(SelectedProxy selected) {
		this.selected = selected;
	}
	
}

