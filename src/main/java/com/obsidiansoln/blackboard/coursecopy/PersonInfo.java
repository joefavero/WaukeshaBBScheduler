package com.obsidiansoln.blackboard.coursecopy;

public class PersonInfo {
	
	private Long bbCourseId;
	private Long personId;
	private char personType;
	private char sourcePersonType;
	
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
	public char getPersonType() {
		return personType;
	}
	public void setPersonType(char personType) {
		this.personType = personType;
	}
	public char getSourcePersonType() {
		return sourcePersonType;
	}
	public void setSourcePersonType(char sourcePersonType) {
		this.sourcePersonType = sourcePersonType;
	}
	
}
