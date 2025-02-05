/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
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
