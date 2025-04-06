/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.model;

public class BBRestCounts {
	int l_rateLimit = -1;
	int l_rateLimitRemaining = -1;
	int l_rateLimitReset = -1;
	String applicationVersion;
	public int getL_rateLimit() {
		return l_rateLimit;
	}
	public void setL_rateLimit(int l_rateLimit) {
		this.l_rateLimit = l_rateLimit;
	}
	public int getL_rateLimitRemaining() {
		return l_rateLimitRemaining;
	}
	public void setL_rateLimitRemaining(int l_rateLimitRemaining) {
		this.l_rateLimitRemaining = l_rateLimitRemaining;
	}
	public int getL_rateLimitReset() {
		return l_rateLimitReset;
	}
	public void setL_rateLimitReset(int l_rateLimitReset) {
		this.l_rateLimitReset = l_rateLimitReset;
	}
	public String getApplicationVersion() {
		return applicationVersion;
	}
	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}
	
	

}
