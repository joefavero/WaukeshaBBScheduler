package com.obsidiansoln.blackboard.gradebook;

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

public class SchemaHandler implements RestHandler {

	private static final Logger log = LoggerFactory.getLogger(GradebookHandler.class);

	@Override
	public HTTPStatus createObject(String host, String access_token) {
		log.trace("In createObject()");
		return (RestRequest.sendRequest(host, RestConstants.GRADEBOOK_PATH_V1, HttpMethod.POST, access_token, getBody()));
	}

	@Override
	public HTTPStatus readObject(String host, String access_token, RequestData data) {
		log.trace("In readObject()");
		if (data.getCourseId() != null) {
				return (RestRequest.sendRequest(host, RestConstants.GRADEBOOK_PATH_V1 + data.getCourseId()
						+ RestConstants.GRADEBOOK_SCHEMAS, HttpMethod.GET, access_token, ""));
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
		return (RestRequest.sendRequest(host, RestConstants.GRADEBOOK_PATH_V1 + data.getCourseId() + RestConstants.GRADEBOOK_SCHEMAS + data.getSchemaId() , HttpMethod.PATCH, access_token, getBody()));
	}

	@Override
	public HTTPStatus deleteObject(String host, String access_token, RequestData data) {
		log.trace("In deleteObject()");
		return (RestRequest.sendRequest(host, RestConstants.GRADEBOOK_PATH_V1 + data.getCourseId() + RestConstants.GRADEBOOK_SCHEMAS + data.getSchemaId(), HttpMethod.DELETE, access_token, ""));
	}

	public SchemaResponseProxy getClientData(String host, String access_token, String p_nextPage,
			RequestData p_data) {
		log.trace("In getClientData()");
		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, p_data).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		SchemaResponseProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data,SchemaResponseProxy.class);
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

	public SchemaProxy getClientData2(String host, String access_token, String p_nextPage, RequestData p_data) {
		log.trace("In getClientData2");
		String data = null;
		if (p_nextPage == null) {
			data = readObject(host, access_token, p_data).getData();
		} else {
			data = readObject(host, access_token, p_nextPage).getData();
		}

		ObjectMapper mapper = new ObjectMapper();

		SchemaProxy obj = null;

		if (data != null) {
			// JSON file to Java object
			try {
				obj = mapper.readValue(data, SchemaProxy.class);
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
		SchemaProxy l_schema = new SchemaProxy();
		
		l_schema.setExternalId(null);
		l_schema.setTitle("Final Letter");
		l_schema.setDescription("Updated 2024");
		
		GradeSymbolListProxy l_gradeSymbolList = new GradeSymbolListProxy();
		GradeSymbolProxy l_gradeSymbol = new GradeSymbolProxy();
		l_gradeSymbol.setText("A");
		l_gradeSymbol.setAbsoluteValue(95.0);
		l_gradeSymbol.setLowerBound(89.5);
		l_gradeSymbol.setUpperBound(100.0);
		l_gradeSymbolList.add(l_gradeSymbol);
		
		l_gradeSymbol = new GradeSymbolProxy();
		l_gradeSymbol.setText("B");
		l_gradeSymbol.setAbsoluteValue(85.0);
		l_gradeSymbol.setLowerBound(79.5);
		l_gradeSymbol.setUpperBound(89.5);
		l_gradeSymbolList.add(l_gradeSymbol);
		
		l_gradeSymbol = new GradeSymbolProxy();
		l_gradeSymbol.setText("C");
		l_gradeSymbol.setAbsoluteValue(75.0);
		l_gradeSymbol.setLowerBound(69.5);
		l_gradeSymbol.setUpperBound(79.5);
		l_gradeSymbolList.add(l_gradeSymbol);
		
		l_gradeSymbol = new GradeSymbolProxy();
		l_gradeSymbol.setText("D");
		l_gradeSymbol.setAbsoluteValue(65.0);
		l_gradeSymbol.setLowerBound(59.5);
		l_gradeSymbol.setUpperBound(69.5);
		l_gradeSymbolList.add(l_gradeSymbol);
		
		l_gradeSymbol = new GradeSymbolProxy();
		l_gradeSymbol.setText("F");
		l_gradeSymbol.setAbsoluteValue(30.0);
		l_gradeSymbol.setLowerBound(0.0);
		l_gradeSymbol.setUpperBound(59.5);
		l_gradeSymbolList.add(l_gradeSymbol);
		
		l_schema.setSymbols(l_gradeSymbolList);
				
		String body = "";
		try {
			body = objMapper.writeValueAsString(l_schema);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.debug(body);

		return (body);
	}
}
