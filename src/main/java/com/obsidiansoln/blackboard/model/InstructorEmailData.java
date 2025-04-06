/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.model;



public class InstructorEmailData {
	
	private String m_firstName = null;
	private String m_lastName = null;
	private String m_email = null;
	
	public String getFirstName() {
		return m_firstName;
	}
	public void setFirstName(String p_firstName) {
		this.m_firstName = p_firstName;
	}
	public String getLastName() {
		return m_lastName;
	}
	public void setLastName(String p_lastName) {
		this.m_lastName = p_lastName;
	}
	public String getEmail() {
		return m_email;
	}
	public void setEmail(String p_email) {
		this.m_email = p_email;
	}

	
	
}
