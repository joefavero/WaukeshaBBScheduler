/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */package com.obsidiansoln.blackboard.datasource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.PagingProxy;

public class DatasourceResponseProxy {
	
	@JsonProperty("results")
	private DatasourceListProxy results;

	public DatasourceListProxy getResults() {
		return results;
	}
	public void setResults(DatasourceListProxy results) {
		this.results = results;
	}
	
	@JsonProperty("paging")
	private PagingProxy paging;

	public PagingProxy getPaging() {
		return paging;
	}
}