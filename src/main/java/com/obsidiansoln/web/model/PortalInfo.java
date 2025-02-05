/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.web.model;

import java.util.ArrayList;
import java.util.Date;

public class PortalInfo {

	private String logLevel;
	private Date semesterStart1;
	private Date semesterEnd1;
	private Date semesterStart2;
	private Date semesterEnd2;
	private String defaultSemester;
	private String defaultTerm;
	private String adminEmail;
	private String adminInstructor;
	private String adminPhone;
	private ArrayList<String> terms;
	
	public PortalInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}


	public Date getSemesterStart1() {
		return semesterStart1;
	}

	public void setSemesterStart1(Date semesterStart1) {
		this.semesterStart1 = semesterStart1;
	}

	public Date getSemesterEnd1() {
		return semesterEnd1;
	}

	public void setSemesterEnd1(Date semesterEnd1) {
		this.semesterEnd1 = semesterEnd1;
	}

	public Date getSemesterStart2() {
		return semesterStart2;
	}

	public void setSemesterStart2(Date semesterStart2) {
		this.semesterStart2 = semesterStart2;
	}

	public Date getSemesterEnd2() {
		return semesterEnd2;
	}

	public void setSemesterEnd2(Date semesterEnd2) {
		this.semesterEnd2 = semesterEnd2;
	}
	
	public String getDefaultSemester() {
		return defaultSemester;
	}

	public void setDefaultSemester(String defaultSemester) {
		this.defaultSemester = defaultSemester;
	}

	public String getDefaultTerm() {
		return defaultTerm;
	}

	public void setDefaultTerm(String defaultTerm) {
		this.defaultTerm = defaultTerm;
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	public String getAdminInstructor() {
		return adminInstructor;
	}

	public void setAdminInstructor(String adminInstructor) {
		this.adminInstructor = adminInstructor;
	}

	public String getAdminPhone() {
		return adminPhone;
	}

	public void setAdminPhone(String adminPhone) {
		this.adminPhone = adminPhone;
	}

	public ArrayList<String> getTerms() {
		return terms;
	}

	public void setTerms(ArrayList<String> terms) {
		this.terms = terms;
	}

	
}
