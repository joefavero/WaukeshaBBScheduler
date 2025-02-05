/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.obsidiansoln.blackboard.model.HTTPStatus;

public abstract class RestRequest {

	private static final Logger log = LoggerFactory.getLogger(RestRequest.class);
	private static int counter = 0;

	public static int getCounter() {
		return counter;
	}

	public static void setCounter(int counter) {
		RestRequest.counter = counter;
	}

	public static HTTPStatus sendRequest(String host, String sUri, HttpMethod method, String access_token, String body) {
		log.trace("In sendRequest()");
		HTTPStatus l_httpStatus = new HTTPStatus();
		try {
		
			// Increment Counter
			RestRequest.setCounter(RestRequest.getCounter() + 1);

			RestTemplate restTemplate = new RestTemplate();

			// Workaround to allow for PATCH requests
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			restTemplate.setRequestFactory(requestFactory);
			URI uri = null;
			try {
				uri = new URI(host + sUri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			HttpHeaders headers = new HttpHeaders();

			headers.add("Authorization", "Bearer " + access_token);
			headers.setContentType(MediaType.APPLICATION_JSON);
			log.debug("Request Headers: " + headers.toString());

			HttpEntity<String> request = new HttpEntity<String>(body, headers);
			log.info("URI: " + uri);
			log.info("Request Body: " + request.getBody());

			ResponseEntity<String> response = restTemplate.exchange(uri, method, request, String.class);

			// Process Headers and look for Blackboard REST API Limit
			HttpHeaders l_headers = response.getHeaders();
			int l_rateLimit = -1;
			int l_rateLimitRemaining = -1;
			int l_rateLimitReset = -1;

			for (Map.Entry<String, List<String>> l_entry : l_headers.entrySet()) {
				if (l_entry.getKey().equalsIgnoreCase("X-Rate-Limit-Remaining")) {
					l_rateLimitRemaining = Integer.parseInt(l_entry.getValue().get(0));
				} else if (l_entry.getKey().equalsIgnoreCase("X-Rate-Limit-Limit")) {
					l_rateLimit = Integer.parseInt(l_entry.getValue().get(0));
				} else if (l_entry.getKey().equalsIgnoreCase("X-Rate-Limit-Reset")) {
					l_rateLimitReset = Integer.parseInt(l_entry.getValue().get(0));
				}
			}

			// log.info(" Limit Reset: " + l_rateLimitReset);

			if (l_rateLimitRemaining == 0) {
				log.error("Blackboard REST API Limit Exceeded");
				log.error("		Limit: " + l_rateLimit);
				log.error("		Limit Remaining: " + l_rateLimitRemaining);
				log.error("		Limit Reset: " + l_rateLimitReset);
				return null;
			}

			log.debug("Status: " + response.getStatusCode());
			log.debug("Body: " + response.getBody());
			
			l_httpStatus.setStatus(response.getStatusCode().value());
			l_httpStatus.setHeaders(response.getHeaders());
			if (response.getStatusCode().value() == 200) {
				l_httpStatus.setData(response.getBody());
				return (l_httpStatus);
			} else if (response.getStatusCode().value() == 201) {
				l_httpStatus.setData(response.getBody());
				return (l_httpStatus);
			} else if (response.getStatusCode().value() == 202) {
				l_httpStatus.setData(response.getBody());
				return (l_httpStatus);
			} else if (response.getStatusCode().value() == 303) {
				l_httpStatus.setData(response.getBody());
				return (l_httpStatus);
			}else if (response.getStatusCode().value() == 400) { // Bad Request
				return (l_httpStatus);
			} else if (response.getStatusCode().value() == 403) { // Forbidden
				return (l_httpStatus);
			} else if (response.getStatusCode().value() == 404) { // Not Found
				return (l_httpStatus);
			} else if (response.getStatusCode().value() == 409) { // Not Found
				return (l_httpStatus);
			} else {
				return (l_httpStatus);
			}
		} catch(HttpStatusCodeException e) {
			l_httpStatus.setStatus(e.getStatusCode().value());
			log.info("STATUS: " + e.getStatusCode().value());
			log.info("MESSAGE: " + e.getResponseBodyAsString());
	        return l_httpStatus;
	    }
	}

	public static HttpHeaders sendRequest(String host, String sUri, HttpMethod method, String access_token) {

		try {
			log.trace("In sendRequest()");
			// Increment Counter
			RestRequest.setCounter(RestRequest.getCounter() + 1);

			RestTemplate restTemplate = new RestTemplate();

			// Workaround for allowing unsuccessful HTTP Errors to still print to the screen
			restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
				protected boolean hasError(HttpStatus statusCode) {
					return false;
				}
			});

			// Workaround to allow for PATCH requests
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			restTemplate.setRequestFactory(requestFactory);

			URI uri = null;
			try {
				uri = new URI(host + sUri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			HttpHeaders headers = new HttpHeaders();

			headers.add("Authorization", "Bearer " + access_token);
			headers.setContentType(MediaType.APPLICATION_JSON);
			log.debug("Request Headers: " + headers.toString());

			HttpEntity<String> request = new HttpEntity<String>("", headers);
			log.debug("URI: " + uri);

			ResponseEntity<String> response = restTemplate.exchange(uri, method, request, String.class);

			// Process Headers and look for Blackboard REST API Limit
			HttpHeaders l_headers = response.getHeaders();
			return l_headers;
		
		} catch (Exception e) {
			return (null);
		}
	}
	

}
