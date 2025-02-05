/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.PagingProxy;

public class UserResponseProxy {
	
	@JsonProperty("results")
	private UserListProxy results;

	public UserListProxy getResults() {
		return results;
	}
	
	@JsonProperty("paging")
	private PagingProxy paging;

	public PagingProxy getPaging() {
		return paging;
	}

	public void setResults(UserListProxy results) {
		this.results = results;
	}
}