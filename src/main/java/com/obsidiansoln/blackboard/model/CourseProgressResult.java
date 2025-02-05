/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.model;

/**
 * A model to pass to the UI for non progress course results
 * @author Nick
 *
 */
public class CourseProgressResult {
	private double progressPercent;
	private String name;
	private double hours;
	
	public CourseProgressResult(){
		
	}
	
	public CourseProgressResult(String name, double percent){
		this.name = name;
		this.progressPercent = percent;
		this.hours = 0.0;
	}	
	
	public CourseProgressResult(String name, double percent, double hours){
		this.name = name;
		this.progressPercent = percent;
		this.hours = hours;
	}	
	
	public double getProgressPercent() {
		return progressPercent;
	}
	public void setProgressPercent(double progressPercent) {
		this.progressPercent = progressPercent;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public double getHours() {
		return hours;
	}

	public void setHours(double hours) {
		this.hours = hours;
	}
	
}
