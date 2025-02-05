/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.gradebook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.PagingProxy;

public class AttemptResponseProxy {
	
	@JsonProperty("results")
	private AttemptListProxy results;

	public AttemptListProxy getResults() {
		return results;
	}
	public void setResults(AttemptListProxy results) {
		this.results = results;
	}
	
	@JsonProperty("paging")
	private PagingProxy paging;

	public PagingProxy getPaging() {
		return paging;
	}


}