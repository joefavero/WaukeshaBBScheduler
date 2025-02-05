package com.obsidiansoln.blackboard.model;

import org.springframework.http.HttpHeaders;

public class HTTPStatus {
	
	int status;
	HttpHeaders headers;
	String data;
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public HttpHeaders getHeaders() {
		return headers;
	}
	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}

}
