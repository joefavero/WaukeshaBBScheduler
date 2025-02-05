/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.algorithm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obsidiansoln.blackboard.model.NonProgramCourse;

public class ProgressCalculator {
	private static final Logger log = LoggerFactory.getLogger(ProgressCalculator.class);

	/**
	 * Calculates the received percentage for a given progress area by dividing
	 * total score by possible score
	 * 
	 * @param progressArea - The Progress area with scores to do the calculations
	 *                     for
	 * @return The percentage received or 0 if there are no scores
	 */
	public double calculatePercentage(ProgressArea progressArea) {
		log.trace("In calculatePercentage() ...");
		if (progressArea.getScores() == null)
			return 0;
		double possible = 0;
		double total = 0;

		for (Score score : progressArea.getScores()) {
			if (score.getStatus().equalsIgnoreCase("Calculated")) {
				possible += score.getPossible();
				total += score.getScore();
			}
		}
		// Make sure we don't divide by 0
		if (possible == 0)
			return 0.00;
		
		return total / possible;
	}

	/**
	 * This calculates the total progress for an individual based on a list of
	 * progressAreas that they have
	 * 
	 * @param progressAreas A list of courses with weights and grades
	 * @return The overall percentage currently for the given areas
	 */
	public double calculate(List<ProgressArea> progressAreas) {
		log.trace("In calculateList() ...");
		double denominator = 0;
		double numerator = 0;
		for (ProgressArea progressArea : progressAreas) {
			if (progressArea.getScores() != null && progressArea.getScores().size() > 0) {
				denominator += progressArea.getWeight();
				numerator += (calculatePercentage(progressArea) * progressArea.getWeight());
			}
		}
		// Don't divide by 0 check
		if (denominator == 0)
			return 0;
		return numerator / denominator;
	}

	/**
	 * This calculates the total progress as a percentage for an individual based on
	 * a list of progressAreas that they have
	 * 
	 * @param progressAreas A list of courses with weights and grades
	 * @return The overall percentage currently for the given areas
	 */
	public double calculateAsPercent(List<ProgressArea> progressAreas) {
		log.trace("In calculateAsPercentList() ...");
		double denominator = 0;
		double numerator = 0;
		for (ProgressArea progressArea : progressAreas) {
			if (progressArea.getScores() != null && progressArea.getScores().size() > 0) {
				denominator += progressArea.getWeight();
				numerator += (calculatePercentage(progressArea) * progressArea.getWeight());
			}
		}
		// Don't divide by 0 check
		if (denominator == 0)
			return 0.00;
		return (double) Math.floor(numerator / denominator * 10000) / 100;
	}

	public double calculateNonProgramCourse(NonProgramCourse course) {
		log.trace("In calculateNonPorgramCourse() ...");
		double denominator = 0;
		double numerator = 0;
		for (Score score : course.getScores()) {
			if (score.getStatus().equalsIgnoreCase("Calculated")) {
				denominator += score.getPossible();
				numerator += score.getScore();
			}
		}
		if (denominator == 0)
			return 0.00;
		return (double) Math.floor(numerator / denominator * 10000) / 100;
	}

}
