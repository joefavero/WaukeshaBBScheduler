/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.gradebook;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FormulaProxy {

	@JsonProperty("formula")
	private String formula;

	public String getFormula() {
		return formula;
	}
	
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	@JsonProperty("aliases")
	private HashMap<String,String> aliases;

	public HashMap<String,String> getAliases() {
		return aliases;
	}
	
	public void setAliases(HashMap<String, String> aliases) {
		this.aliases = aliases;
	}
}
