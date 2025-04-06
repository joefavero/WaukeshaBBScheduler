/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.course;

import java.io.IOException;
import java.net.URLEncoder;

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

public class CategoryHandler implements RestHandler {

	private static final Logger log = LoggerFactory.getLogger(CategoryHandler.class);

	@Override
	public HTTPStatus  createObject(String host, String access_token) {
		log.trace("In createObject()");
		return (RestRequest.sendRequest(host, RestConstants.CATEGORY_PATH, HttpMethod.POST, access_token, getBody()));
	}

	@Override
	public HTTPStatus  readObject(String host, String access_token, RequestData data) {
		log.trace("In readObject()");
		if (data != null && data.getCategoryId() != null) {
			if (data.getCategoryType() != null && data.getCategoryType().equalsIgnoreCase("courses")) {
				return (RestRequest.sendRequest(host, RestConstants.CATEGORY_PATH + RestConstants.CATEGORY_COURSE_TYPE
						+ data.getCategoryId() + "/courses", HttpMethod.GET, access_token, ""));
			} else {
				return (RestRequest.sendRequest(host,
						RestConstants.CATEGORY_PATH + RestConstants.CATEGORY_COURSE_TYPE + data.getCategoryId(),
						HttpMethod.GET, access_token, ""));
			}
		} else if (data != null && data.getCategoryName() != null) {
			return (RestRequest.sendRequest(host,
					RestConstants.CATEGORY_PATH + RestConstants.CATEGORY_COURSE_TYPE + "?title=" + URLEncoder.encode(data.getCategoryName()),
					HttpMethod.GET, access_token, ""));
		}  else {
			return (RestRequest.sendRequest(host, RestConstants.CATEGORY_PATH + RestConstants.CATEGORY_COURSE_TYPE,
					HttpMethod.GET, access_token, ""));
		}
	}

	@Override
	public HTTPStatus  readObject(String host, String access_token, String nextPage) {
		log.trace("In readObject()");
		return (RestRequest.sendRequest(host, nextPage, HttpMethod.GET, access_token, ""));
	}

	@Override
	public HTTPStatus  updateObject(String host, String access_token, RequestData data) {
		log.trace("In updateObject()");
		return (RestRequest.sendRequest(host, RestConstants.CATEGORY_PATH, HttpMethod.PUT, access_token, getBody()));
	}

	@Override
	public HTTPStatus  deleteObject(String host, String access_token, RequestData data) {
		log.trace("In deleteObject()");
		return (RestRequest.sendRequest(host, RestConstants.CATEGORY_PATH, HttpMethod.DELETE, access_token, ""));
	}

	public CategoryResponseProxy getClientData(String host, String access_token, String p_nextPage,
			RequestData p_data) {
		log.trace("In getClientData");
		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, p_data).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		CategoryResponseProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, CategoryResponseProxy.class);
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

	public CategoryProxy getClientData2(String host, String access_token, String p_nextPage, RequestData p_data) {
		log.trace("In getClientData2");
		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, p_data).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		CategoryProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, CategoryProxy.class);
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
