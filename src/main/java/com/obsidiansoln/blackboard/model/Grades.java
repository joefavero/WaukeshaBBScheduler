/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Grades {

	public Grades() {
		// TODO Auto-generated constructor stub
	}

	private String m_id = null;
	private String m_attemptId = null;
	private Timestamp m_attemptDate = null;
	private Timestamp m_dateModified= null;
	private String m_title = null;
	private String m_userid = null;
	private String m_courseid = null;
	private String m_coursename = null;
	private BigDecimal m_score = null;
	private BigDecimal m_points = null;
	private String m_displayScore = null;
	private String m_catalog = null;
	private String m_courseBatchUID = null;
	private String m_userBatchUID = null;
	private String firstName = null;
	private String lastName = null;
	private boolean isPlaceHolder = false;
	private String status;
	private String feedback;
	private Timestamp due= null;
	private String columnId;
	private BigDecimal excludedScore = null;
	
	
	public String getId() {
		return m_id;
	}
	public void setId(String id) {
		this.m_id = id;
	}
	public String getAttemptId() {
		return m_attemptId;
	}
	public void setAttemptId(String p_attemptId) {
		this.m_attemptId = p_attemptId;
	}	
	public Timestamp getAttemptDate() {
		return m_attemptDate;
	}
	public void setAttemptDate(Timestamp p_attemptDate) {
		this.m_attemptDate = p_attemptDate;
	}
	public Timestamp getDateModified() {
		return m_dateModified;
	}
	public void setDateModified(Timestamp p_dateModified) {
		this.m_dateModified = p_dateModified;
	}
	public String getTitle() {
		return m_title;
	}
	public void setTitle(String p_title) {
		this.m_title = p_title;
	}
	public String getUserid() {
		return m_userid;
	}
	public void setUserid(String p_userid) {
		this.m_userid = p_userid;
	}
	public String getCourseid() {
		return m_courseid;
	}
	public void setCourseid(String p_courseid) {
		this.m_courseid = p_courseid;
	}
	public String getCourseName() {
		return m_coursename;
	}
	public void setCourseName(String p_coursename) {
		this.m_coursename = p_coursename;
	}
	public BigDecimal getScore() {
		return m_score;
	}
	public void setScore(BigDecimal p_score) {
		this.m_score = p_score;
	}
	public BigDecimal getPoints() {
		return m_points;
	}
	public void setPoints(BigDecimal p_points) {
		this.m_points = p_points;
	}
	public String getDisplayScore() {
		return m_displayScore;
	}
	public void setDisplayScore(String p_displayScore) {
		this.m_displayScore = p_displayScore;
	}
	public String getCatalog() {
		return m_catalog;
	}
	public void setCatalog(String p_catalog) {
		this.m_catalog = p_catalog;
	}

	public String getCourseBatchUID() {
		return m_courseBatchUID;
	}
	public void setCourseBatchUID(String p_courseBatchUID) {
		this.m_courseBatchUID = p_courseBatchUID;
	}
	public String getUserBatchUID() {
		return m_userBatchUID;
	}
	public void setUserBatchUID(String p_userBatchUID) {
		this.m_userBatchUID = p_userBatchUID;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public boolean isPlaceHolder() {
		return isPlaceHolder;
	}
	public void setPlaceHolder(boolean isPlaceHolder) {
		this.isPlaceHolder = isPlaceHolder;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getFeedback() {
		return feedback;
	}
	
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	
	public Timestamp getDue() {
		return due;
	}
	
	public void setDue(Timestamp due) {
		this.due = due;
	}
	
	public String getColumnId() {
		return columnId;
	}
	
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	
	public BigDecimal getExcludedScore() {
		return excludedScore;
	}
	
	public void setExcludedScore(BigDecimal excludedScore) {
		this.excludedScore = excludedScore;
	}
	
}
