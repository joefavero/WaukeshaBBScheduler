/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.gradebook;

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

public class FormulaHandler implements RestHandler {

	private static final Logger log = LoggerFactory.getLogger(AttemptHandler.class);

	@Override
	public HTTPStatus createObject(String host, String access_token) {
		log.trace("In createObject()");
		return (RestRequest.sendRequest(host, RestConstants.GRADEBOOK_PATH, HttpMethod.POST, access_token, getBody()));
	}

	@Override
	public HTTPStatus readObject(String host, String access_token, RequestData data) {
		log.trace("In readObject()");
		if (data.getColumnId() != null) {
			return (RestRequest.sendRequest(host,
					RestConstants.GRADEBOOK_PATH + data.getCourseId() + RestConstants.GRADEBOOK_COLUMNS
							+ data.getColumnId() + RestConstants.GRADEBOOK_ATTEMPTS + "&userId=" + data.getUserId(),
					HttpMethod.GET, access_token, ""));
		} else {
			return (RestRequest.sendRequest(host,
					RestConstants.GRADEBOOK_PATH + data.getCourseId() + RestConstants.GRADEBOOK_COLUMNS, HttpMethod.GET,
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
		log.trace("In updateObject()");
		return (RestRequest.sendRequest(host, RestConstants.GRADEBOOK_PATH, HttpMethod.PUT, access_token, getBody()));
	}

	@Override
	public HTTPStatus deleteObject(String host, String access_token, RequestData data) {
		log.trace("In deleteObject()");
		return (RestRequest.sendRequest(host, RestConstants.GRADEBOOK_PATH, HttpMethod.DELETE, access_token, ""));
	}

	public FormulaParserProxy getClientData(String data, String p_nextPage, RequestData p_data) {
		log.trace("In getClientData()");

		ObjectMapper mapper = new ObjectMapper();

		FormulaParserProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, FormulaParserProxy.class);
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
