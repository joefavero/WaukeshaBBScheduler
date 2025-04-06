/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.coursecopy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseCopyProxy {

	@JsonProperty("targetCourse")
	private TargetCourseProxy targetCourse;

	public TargetCourseProxy getTargetCourse() {
		return targetCourse;
	}

	public void setTargetCourse (TargetCourseProxy targetCourse) {
		this.targetCourse = targetCourse;
	}
}
