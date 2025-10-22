/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.group;

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
import com.obsidiansoln.blackboard.coursecopy.SectionInfo;
import com.obsidiansoln.blackboard.model.HTTPStatus;
import com.obsidiansoln.blackboard.model.RequestData;

public class GroupHandler implements RestHandler {

	private static final Logger log = LoggerFactory.getLogger(GroupHandler.class);

	@Override
	public HTTPStatus createObject(String host, String access_token) {
		log.trace("In createObject()");
		return (RestRequest.sendRequest(host, RestConstants.COURSE_PATH_V2, HttpMethod.POST, access_token, getBody("")));
	}

	public GroupProxy createObject(String host, String access_token, RequestData data, String name) {
		log.trace("In createObject()");
		HTTPStatus l_response = RestRequest.sendRequest(host, RestConstants.COURSE_PATH_V2 + data.getCourseId() + RestConstants.COURSE_GROUP_SET_PATH, HttpMethod.POST, access_token, getBody(name));

		ObjectMapper mapper = new ObjectMapper();

		GroupProxy obj = null;

		if (l_response.getData() != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(l_response.getData(),GroupProxy.class);
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

	public GroupProxy createObject(String host, String access_token, RequestData data, SectionInfo section, String groupSet) {
		log.trace("In createObject()");
		HTTPStatus l_response = RestRequest.sendRequest(host, RestConstants.COURSE_PATH_V2+data.getCourseId()+ RestConstants.COURSE_GROUP_SET_PATH + "/" + groupSet +"/groups", HttpMethod.POST, access_token, getBody(data.getCourseId(),section));

		//  Need to handle if there is a group in BB that was not Deleted Correctly
		if (l_response.getStatus() == 409) {
			RequestData data2 = new RequestData();
			data2.setCourseId(data.getCourseId());
			data2.setExternalId(data.getCourseId() + "." + String.valueOf(section.getSectionId()));
			l_response = this.deleteObject(host, access_token, data2);
			l_response = RestRequest.sendRequest(host, RestConstants.COURSE_PATH_V2+data.getCourseId()+ RestConstants.COURSE_GROUP_SET_PATH + "/" + groupSet +"/groups", HttpMethod.POST, access_token, getBody(data.getCourseId(),section));
		}
		ObjectMapper mapper = new ObjectMapper();

		GroupProxy obj = null;

		if (l_response.getData() != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(l_response.getData(),GroupProxy.class);
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
		if (data.getCourseId() != null) {
			if (data.getCourseNumber() != null) {
				return (RestRequest.sendRequest(host, RestConstants.COURSE_PATH_V2+data.getCourseId()+ RestConstants.COURSE_GROUP_SET_PATH
						+ "?name=" + data.getCourseNumber() + "&nameCompare=contains", HttpMethod.GET, access_token, ""));
			} else {
				if (data.getUserName() != null) {
					return (RestRequest.sendRequest(host, RestConstants.COURSE_PATH_V2+data.getCourseId()+ RestConstants.COURSE_GROUP_SET_PATH + "/" + data.getGroupId()
					, HttpMethod.GET, access_token, ""));
				} else {
					return (RestRequest.sendRequest(host, RestConstants.COURSE_PATH_V2+data.getCourseId()+ RestConstants.COURSE_GROUP_SET_PATH + "/" + data.getGroupId() + "/groups"
							, HttpMethod.GET, access_token, ""));
				}
			}
		} else {
			return (RestRequest.sendRequest(host, RestConstants.GRADEBOOK_PATH_V1 + data.getCourseId()
			+ RestConstants.GRADEBOOK_SCHEMAS, HttpMethod.GET, access_token, ""));
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
		return (RestRequest.sendRequest(host, RestConstants.COURSE_PATH_V2 + "courseId:" + data.getCourseName() + RestConstants.COURSE_GROUP + data.getGroupId() + RestConstants.COURSE_USER + "userName:" + data.getUserName(), HttpMethod.PUT, access_token,null));
	}


	@Override
	public HTTPStatus deleteObject(String host, String access_token, RequestData data) {
		log.trace("In deleteObject()");
		if (data.getUserName() != null) {
			return (RestRequest.sendRequest(host, RestConstants.COURSE_PATH_V2 + data.getCourseId() + RestConstants.COURSE_GROUP + "sets/" +data.getGroupId(), HttpMethod.DELETE, access_token, ""));
		} else if (data.getExternalId() != null) {
			return (RestRequest.sendRequest(host, RestConstants.COURSE_PATH_V2 + data.getCourseId() + RestConstants.COURSE_GROUP + "externalId:" + data.getExternalId(), HttpMethod.DELETE, access_token, ""));
		} else {
			return (RestRequest.sendRequest(host, RestConstants.COURSE_PATH_V2 + data.getCourseId() + RestConstants.COURSE_GROUP +data.getGroupId(), HttpMethod.DELETE, access_token, ""));
		}
	}

	public GroupResponseProxy getClientData(String host, String access_token, String p_nextPage,
			RequestData p_data) {
		log.trace("In getClientData()");
		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, p_data).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		GroupResponseProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data,GroupResponseProxy.class);
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

	public GroupProxy getClientData2(String host, String access_token, String p_nextPage, RequestData p_data) {
		log.trace("In getClientData2");
		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, p_data).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		GroupProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, GroupProxy.class);
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

	private String getBody(String courseId, SectionInfo section) {
		log.trace("In getBody()");

		ObjectMapper objMapper = new ObjectMapper();
		GroupProxy l_group = new GroupProxy();
		l_group.setName(String.valueOf("SECTION " + section.getSectionNumber()) + " " + section.getCourseNumber() + " " + section.getTeacherName());
		l_group.setExternalId(courseId + "." + String.valueOf(section.getSectionId()));
		l_group.setDescription("Auto generated groups synced to IC roster.  Do not modify manually.");
		Availability l_avail = new Availability();
		l_avail.setAvailable("Yes");
		l_group.setAvailablity(l_avail);
		Enrollment l_enroll = new Enrollment();
		l_enroll.setType("InstructorOnly");
		l_group.setEnrollment(l_enroll);

		String body = "";
		try {
			body = objMapper.writeValueAsString(l_group);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.debug(body);

		return (body);
	}

	private String getBody(String p_name) {
		log.trace("In getBody()");

		ObjectMapper objMapper = new ObjectMapper();
		GroupProxy l_group = new GroupProxy();
		l_group.setName(p_name);
		Availability l_avail = new Availability();
		l_avail.setAvailable("Yes");
		l_group.setAvailablity(l_avail);
		Enrollment l_enroll = new Enrollment();
		l_enroll.setType("SelfEnrollment");
		l_group.setEnrollment(l_enroll);

		String body = "";
		try {
			body = objMapper.writeValueAsString(l_group);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.debug(body);

		return (body);
	}

	private String getBody() {
		log.trace("In getBody()");

		ObjectMapper objMapper = new ObjectMapper();
		ObjectNode course = objMapper.createObjectNode();
		course.put("contentId", "_34038_1");
		course.put("groupId","_272_1");
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
