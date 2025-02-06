package com.obsidiansoln.blackboard.coursecopy;

public class SectionInfo {
	
	private Long bbCourseId;
	private Long calendarId;
	private Long courseId;
	private Long sectionId;
	private Long personId;
	private boolean selected;
	
	public Long getBbCourseId() {
		return bbCourseId;
	}
	public void setBbCourseId(Long bbCourseId) {
		this.bbCourseId = bbCourseId;
	}
	public Long getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}
	public Long getCourseId() {
		return courseId;
	}
	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}
	public Long getSectionId() {
		return sectionId;
	}
	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}
	public Long getPersonId() {
		return personId;
	}
	public void setPersonId(Long personId) {
		this.personId = personId;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
