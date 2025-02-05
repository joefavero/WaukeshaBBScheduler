/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
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

