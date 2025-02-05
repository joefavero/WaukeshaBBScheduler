package com.obsidiansoln.blackboard.coursecopy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TargetCourseProxy {
	@JsonProperty("courseId")
	private String courseId;

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	
	@JsonProperty("id")
	private String id;

	public String getId() {
		return id;
	}

	public void setId (String id) {
		this.id = id;
	}

}
