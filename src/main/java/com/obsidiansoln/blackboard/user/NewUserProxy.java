/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class NewUserProxy {

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

	public void setUserName(String userName) {
		this.userName = userName;
	}	
	
	@JsonProperty("password")
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}	
	
	@JsonProperty("institutionRoleIds")
	private String[] institutionRoleIds;
	
	public String[] getInstitutionRoleIds() {
		return institutionRoleIds;
	}

	public void setInstitutionRoleIds(String[] institutionRoleIds) {
		this.institutionRoleIds = institutionRoleIds;
	}

	@JsonProperty("systemRoleIds")
	private String[] systemRoleIds;
	
	public String[] getSystemRoleIds() {
		return systemRoleIds;
	}

	public void setSystemRoleIds(String[] systemRoleIds) {
		this.systemRoleIds = systemRoleIds;
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