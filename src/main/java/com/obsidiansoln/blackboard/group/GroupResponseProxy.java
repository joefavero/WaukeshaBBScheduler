/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.PagingProxy;

public class GroupResponseProxy {
	
	@JsonProperty("results")
	private GroupListProxy results;

	public GroupListProxy getResults() {
		return results;
	}
	
	@JsonProperty("paging")
	private PagingProxy paging;

	public PagingProxy getPaging() {
		return paging;
	}

	public void setResults(GroupListProxy results) {
		this.results = results;
	}
}
