/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.term;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.PagingProxy;

public class TermResponseProxy {
	
	@JsonProperty("results")
	private TermListProxy results;

	public TermListProxy getResults() {
		return results;
	}
	
	@JsonProperty("paging")
	private PagingProxy paging;

	public PagingProxy getPaging() {
		return paging;
	}

	public void setResults(TermListProxy results) {
		this.results = results;
	}
}