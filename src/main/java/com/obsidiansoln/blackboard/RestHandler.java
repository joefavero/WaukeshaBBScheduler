/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard;


import com.obsidiansoln.blackboard.model.HTTPStatus;
import com.obsidiansoln.blackboard.model.RequestData;

public interface RestHandler {

	HTTPStatus createObject(String host, String access_token);
	
	HTTPStatus  readObject(String host, String access_token, RequestData data);
	
	HTTPStatus  readObject(String host, String access_token, String nextPage);

	HTTPStatus  updateObject(String host, String access_token, RequestData data);
	
	HTTPStatus  deleteObject(String host, String access_token, RequestData data);
	
}
