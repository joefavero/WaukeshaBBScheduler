/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.templates;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.PagingProxy;

public class TemplateResponseProxy {
	
	@JsonProperty("results")
	private TemplateListProxy results;

	public TemplateListProxy getResults() {
		return results;
	}
	public void setResults(TemplateListProxy results) {
		this.results = results;
	}
	
	@JsonProperty("paging")
	private PagingProxy paging;

	public PagingProxy getPaging() {
		return paging;
	}
}