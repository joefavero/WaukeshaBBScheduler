/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.user;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obsidiansoln.blackboard.RestConstants;
import com.obsidiansoln.blackboard.RestHandler;
import com.obsidiansoln.blackboard.RestRequest;
import com.obsidiansoln.blackboard.model.HTTPStatus;
import com.obsidiansoln.blackboard.model.RequestData;

public class UserHandler implements RestHandler {

	private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

	@Override
	public HTTPStatus createObject(String host, String access_token) {
		log.trace("In createObject()");
		return (RestRequest.sendRequest(host, RestConstants.USER_PATH, HttpMethod.POST, access_token, getBody()));
	}
	
	public UserProxy createObject(String host, String access_token, String body) {
		log.trace("In createObject()");
		HTTPStatus l_status = RestRequest.sendRequest(host, RestConstants.USER_PATH, HttpMethod.POST, access_token, body);
		
		ObjectMapper mapper = new ObjectMapper();

		UserProxy obj = null;

		if (l_status.getData() != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(l_status.getData(), UserProxy.class);
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

	@Override
	public HTTPStatus readObject(String host, String access_token, RequestData data) {
		log.trace("In readObject()");
		if (data.getUserId() != null) {
			return (RestRequest.sendRequest(host, RestConstants.USER_PATH + "/" + data.getUserId()
					, HttpMethod.GET, access_token, ""));
		} else if (data.getUserName() != null) {
			return (RestRequest.sendRequest(host,
					RestConstants.USER_PATH + RestConstants.USER_USERNAME_PARAMETER + data.getUserName()
					, HttpMethod.GET, access_token, ""));
		} else if (data.getExternalId() != null) {
			return (RestRequest.sendRequest(host,
					RestConstants.USER_PATH + RestConstants.USER_EXTERNALID_PARAMETER + data.getExternalId()
					, HttpMethod.GET, access_token, ""));
		} else if (data.getUuid() != null) {
			return (RestRequest.sendRequest(host,
					RestConstants.USER_PATH + RestConstants.USER_UUID_PARAMETER + data.getUuid()
					, HttpMethod.GET, access_token, ""));
		} else if (data.getFamilyName() != null) {
			return (RestRequest.sendRequest(host,
					RestConstants.USER_PATH + "/" + RestConstants.USER_FAMILY_NAME_PARAMETER + data.getFamilyName(),
					HttpMethod.GET, access_token, ""));
		} else if (data.getDatasourceId() != null) {
			return (RestRequest.sendRequest(host,
					RestConstants.USER_PATH + "/" + RestConstants.USER_DATA_SOURCE_PARAMETER + data.getDatasourceId(),
					HttpMethod.GET, access_token, ""));
		} else {
			return (RestRequest.sendRequest(host,
					RestConstants.USER_PATH + "?availability.available=Yes",
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
		return (RestRequest.sendRequest(host, RestConstants.USER_PATH + "/externalId:" + RestConstants.USER_ID,
				HttpMethod.PUT, access_token, getBody()));
	}

	@Override
	public HTTPStatus deleteObject(String host, String access_token, RequestData data) {
		log.trace("In deleteObject()");
		return (RestRequest.sendRequest(host, RestConstants.USER_PATH + "/externalId:" + RestConstants.USER_ID,
				HttpMethod.DELETE, access_token, ""));
	}

	public UserResponseProxy getClientData(String host, String access_token, String p_nextPage, RequestData p_data) {
		log.trace("In getClientData()");
		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, p_data).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		UserResponseProxy obj = null;
		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, UserResponseProxy.class);
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

	public UserProxy getClientData2(String host, String access_token, String p_nextPage, RequestData p_data) {
		log.trace("In getClientData2()");

		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, p_data).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}
		ObjectMapper mapper = new ObjectMapper();

		UserProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, UserProxy.class);
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
		ObjectMapper objMapper = new ObjectMapper();
		NewUserProxy l_user = new NewUserProxy();
		l_user.setUserName("test.parent");
		l_user.setPassword("password@1");
		NameProxy l_name = new NameProxy();
		l_name.setFamily("Parent");
		l_name.setGiven("Test");
		l_user.setName(l_name);
		ContactProxy l_contact= new ContactProxy();
		l_contact.setEmail("test.parent@gmail.com");
		l_user.setContact(l_contact);
		
		// Set the Institution Roles
		String[] l_institionRoleIds = {"OBSERVER"};
		l_user.setInstitutionRoleIds(l_institionRoleIds);
		
		// Set the System Roles
		String[] l_systemRoleIds = {"OBSERVER"};
		l_user.setSystemRoleIds(l_systemRoleIds);

		try {
			body = objMapper.writeValueAsString(l_user);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info(body);

		return (body);
	}
}
