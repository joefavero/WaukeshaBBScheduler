/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnapshotJobDetails {

	private static Logger mLog = LoggerFactory.getLogger(SnapshotJobDetails.class);

	private String endpoint;
	private String operation;
	private String jobTitle;
	private String timestamp;

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint.toLowerCase();
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation.toLowerCase();
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public static SnapshotJobDetails fromFileName(String filename) {
		mLog.info("In fromFileName() ..." + filename);

		// Remove Directory Path
		if (filename.contains(":")) {
			filename = filename.substring(filename.lastIndexOf('\\') + 1, filename.indexOf(".txt"));
		} else {
			filename = filename.substring(filename.lastIndexOf('/') + 1, filename.indexOf(".txt"));
		}
		
		mLog.info("In fromFileName() ..." + filename);
		SnapshotJobDetails details = new SnapshotJobDetails();
		if (filename == null)
			return null;
		String[] tokens = filename.split("_");
		mLog.info("Number of Tokens: " + tokens.length);
		if (tokens.length != 4)
			return null;
		details.setEndpoint(tokens[0]);
		details.setOperation(tokens[1]);
		details.setJobTitle(tokens[2]);
		details.setTimestamp(tokens[3]);

		return details;
	}

}
