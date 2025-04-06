/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.web.model;

import java.util.List;

public class SearchFormData {
	private List<SemesterInfo> semesters;
	private List<String> terms;
	private List<FilterInfo> filters;
	private String contains;
	private boolean includeUnavailable;
	private String defaultTerm;
	private int defaultSemesterIndex;

	public SearchFormData() {
		includeUnavailable = true;
	}

	public List<SemesterInfo> getSemesters() {
		return semesters;
	}

	public void setSemesters(List<SemesterInfo> semesters) {
		this.semesters = semesters;
	}

	public List<FilterInfo> getFilters() {
		return filters;
	}

	public void setFilters(List<FilterInfo> filters) {
		this.filters = filters;
	}

	public List<String> getTerms() {
		return terms;
	}

	public void setTerms(List<String> terms) {
		this.terms = terms;
	}

	public String getContains() {
		return contains;
	}

	public void setContains(String contains) {
		this.contains = contains;
	}

	public boolean isIncludeUnavailable() {
		return includeUnavailable;
	}

	public void setIncludeUnavailable(boolean includeUnavailable) {
		this.includeUnavailable = includeUnavailable;
	}

	public String getDefaultTerm() {
		return defaultTerm;
	}

	public void setDefaultTerm(String defaultTerm) {
		this.defaultTerm = defaultTerm;
	}

	public int getDefaultSemesterIndex() {
		return defaultSemesterIndex;
	}

	public void setDefaultSemesterIndex(int defaultSemesterIndex) {
		this.defaultSemesterIndex = defaultSemesterIndex;
	}

}
