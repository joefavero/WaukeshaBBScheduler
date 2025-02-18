package com.obsidiansoln.database.model;

public class ICBBCourse {
	private Long id;
	private String bbCourseId;
	private String courseId;
	private String courseName;
	private String courseDescription;
	private String groupSetId;
	
	public String getCourseId() {
		return courseId;
	}
	
	public String getBbCourseId() {
		return bbCourseId;
	}

	public void setBbCourseId(String bbCourseId) {
		this.bbCourseId = bbCourseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseDescription() {
		return courseDescription;
	}

	public void setCourseDescription(String courseDescription) {
		this.courseDescription = courseDescription;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupSetId() {
		return groupSetId;
	}

	public void setGroupSetId(String groupSetId) {
		this.groupSetId = groupSetId;
	}
	

}
