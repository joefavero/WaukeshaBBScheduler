/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.user;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obsidiansoln.blackboard.RestConstants;
import com.obsidiansoln.blackboard.RestHandler;
import com.obsidiansoln.blackboard.RestRequest;
import com.obsidiansoln.blackboard.model.HTTPStatus;
import com.obsidiansoln.blackboard.model.RequestData;

public class ParentHandler implements RestHandler {

	private static final Logger log = LoggerFactory.getLogger(ParentHandler.class);

	@Override
	public HTTPStatus createObject(String host, String access_token) {
		log.trace("In createObject()");
		return (RestRequest.sendRequest(host, RestConstants.PARENT_PATH, HttpMethod.PUT, access_token, getBody()));
	}
	
	public HTTPStatus createObject(String host, String access_token, RequestData data) {
		log.trace("In createObject()");
		return (RestRequest.sendRequest(host, RestConstants.PARENT_PATH + "/" + data.getUserId() + RestConstants.PARENT_OBSERVERS + "/" + data.getParentId(),
				HttpMethod.PUT, access_token, getBody()));
	}


	@Override
	public HTTPStatus readObject(String host, String access_token, RequestData data) {
		log.trace("In readObject()");
		if (data.getUserId() != null) {
			return (RestRequest.sendRequest(host, RestConstants.PARENT_PATH + "/" + data.getUserId() + RestConstants.PARENT_OBSERVERS
					, HttpMethod.GET, access_token, ""));
		} else if (data.getParentId() != null) {
			return (RestRequest.sendRequest(host, RestConstants.PARENT_PATH + "/" + data.getParentId() + RestConstants.PARENT_OBSERVEES
					, HttpMethod.GET, access_token, ""));
		} else {
			return (RestRequest.sendRequest(host,
					RestConstants.PARENT_PATH,
					HttpMethod.GET, access_token, ""));
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
		return (RestRequest.sendRequest(host, RestConstants.PARENT_PATH + "/externalId:" + RestConstants.USER_ID,
				HttpMethod.PUT, access_token, getBody()));
	}


	//public String deleteObject(String host, String access_token, RequestData data) {
	//	log.trace("In deleteObject()");
	//	return (RestRequest.sendRequest(host, RestConstants.PARENT_PATH + "/externalId:" + RestConstants.USER_ID,
	//			HttpMethod.DELETE, access_token, ""));
	//}
	@Override
	public HTTPStatus deleteObject(String host, String access_token, RequestData data) {
		log.trace("In deleteObject()");
		return (RestRequest.sendRequest(host, RestConstants.PARENT_PATH + "/" + data.getUserId() + RestConstants.PARENT_OBSERVERS + "/" + data.getParentId(),
				HttpMethod.DELETE, access_token, ""));
	}


	public ParentResponseProxy getClientData(String host, String access_token, String p_nextPage, RequestData p_data) {
		log.trace("In getClientData()");
		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, p_data).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		ParentResponseProxy obj = null;
		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, ParentResponseProxy.class);
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

	public ParentProxy getClientData2(String host, String access_token, String p_nextPage, RequestData p_data) {
		log.trace("In getClientData2()");
		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, p_data).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		ParentProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, ParentProxy.class);
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

		String body = "";
		log.info(body);

		return (body);
	}
}
