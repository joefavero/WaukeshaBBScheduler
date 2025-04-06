/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.coursecopy;

public class PersonInfo {
	
	private Long bbCourseId;
	private Long personId;
	private String personType;
	private String sourcePersonType;
	private Long modifiedByPersonId;
	
	public Long getBbCourseId() {
		return bbCourseId;
	}
	public void setBbCourseId(Long bbCourseId) {
		this.bbCourseId = bbCourseId;
	}
	public Long getPersonId() {
		return personId;
	}
	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getPersonType() {
		return personType;
	}
	public void setPersonType(String personType) {
		this.personType = personType;
	}
	public String getSourcePersonType() {
		return sourcePersonType;
	}
	public void setSourcePersonType(String sourcePersonType) {
		this.sourcePersonType = sourcePersonType;
	}
	public Long getModifiedByPersonId() {
		return modifiedByPersonId;
	}
	public void setModifiedByPersonId(Long modifiedByPersonId) {
		this.modifiedByPersonId = modifiedByPersonId;
	}
	
}
