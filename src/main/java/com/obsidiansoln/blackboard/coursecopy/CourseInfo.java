package com.obsidiansoln.blackboard.coursecopy;

import java.util.List;

public class CourseInfo {
	
	private Long endYear;
	private Long calendarId;
	private String courseTemplateId;
	private Long personId;
	private String targetCourseId;
	private String targetCourseName;
	private String targetCourseDescription;
	private String targetCourseDataSource;
	private String targetCourseTerm;
	private List<String> sections;
	private List<String> additionalStudents;
	private List<String> additionalTeachers;
	

	public Long getEndYear() {
		return endYear;
	}
	public void setEndYear(Long endYear) {
		this.endYear = endYear;
	}
	public Long getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	public String getCourseTemplateId() {
		return courseTemplateId;
	}
	public void setCourseTemplateId(String courseTemplateId) {
		this.courseTemplateId = courseTemplateId;
	}
	public Long getPersonId() {
		return personId;
	}
	public void setPersonId(Long personId) {
		this.personId = personId;
	}
	public String getTargetCourseId() {
		return targetCourseId;
	}
	public void setTargetCourseId(String targetCourseId) {
		this.targetCourseId = targetCourseId;
	}
	public String getTargetCourseName() {
		return targetCourseName;
	}
	public void setTargetCourseName(String targetCourseName) {
		this.targetCourseName = targetCourseName;
	}
	public String getTargetCourseDescription() {
		return targetCourseDescription;
	}
	public void setTargetCourseDescription(String targetCourseDescription) {
		this.targetCourseDescription = targetCourseDescription;
	}
	public String getTargetCourseDataSource() {
		return targetCourseDataSource;
	}
	public void setTargetCourseDataSource(String targetCourseDataSource) {
		this.targetCourseDataSource = targetCourseDataSource;
	}
	public String getTargetCourseTerm() {
		return targetCourseTerm;
	}
	public void setTargetCourseTerm(String targetCourseTerm) {
		this.targetCourseTerm = targetCourseTerm;
	}
	public List<String> getSections() {
		return sections;
	}
	public void setSections(List<String> sections) {
		this.sections = sections;
	}
	public List<String> getAdditionalStudents() {
		return additionalStudents;
	}
	public void setAdditionalStudents(List<String> additionalStudents) {
		this.additionalStudents = additionalStudents;
	}
	public List<String> getAdditionalTeachers() {
		return additionalTeachers;
	}
	public void setAdditionalTeachers(List<String> additionalTeachers) {
		this.additionalTeachers = additionalTeachers;
	}
	
}
