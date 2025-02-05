/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.coursecopy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.PagingProxy;

public class CourseCopyResponseProxy {
	
	@JsonProperty("results")
	private CourseListProxy results;

	public CourseListProxy getResults() {
		return results;
	}
	public void setResults(CourseListProxy results) {
		this.results = results;
	}
	
	@JsonProperty("paging")
	private PagingProxy paging;

	public PagingProxy getPaging() {
		return paging;
	}
}