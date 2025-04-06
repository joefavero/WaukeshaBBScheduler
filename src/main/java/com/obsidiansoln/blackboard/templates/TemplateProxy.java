/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.templates;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.course.CourseProxy;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateProxy {
	
	
	public TemplateProxy(CourseProxy l_course) {
		super();
		this.templateId = l_course.getCourseId();
		this.templateName = l_course.getName();
		this.categories = null;
	}
	
	@JsonProperty("templateId")
	private String templateId;
	
	@JsonProperty("templateName")
	private String templateName;
	
	@JsonProperty("categories")
	private List<String> categories;
	
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
}
