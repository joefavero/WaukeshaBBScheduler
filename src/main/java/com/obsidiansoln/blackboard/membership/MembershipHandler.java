/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.membership;

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

public class MembershipHandler implements RestHandler {

	private static final Logger log = LoggerFactory.getLogger(MembershipHandler.class);

	@Override
	public HTTPStatus createObject(String host, String access_token) {
		log.trace("In createObject()");
		return (RestRequest
				.sendRequest(host,
						RestConstants.MEMBERSHIP_PATH + RestConstants.MEMBERSHIP_COURSE_EXTENSION
								+ "/users/externalId:" + RestConstants.USER_ID,
						HttpMethod.PUT, access_token, getBody(null)));
	}
	
	public HTTPStatus createObject(String host, String access_token, RequestData data, EnrollmentOptionProxy enrollment) {
		log.trace("In createObject()");
		return (RestRequest
				.sendRequest(host,
						RestConstants.MEMBERSHIP_PATH + RestConstants.MEMBERSHIP_COURSE_EXTENSION + RestConstants.MEMBERSHIP_COURSENAME_PARAMETER + data.getCourseName() +
						RestConstants.MEMBERSHIP_USER_EXTENSION + RestConstants.MEMBERSHIP_USERNAME_PARAMETER + data.getUserName(),
						HttpMethod.PUT, access_token, getBody(enrollment)));
	}

	@Override
	public HTTPStatus readObject(String host, String access_token, RequestData data) {
		log.trace("In readObject()");
		if (data != null && data.getUserId() != null) {
			if (data.getCourseId() != null) {
				return (RestRequest.sendRequest(host,
						RestConstants.MEMBERSHIP_PATH + RestConstants.MEMBERSHIP_COURSE_EXTENSION + data.getCourseId()
								+ RestConstants.MEMBERSHIP_USER_EXTENSION + data.getUserId(),
						HttpMethod.GET, access_token, ""));

			} else {
				if (data.getCourseRole() != null && data.getCourseRole().equals("Instructor")) {
					return (RestRequest.sendRequest(host,
							RestConstants.MEMBERSHIP_PATH + RestConstants.MEMBERSHIP_USER_EXTENSION + data.getUserId()
									+ RestConstants.MEMBERSHIP_COURSE_EXTENSION
									+ RestConstants.MEMBERSHIP_INSTRUCTOR_ROLE_PARAMETER,
							HttpMethod.GET, access_token, ""));
				} else {
					return (RestRequest.sendRequest(host,
							RestConstants.MEMBERSHIP_PATH + RestConstants.MEMBERSHIP_USER_EXTENSION + data.getUserId()
									+ RestConstants.MEMBERSHIP_COURSE_EXTENSION
									+ RestConstants.MEMBERSHIP_STUDENT_ROLE_PARAMETER,
							HttpMethod.GET, access_token, ""));
				}
			}
		} else if (data != null & data.getUserName() != null) {
			return (RestRequest.sendRequest(host,
					RestConstants.MEMBERSHIP_PATH + RestConstants.MEMBERSHIP_USER_EXTENSION
							+ RestConstants.MEMBERSHIP_USERNAME_PARAMETER + data.getUserName()
							+ RestConstants.MEMBERSHIP_COURSE_EXTENSION,
					HttpMethod.GET, access_token, ""));
		} else if (data != null & data.getCourseId() != null) {
			if (data.getCourseRole() != null && data.getCourseRole().equals("Instructor")) {
				return (RestRequest.sendRequest(host,
						RestConstants.MEMBERSHIP_PATH + RestConstants.MEMBERSHIP_COURSE_EXTENSION
								+ data.getCourseId() + RestConstants.MEMBERSHIP_USER_EXTENSION + RestConstants.MEMBERSHIP_INSTRUCTOR_ROLE_PARAMETER,
						HttpMethod.GET, access_token, ""));
			} else {
				return (RestRequest.sendRequest(host,
						RestConstants.MEMBERSHIP_PATH + RestConstants.MEMBERSHIP_COURSE_EXTENSION
								+ data.getCourseId() +  RestConstants.MEMBERSHIP_USER_EXTENSION + RestConstants.MEMBERSHIP_STUDENT_ROLE_PARAMETER,
						HttpMethod.GET, access_token, ""));
			}
		} else {
			return (RestRequest.sendRequest(host,
					RestConstants.MEMBERSHIP_PATH + RestConstants.MEMBERSHIP_COURSE_EXTENSION + "/courseId:"
							 + RestConstants.MEMBERSHIP_USER_EXTENSION,
					HttpMethod.GET, access_token, ""));
		}
	}

	@Override
	public HTTPStatus readObject(String host, String access_token, String nextPage) {
		log.trace("In readObject(NextPage)");
		return (RestRequest.sendRequest(host, nextPage, HttpMethod.GET, access_token, ""));
	}

	@Override
	public HTTPStatus updateObject(String host, String access_token, RequestData data) {
		log.trace("In updateObject()");
		return (RestRequest
				.sendRequest(host,
						RestConstants.MEMBERSHIP_PATH + "/externalId:"
								+ "/users/externalId:" + RestConstants.USER_ID,
						HttpMethod.PUT, access_token, getBody(null)));
	}

	@Override
	public HTTPStatus deleteObject(String host, String access_token, RequestData data) {
		log.trace("In deleteObject()");
		return (RestRequest.sendRequest(host, RestConstants.MEMBERSHIP_PATH + "/externalId:"
				+ "/users/externalId:" + RestConstants.USER_ID, HttpMethod.DELETE,
				access_token, ""));
	}

	public MembershipResponseProxy getClientData(String host, String access_token, String p_nextPage,
			RequestData p_data) {
		log.trace("In getClientData()");
		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, p_data).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}
		ObjectMapper mapper = new ObjectMapper();
		MembershipResponseProxy obj = null;
		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, MembershipResponseProxy.class);
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

	private String getBody(EnrollmentOptionProxy p_enrollment) {
		log.trace("In getBody()");
		ObjectMapper objMapper = new ObjectMapper();

		String body = "";
		try {
			body = objMapper.writeValueAsString(p_enrollment);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info(body);

		return (body);
	}
}
