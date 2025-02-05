/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.web.model;

public class RestInfo {
	private String host;
	private String key;
	private String secret;
	private int limit;
	private int limitRemaining;
	private int limitReset;
	private String applicationVersion;
	
	public RestInfo() {

	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getLimitRemaining() {
		return limitRemaining;
	}

	public void setLimitRemaining(int limitRemaining) {
		this.limitRemaining = limitRemaining;
	}

	public int getLimitReset() {
		return limitReset;
	}

	public void setLimitReset(int limitReset) {
		this.limitReset = limitReset;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	
}
