/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.model;

import com.obsidiansoln.blackboard.user.ParentProxy;
import com.obsidiansoln.blackboard.user.UserProxy;

public class StudentData {

	private String id;
	private String firstName;
	private String lastName;
	private String externalId;
	private String email;

	public StudentData(UserProxy p_user) {
		this.id = p_user.getId();
		this.firstName = p_user.getName().getGiven();
		this.lastName = p_user.getName().getFamily();
		this.externalId = p_user.getExternalId();
		if (p_user.getContact() != null) {
			this.email = p_user.getContact().getEmail();
		}
	}

	public StudentData(ParentProxy p_user) {
		this.id = p_user.getId();
		this.firstName = p_user.getName().getGiven();
		this.lastName = p_user.getName().getFamily();
		this.externalId = p_user.getExternalId();
	}

	public StudentData(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
