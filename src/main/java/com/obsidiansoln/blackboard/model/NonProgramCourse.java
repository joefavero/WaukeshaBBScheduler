/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.obsidiansoln.blackboard.algorithm.Score;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NonProgramCourse {

	private String name;
	private String courseId;
	private List<Score> scores;	
	private List<Score> missingScores;
	private String courseGrade;	
	private double weight;
	private String courseBatchId;
	private String instructorEmail;
	
	public NonProgramCourse(){
		this.scores = new ArrayList<Score>();
	}
	
	public NonProgramCourse(String name, List<Score> scores, String courseId){
		this.name = name;
		this.scores = scores;
		this.courseId = courseId;		
	}
	
	public NonProgramCourse(String name, List<Score> scores, String courseId, List<Score> missingScores){
		this.name = name;
		this.scores = scores;
		this.courseId = courseId;	
		this.missingScores = missingScores;
	}
	
	public NonProgramCourse(double weight, String name, List<Score> scores, String courseId, List<Score> missingScores){
		this.name = name;
		this.scores = scores;
		this.courseId = courseId;	
		this.missingScores = missingScores;
		this.weight = weight;
	}
	
	public NonProgramCourse(double weight, String name, List<Score> scores, String courseId, String uid, List<Score> missingScores){
		this.name = name;
		this.scores = scores;
		this.courseId = courseId;	
		this.missingScores = missingScores;
		this.weight = weight;
		this.courseBatchId = uid;
	}
	
	public NonProgramCourse(double weight, String name, List<Score> scores, String courseId, String uid, List<Score> missingScores, String instructorEmail){
		this.name = name;
		this.scores = scores;
		this.courseId = courseId;	
		this.missingScores = missingScores;
		this.weight = weight;
		this.courseBatchId = uid;
		this.instructorEmail = instructorEmail;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
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
