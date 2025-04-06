/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.model;

import com.obsidiansoln.blackboard.gradebook.AttemptProxy;

public class NotInListStatus {
	
	private boolean status;
	private AttemptProxy attempt;

	public NotInListStatus() {
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public AttemptProxy getAttempt() {
		return attempt;
	}

	public void setAttempt(AttemptProxy attempt) {
		this.attempt = attempt;
	}

	
}
