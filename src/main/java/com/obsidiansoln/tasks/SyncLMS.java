/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.obsidiansoln.blackboard.model.SnapshotFileInfo;
import com.obsidiansoln.blackboard.sis.SnapshotFileManager;
import com.obsidiansoln.database.dao.InfiniteCampusDAO;
import com.obsidiansoln.database.model.ICBBEnrollment;
import com.obsidiansoln.database.model.ICBBGroup;
import com.obsidiansoln.database.model.ICGuardian;
import com.obsidiansoln.database.model.ICStaff;
import com.obsidiansoln.database.model.ICUser;
import com.obsidiansoln.util.EmailManager;
import com.obsidiansoln.util.RestManager;
import com.obsidiansoln.web.model.ConfigData;
import com.obsidiansoln.web.model.ContactModel;
import com.obsidiansoln.web.service.BBSchedulerService;


@Component("BBSchedulerTasks")
public class SyncLMS {

	private static Logger mLog = LoggerFactory.getLogger(SyncLMS.class);
	private BBSchedulerService m_service = null;
	@Autowired
	private InfiniteCampusDAO dao;

	public SyncLMS() {
		super();
		m_service = new BBSchedulerService();
	}

	public void syncUsers() {
		mLog.trace("In syncUsers() ...");

		mLog.info ("Starting to Sync the Users between Infinite Campus and Blackboard");
		SnapshotFileManager l_manager = new SnapshotFileManager();

		// Get Students
		List<ICUser> l_students = dao.getSISStudents();
		mLog.info("Number of Students: " + l_students.size());

		List<SnapshotFileInfo> l_files = l_manager.createStudentFile(l_students);
		for (SnapshotFileInfo l_file:l_files) {
			l_manager.sendFile(l_file);
		} 
	}

	public void syncStaff() {
		mLog.trace("In syncStaff() ...");

		mLog.info ("Starting to Sync the Staffbetween Infinite Campus and Blackboard");
		SnapshotFileManager l_manager = new SnapshotFileManager();

		// Get Staff
		List<ICStaff> l_staffs = dao.getSISStaff();
		mLog.info("Number of Staff: " + l_staffs.size());

		List<SnapshotFileInfo> l_files = l_manager.createStaffFile(l_staffs);
		for (SnapshotFileInfo l_file:l_files) {
			l_manager.sendFile(l_file);
		} 
	}

	public void syncGuardians() {
		mLog.trace("In syncGuardians() ...");

		mLog.info ("Starting to Sync the Guardians between Infinite Campus and Blackboard");
		SnapshotFileManager l_manager = new SnapshotFileManager();

		// Get Guardians
		List<ICGuardian> l_guardians= dao.getSISGuardians();
		mLog.info("Number of Guardians: " + l_guardians.size());

		List<SnapshotFileInfo> l_files = l_manager.createGuardianFile(l_guardians);
		for (SnapshotFileInfo l_file:l_files) {
			l_manager.sendFile(l_file);
		}
	}

	public void syncEnrollments() {
		mLog.trace("In syncEnrollments() ...");

		mLog.info ("Starting to Sync the Enrollments between Infinite Campus and Blackboard");
		SnapshotFileManager l_manager = new SnapshotFileManager();

		List<ICBBEnrollment> l_enrollments = dao.getBBEnrollments();

		List<SnapshotFileInfo> l_files = l_manager.createEnrollmentFile(l_enrollments);
		for (SnapshotFileInfo l_file:l_files) {
			l_manager.sendFile(l_file);
		} 
	}

	public void syncGroups() {
		mLog.trace("In syncGroups() ...");

		mLog.info ("Starting to Sync the Groups between Infinite Campus and Blackboard");
		List<ICBBGroup> l_groups = dao.getBBGroups();
		ConfigData l_configData = null;
		String l_message;
		for (ICBBGroup l_group:l_groups) {
			try {
				l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);
				l_manager.createGroupMembership(l_group);
			} catch (Exception e) {
				mLog.error("ERROR: ", e);
				l_message = "ERRROR IN BB Scheduler Group Sync" + "<br>";

			}
		}

		l_message = "BB Scheduler Group Sync Finished " + "<br>";



		// Send Email 
		if (l_configData != null) {
			sendEmail(l_configData.getEmailFrom(), l_configData.getSnapshotEmail(), "Infinite Campus Integration", l_message);
		}
	}

	private void sendEmail(String p_from, String p_email, String p_subject, String p_message) {
		mLog.info("In sendEmail() ...");
		// Now Send Email
		try {
			mLog.info("Sending Email: ");
			EmailManager l_email = new EmailManager();
			ContactModel l_contact = new ContactModel();
			l_contact.setEmail(p_email);
			l_contact.setSubject(p_subject);
			l_contact.setMessage(p_message);
			l_email.sendEmail(p_from, p_email, l_contact);
		} catch (Exception l_ex) {
			mLog.error("ERROR", l_ex);
		}
	}
}
