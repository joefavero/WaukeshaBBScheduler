/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.model;

/**
 * A representation of a Tulsa Course Detail
 * @author Nick
 *
 */
public class TulsaCourseDetails {
	private boolean isProgressCourse;
	private double weight;
	
	public boolean isProgressCourse() {
		return isProgressCourse;
	}
	public void setProgressCourse(boolean isProgressCourse) {
		this.isProgressCourse = isProgressCourse;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	
	/**
	 * Parses a Course Id and creates the appropriate TulsaCourseDetails object
	 * @param courseId The Course Id to parse
	 * @return a new instance of Tulsa Course Details
	 */
	public static TulsaCourseDetails fromCourseName(String courseId){
		TulsaCourseDetails details = new TulsaCourseDetails();
		if(courseId == null) return details;
		String [] tokens = courseId.split("-");
		if(tokens.length != 5) return null;
		details.setProgressCourse(tokens[1].toLowerCase().contains("ocas")==false);
		details.setWeight(Double.parseDouble(tokens[4]));		//
		
		return details;
	}
	
	
}
