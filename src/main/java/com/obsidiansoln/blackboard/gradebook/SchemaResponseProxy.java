/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.gradebook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.PagingProxy;

public class SchemaResponseProxy {
	
	@JsonProperty("results")
	private SchemaListProxy results;

	public SchemaListProxy getResults() {
		return results;
	}
	
	@JsonProperty("paging")
	private PagingProxy paging;

	public PagingProxy getPaging() {
		return paging;
	}

	public void setResults(SchemaListProxy results) {
		this.results = results;
	}
}
