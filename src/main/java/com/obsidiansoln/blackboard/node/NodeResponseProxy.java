package com.obsidiansoln.blackboard.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.PagingProxy;

public class NodeResponseProxy {
	
	@JsonProperty("results")
	private NodeListProxy results;

	public NodeListProxy getResults() {
		return results;
	}
	
	@JsonProperty("paging")
	private PagingProxy paging;

	public PagingProxy getPaging() {
		return paging;
	}

	public void setResults(NodeListProxy results) {
		this.results = results;
	}
}