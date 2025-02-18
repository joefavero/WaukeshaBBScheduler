/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.web.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.obsidiansoln.blackboard.group.GroupProxy;
import com.obsidiansoln.blackboard.model.Grades;
import com.obsidiansoln.blackboard.model.StudentData;
import com.obsidiansoln.database.model.ICEnrollment;
import com.obsidiansoln.util.RestManager;

import jakarta.servlet.ServletContext;

@Service
public class AsyncService {

	private static Logger mLog = LoggerFactory.getLogger(AsyncService.class);
	private BBSchedulerService m_service = null;
	@Autowired
	ServletContext servletContext;

	public AsyncService() {
		m_service = new BBSchedulerService();
	}

	@Async("asyncExecutor1")
	@ResponseBody
	public CompletableFuture<List<String>> processEnrollment(String p_courseId, ICEnrollment p_enrollment, HashMap<String,GroupProxy> p_list) throws InterruptedException {
		mLog.trace("In procesEnrollment()");
		List<String> l_returnList = new ArrayList<String>();;

		RestManager l_manager = null;
		try {
	
			l_manager = new RestManager(m_service.getConfigData());
			//mLog.info("Adding Course Enrollment: " + p_enrollment.getUsername());
		    l_manager.createMembership(p_courseId, p_enrollment.getUsername(), "Student");
		    //mLog.info("Adding Group Enrollment: " + p_enrollment.getUsername());
			l_manager.createGroupMembership(p_courseId, p_enrollment, p_list);
			
		} catch (Exception e) {
			mLog.error("An error occurred generating admin results", e);
		}

		return CompletableFuture.completedFuture(l_returnList);
	}
	

	@Async("asyncExecutor1")
	@ResponseBody
	public CompletableFuture<List<StudentData>> processCourse(String p_courseId, boolean p_includeUnavailable, RestManager p_manager) throws InterruptedException {
		mLog.trace("In procesCourse()" + p_courseId);
		HashMap<String, StudentData> l_studentList = new HashMap<String, StudentData>();
		try {
			List<StudentData> l_students = p_manager.getMembershipStudents(p_courseId, p_includeUnavailable);
			for (StudentData l_student : l_students) {
				l_studentList.put(l_student.getId(), l_student);
			}

		} catch (Exception e) {
			mLog.error("An error occurred generating admin results", e);
		}
		
		Collection<StudentData> l_values = l_studentList.values();
		List<StudentData> l_newList = new ArrayList<>(l_values);


		return CompletableFuture.completedFuture(l_newList);
	}

}