/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.web.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.obsidiansoln.blackboard.group.GroupProxy;
import com.obsidiansoln.blackboard.model.SnapshotFileInfo;
import com.obsidiansoln.blackboard.model.StudentData;
import com.obsidiansoln.blackboard.sis.SnapshotFileManager;
import com.obsidiansoln.database.model.ICBBGroup;
import com.obsidiansoln.database.model.ICEnrollment;
import com.obsidiansoln.util.EmailManager;
import com.obsidiansoln.util.RestManager;
import com.obsidiansoln.web.model.ContactModel;
import com.obsidiansoln.web.model.ToastMessage;

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
			if (p_enrollment != null && p_enrollment.getRole().equals("Student")) {
				l_manager.createMembership(p_courseId, p_enrollment.getUsername(), p_enrollment.getRole());
				l_manager.createGroupMembership(p_courseId, p_enrollment, p_list);
			} else {
				l_manager.createMembership(p_courseId, p_enrollment.getUsername(), p_enrollment.getRole());
			}
		
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
	
	@Async("asyncExecutor2")
	@ResponseBody
	public void processSISFile (SnapshotFileInfo p_file,  SnapshotFileManager p_manager) throws InterruptedException {
		mLog.trace("In procesSISFile()" + p_file);
		p_manager.sendFile(p_file);

	}
	
	@Async("asyncExecutor2")
	@ResponseBody
	public void processSISGroups (List<ICBBGroup> p_groups, RestManager p_manager) throws InterruptedException {
		mLog.trace("In procesSISGroups()");
		for (ICBBGroup l_group:p_groups) {
			try {
				p_manager.createGroupMembership(l_group);
			} catch (Exception e) {
				mLog.error("Error: ", e);
			}
		}
	}
}