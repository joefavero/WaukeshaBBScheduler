/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
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
