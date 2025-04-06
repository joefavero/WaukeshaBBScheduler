/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.course;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.obsidiansoln.blackboard.RestConstants;
import com.obsidiansoln.blackboard.RestHandler;
import com.obsidiansoln.blackboard.RestRequest;
import com.obsidiansoln.blackboard.model.HTTPStatus;
import com.obsidiansoln.blackboard.model.RequestData;

public class TaskHandler implements RestHandler {

	private static final Logger log = LoggerFactory.getLogger(TaskHandler.class);

	@Override
	public HTTPStatus createObject(String host, String access_token) {
		log.trace("In createObject()");
		return (RestRequest.sendRequest(host,RestConstants.COURSE_PATH, HttpMethod.POST, access_token, getBody()));
	}

	@Override
	public HTTPStatus readObject(String host, String access_token, RequestData data) {
		log.trace("In readObject()");
		if (data != null && data.getUrl() != null) {
			return (RestRequest.sendRequest(
					host, data.getUrl(),
					HttpMethod.GET, access_token, ""));
		} else {
			return (RestRequest.sendRequest(
					host, RestConstants.COURSE_PATH  + "?termId=" + data.getTermId(),
					HttpMethod.GET, access_token, ""));
		}
	}

	@Override
	public HTTPStatus readObject(String host, String access_token, String nextPage) {
		log.trace("In readObject()");
		return (RestRequest.sendRequest(host,nextPage, HttpMethod.GET, access_token, ""));
	}

	@Override
	public HTTPStatus updateObject(String host, String access_token, RequestData data) {
		log.trace("In updateObject()");
		return (RestRequest.sendRequest(host,RestConstants.COURSE_PATH, HttpMethod.PUT, access_token, getBody()));
	}

	@Override
	public HTTPStatus deleteObject(String host, String access_token, RequestData data) {
		log.trace("In deleteObject()");
		return (RestRequest.sendRequest(host, RestConstants.COURSE_PATH, HttpMethod.DELETE, access_token, ""));
	}

	public CourseResponseProxy getClientData(String host, String access_token, String p_nextPage, RequestData p_data) {
		log.trace("In getClientData");
		String data = null;
		if (p_nextPage == null) {
			HTTPStatus l_status = readObject(host, access_token, p_data);
			data = l_status.getData();
		} else {
			HTTPStatus l_status = readObject(host, access_token, p_data);
			data = l_status.getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		CourseResponseProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, CourseResponseProxy.class);
			} catch (JsonParseException e) {
				log.error("Json Parser Error: ", e);
			} catch (JsonMappingException e) {
				log.error("Json Mapping Error: ", e);
			} catch (IOException e) {
				log.error("IO Error: ", e);
			}
		}
		return obj;
	}

	public TaskProxy getClientData2(String host, String access_token, String p_nextPage, RequestData p_data) {
		log.trace("In getClientData2");
		String data = null;
		HTTPStatus l_status = null;
		if (p_nextPage == null) {
			l_status = readObject(host, access_token, p_data);			
			data = l_status.getData();
		} else {
			l_status = readObject(host, access_token, p_data);
			data = l_status.getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		TaskProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, TaskProxy.class);
				if (obj.getStatus() == null) {
					obj.setCourse(mapper.readValue(data, CourseProxy.class));
				}
			} catch (JsonParseException e) {
				log.error("Json Parser Error: ", e);
			} catch (JsonMappingException e) {
				log.error("Json Mapping Error: ", e);
			} catch (IOException e) {
				log.error("IO Error: ", e);
			}
		}
		return obj;
	}

	private String getBody() {
		log.trace("In getBody()");
		ObjectMapper objMapper = new ObjectMapper();
		ObjectNode course = objMapper.createObjectNode();
		// course.put("externalId", RestConstants.COURSE_ID);
		// course.put("dataSourceId", RestConstants.DATASOURCE_ID);
		// course.put("courseId", RestConstants.COURSE_ID);
		// course.put("name", RestConstants.COURSE_NAME);
		// course.put("description", RestConstants.COURSE_DESCRIPTION);
		course.put("allowGuests", "true");
		course.put("readOnly", "false");
		// course.put("termId", RestConstants.TERM_ID);
		ObjectNode availability = course.putObject("availability");
		availability.put("duration", "continuous");

		String body = "";
		try {
			body = objMapper.writeValueAsString(course);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info(body);

		return (body);
	}
}
