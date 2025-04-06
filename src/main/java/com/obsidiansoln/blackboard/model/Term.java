/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.model;


public class Term {

	private Long termId = null;
	private String name = null;
	
	
	public Term(Long termId, String name) {
		super();
		this.termId = termId;
		this.name = name;
	}
	
	public Term() {

	}

	public Long getTermId() {
		return termId;
	}
	public void setTermId(Long termId) {
		this.termId = termId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
