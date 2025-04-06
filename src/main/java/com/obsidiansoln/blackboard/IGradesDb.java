/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.blackboard;

import java.sql.Timestamp;
import java.util.List;

import com.obsidiansoln.blackboard.model.Grades;
import com.obsidiansoln.blackboard.model.StudentData;
import com.obsidiansoln.blackboard.term.TermProxy;



public interface IGradesDb {
	List<Grades> getByUserId(Timestamp startTime, Timestamp endTime, String userId, String[] lmsTerms, boolean includeMissing);
	List<Grades> getByCourseId(Timestamp startTime, Timestamp endTime, String courseId, String[] lmsTerms);
	List<Grades> getByCourseAndUserId(Timestamp startTime, Timestamp endTime, String userId, String courseId, String[] lmsTerms);
	List<String> getByClassName(String className, boolean includeUnavailable);
	List<String> getByCategory(String category, boolean includeUnavailable);
	List<String> getByCourseId(String category, boolean includeUnavailable);
	List<String> getByStudentName(String studentName, boolean includeUnavailable);
	List<String> getByTeacherID(String teacherName, boolean includeUnavailable);
	List<String> getByStudentID(String studentID, boolean includeUnavailable);
	List<String> getByClassName(String className, boolean includeUnavailable, String[] lmsTerm);
	List<String> getByCategory(String category, boolean includeUnavailable, String[] lmsTerm);
	List<String> getByCourseId(String category, boolean includeUnavailable, String[] lmsTerm);
	List<String> getByStudentName(String studentName, boolean includeUnavailable, String[] lmsTerm);
	List<String> getByTeacherID(String teacherName, boolean includeUnavailable, String[] lmsTerm);
	List<String> getByStudentID(String studentID, boolean includeUnavailable, String[] lmsTerm);
	List<String> getByUserName(String userName, boolean includeUnavailable, String[] lmsTerm);
	List<StudentData> getUsersByCourseId(String courseId, boolean includeUnavailable, String[] lmsTerm);
	List<StudentData> getUsersByStudentId(String studentId, boolean includeUnavailable, String[] lmsTerm);
	List<StudentData> getUsersByLocation(String location, boolean includeUnavailable, String[] lmsTerm);
	List<StudentData> getUsersByTeacher(String teacherId, boolean includeUnavailable, String[] lmsTerm);
	List<StudentData> getUsersByTerms(boolean includeUnavailable, String[] lmsTerm);
	List<StudentData> getUsersByUserId(String userId, boolean includeUnavailable, String[] lmsTerm);
	List<StudentData> getUsersByStudentName(String studentName, boolean includeUnavailable, String[] lmsTerm);
	List<StudentData> getUsersByClassName(String className, boolean includeUnavailable, String[] lmsTerm);
	List<StudentData> getUsersByObserver(String className, boolean includeUnavailable, String[] lmsTerm);
	List<String> getByTerm(String[] term, boolean includeUnavailable);
	List<TermProxy> getTerms();
}
