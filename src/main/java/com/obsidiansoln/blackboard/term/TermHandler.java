/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.term;

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
import com.obsidiansoln.blackboard.user.UserProxy;

public class TermHandler implements RestHandler {

	private static final Logger log = LoggerFactory.getLogger(TermHandler.class);

	@Override
	public HTTPStatus createObject(String host, String access_token) {
		log.trace("In createObject()");
		return (RestRequest.sendRequest(host, RestConstants.TERM_PATH, HttpMethod.POST, access_token, getBody()));
	}

	@Override
	public HTTPStatus readObject(String host, String access_token, RequestData data) {
		log.trace("In readObject()");
		if (data != null && data.getTermId() != null) {
			return (RestRequest.sendRequest(host,
					RestConstants.TERM_PATH + "/" + data.getTermId() + "?availability.available=" + RestConstants.TERM_AVAILABLE, HttpMethod.GET,
					access_token, ""));
		} else {
			return (RestRequest.sendRequest(host,
					RestConstants.TERM_PATH + "?availability.available=" + RestConstants.TERM_AVAILABLE, HttpMethod.GET,
					access_token, ""));
		}
	}

	@Override
	public HTTPStatus readObject(String host, String access_token, String nextPage) {
		log.trace("In readObject()");
		return (RestRequest.sendRequest(host, nextPage, HttpMethod.GET, access_token, ""));
	}

	@Override
	public HTTPStatus updateObject(String host, String access_token, RequestData data) {
		log.trace("In uodateObject()");
		;
		return (RestRequest.sendRequest(host, RestConstants.TERM_PATH, HttpMethod.PUT, access_token, getBody()));
	}

	@Override
	public HTTPStatus deleteObject(String host, String access_token, RequestData data) {
		log.trace("In deleteObject()");
		return (RestRequest.sendRequest(host, RestConstants.TERM_PATH, HttpMethod.DELETE, access_token, ""));
	}

	public TermResponseProxy getClientData(String host, String access_token, String p_nextPage, RequestData p_data) {
		log.trace("In getClientData()");
		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, new RequestData()).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		TermResponseProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, TermResponseProxy.class);
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

	public TermProxy getClientData2(String host, String access_token, String p_nextPage, RequestData p_data) {
		log.trace("In getClientData2()");
		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, p_data).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		TermProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, TermProxy.class);
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

		/*
		 * 
		 * { "externalId": "string", "dataSourceId": "string", "name": "string",
		 * "description": "string", "availability": { "available": "Yes", "duration": {
		 * "type": "Continuous", "start": "2016-02-24T19:53:21.448Z", "end":
		 * "2016-02-24T19:53:21.448Z", "daysOfUse": 0 } } }
		 * 
		 * { "externalId" : "BBDN-TERM-JAVA", "dataSourceId" : "BBDN-DSK-JAVA", "name" :
		 * "REST Demo Term - Java", "description" : "Term Used For REST Demo - Java",
		 * "availability" : { "available" : "Yes", "duration" : { "type" : "continuous"
		 * } } }
		 */

		ObjectMapper objMapper = new ObjectMapper();
		ObjectNode term = objMapper.createObjectNode();
		// term.put("externalId", RestConstants.TERM_ID);
		// term.put("dataSourceId", RestConstants.DATASOURCE_ID);
		// term.put("name", RestConstants.TERM_NAME);
		// term.put("description", RestConstants.TERM_DISPLAY);
		ObjectNode availability = term.putObject("availability");
		availability.put("available", "Yes");
		ObjectNode duration = availability.putObject("duration");
		duration.put("type", "Continuous");

		String body = "";
		try {
			body = objMapper.writeValueAsString(term);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info(body);

		return (body);
	}
}
