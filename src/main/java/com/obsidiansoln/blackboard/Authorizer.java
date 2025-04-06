/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.obsidiansoln.web.model.ConfigData;



public class Authorizer {
	
    private static final Logger log = LoggerFactory.getLogger(Authorizer.class);

   
    public Authorizer () {
    	
    }
    
    public Token authorize(ConfigData p_configData) {
    	log.trace("In authorize()");
    	
    	RestTemplate restTemplate = new RestTemplate();
    	Token token = null;
        
        URI uri = null;
		try {
			uri = new URI(p_configData.getRestHost() + RestConstants.AUTH_PATH );
	
        
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Authorization", "Basic " + getHash(p_configData.getRestKey(), p_configData.getRestSecret()));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		log.debug("Request Headers: " + headers.toString());
		
		HttpEntity<String> request = new HttpEntity<String>("grant_type=client_credentials",headers);
		log.debug("Request Body: " + request.getBody());
		log.debug("URI: " + uri);

		ResponseEntity<Token> response = restTemplate.exchange(uri, HttpMethod.POST, request, Token.class);
		log.debug("Response: " + response.toString());
        
		token = response.getBody();
        log.debug("Access Token: " + token.toString());
		} catch (URISyntaxException e) {
			log.error("ERROR: " + e);
		} catch (Exception l_ex) {
			log.error("ERROR: " + l_ex);
		}
        
        return (token);
    }
    
    private String getHash(String p_key, String p_secret) {
    	log.trace("In getHash()");
    	String hashable = p_key + ":" + p_secret;
    	log.debug("Value to hash: " + hashable);
    	String hash = Base64.getEncoder().encodeToString(hashable.getBytes());
    	log.debug("Hashed Value: " + hash);
    	return(hash);
    	
    }
}