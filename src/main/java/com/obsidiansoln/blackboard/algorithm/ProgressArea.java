/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.algorithm;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgressArea {
	private String name; 
	private double weight;
	private String courseId;
	private List<Score> scores;
	private List<Score> missingScores;
	private String courseGrade;
	private String courseBatchId;
	private String instructorEmail;
	
	public ProgressArea(){
		
	}
	
	public ProgressArea(double weight, List<Score> scores){
		this.weight = weight;
		this.scores = scores;
	}
	
	public ProgressArea(double weight, List<Score> scores, String name, String courseId){
		this.weight = weight;
		this.scores = scores;
		this.name = name;
		this.courseId = courseId;
	}
	
	public ProgressArea(double weight, List<Score> scores, String name, String courseId, List<Score> missingScores){
		this.weight = weight;
		this.scores = scores;
		this.name = name;
		this.courseId = courseId;
		this.missingScores = missingScores;
	}
	
	public ProgressArea(double weight, List<Score> scores, String name, String courseId, String uid, List<Score> missingScores){
		this.weight = weight;
		this.scores = scores;
		this.name = name;
		this.courseId = courseId;
		this.missingScores = missingScores;
		this.courseBatchId = uid;
	}
	
	public ProgressArea(double weight, List<Score> scores, String name, String courseId, String uid, List<Score> missingScores, String instructorEmail){
		this.weight = weight;
		this.scores = scores;
		this.name = name;
		this.courseId = courseId;
		this.missingScores = missingScores;
		this.courseBatchId = uid;
		this.instructorEmail = instructorEmail;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getWeight(){
		return this.weight;
	}
	
	public void setWeight(double weight){
		this.weight = weight;
	}

	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public List<Score> getMissingScores() {
		return missingScores;
	}

	public void setMissingScores(List<Score> missingScores) {
		this.missingScores = missingScores;
	}

	public String getCourseGrade() {
		return courseGrade;
	}

	public void setCourseGrade(String courseGrade) {
		this.courseGrade = courseGrade;
	}

	public String getCourseBatchId() {
		return courseBatchId;
	}

	public void setCourseBatchId(String courseBatchId) {
		this.courseBatchId = courseBatchId;
	}

	public String getInstructorEmail() {
		return instructorEmail;
	}

	public void setInstructorEmail(String instructorEmail) {
		this.instructorEmail = instructorEmail;
	}
	
	
}
