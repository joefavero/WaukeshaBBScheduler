/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.algorithm;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Score {

	private double possible;
	private double score;
	private String name;
	private Timestamp submitDate;
	private String status;
	private String feedback;
	private Timestamp dueDate;
	
	public Score(){
		
	}
	
	public Score(double possible, double score, String name, Timestamp submitDate){
		this.possible = possible;
		this.score = score;
		this.name = name;
		this.submitDate = submitDate;
	}
	
	public Score(double possible, double score, String name, Timestamp submitDate, String status){
		this.possible = possible;
		this.score = score;
		this.name = name;
		this.submitDate = submitDate;
		this.status = status;
	}
	
	public double getPossible() {
		return possible;
	}
	public void setPossible(double possible) {
		this.possible = possible;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Timestamp submitDate) {
		this.submitDate = submitDate;
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

	public Timestamp getDueDate() {
		return dueDate;
	}

	public void setDueDate(Timestamp dueDate) {
		this.dueDate = dueDate;
	}
	
	
}
