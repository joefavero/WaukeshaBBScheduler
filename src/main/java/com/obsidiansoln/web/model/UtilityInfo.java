/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.web.model;

import java.util.List;

import com.obsidiansoln.database.model.ICTemplate;

public class UtilityInfo {
	private List<ICTemplate> templates;

	public List<ICTemplate> getTemplates() {
		return templates;
	}

	public void setTemplates(List<ICTemplate> templates) {
		this.templates = templates;
	}


}
