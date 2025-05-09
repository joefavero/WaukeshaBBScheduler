/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.obsidiansoln.blackboard.AvailabilityProxy;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ParentProxy {
	

	public ParentProxy() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ParentProxy(UserProxy p_user) {
		super();
		this.id = p_user.getId();
		this.name = p_user.getName();
		this.contact = p_user.getContact();
		this.userName = p_user.getUserName();
	}

	@JsonProperty("id")
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@JsonProperty("uuid")
	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	@JsonProperty("externalId")
	private String externalId;

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId= externalId;
	}
	
	@JsonProperty("dataSourceId")
	private String dataSourceId;

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId= dataSourceId;
	}
	
	@JsonProperty("studentId")
	private String studentId;

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId= studentId;
	}
	
	@JsonProperty("userName")
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUsername(String userName) {
		this.userName = userName;
	}	
	
	@JsonProperty("availability")
	private AvailabilityProxy availability;

	public AvailabilityProxy getAvailablity() {
		return availability;
	}

	public void setAvailability (AvailabilityProxy availability) {
		this.availability = availability;
	}
	
	@JsonProperty("name")
	private NameProxy name;

	public NameProxy getName() {
		return name;
	}

	public void setName(NameProxy name) {
		this.name = name;
	}
	
	@JsonProperty("contact")
	private ContactProxy contact;

	public ContactProxy getContact() {
		return contact;
	}

	public void setContact(ContactProxy contact) {
		this.contact = contact;
	}
}