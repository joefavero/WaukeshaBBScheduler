/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.PagingProxy;

public class ParentResponseProxy {

	@JsonProperty("results")
	private ParentListProxy results;

	public ParentListProxy getResults() {
		return results;
	}
	
	public void setResults(ParentListProxy results) {
		this.results = results;
	}
	
	@JsonProperty("paging")
	private PagingProxy paging;

	public PagingProxy getPaging() {
		return paging;
	}


}