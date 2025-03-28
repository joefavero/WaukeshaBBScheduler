package com.obsidiansoln.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.obsidiansoln.blackboard.sis.SnapshotFileManager;
import com.obsidiansoln.database.dao.InfiniteCampusDAO;
import com.obsidiansoln.database.model.ICBBEnrollment;
import com.obsidiansoln.database.model.ICBBGroup;
import com.obsidiansoln.database.model.ICGuardian;
import com.obsidiansoln.database.model.ICStaff;
import com.obsidiansoln.database.model.ICUser;
import com.obsidiansoln.util.RestManager;
import com.obsidiansoln.web.model.ConfigData;
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
		
		String l_file = l_manager.createStudentFile(l_students);
		if (l_file != null) {
			l_manager.sendFile(l_file, "person", 1, l_students.size());
		} else {
			mLog.error("Error: " + "Unable to create Snapshot File");
		}
	}
	
	public void syncStaff() {
		mLog.trace("In syncStaff() ...");

		mLog.info ("Starting to Sync the Staffbetween Infinite Campus and Blackboard");
		SnapshotFileManager l_manager = new SnapshotFileManager();
			
		// Get Staff
		List<ICStaff> l_staffs = dao.getSISStaff();
		mLog.info("Number of Staff: " + l_staffs.size());
		
		String l_file = l_manager.createStaffFile(l_staffs);
		if (l_file != null) {
			l_manager.sendFile(l_file, "person", 2, l_staffs.size());
		} else {
			mLog.error("Error: " + "Unable to create Snapshot File");
		}
	}
	
	public void syncGuardians() {
		mLog.trace("In syncGuardians() ...");

		mLog.info ("Starting to Sync the Guardians between Infinite Campus and Blackboard");
		SnapshotFileManager l_manager = new SnapshotFileManager();
			
		// Get Guardians
		List<ICGuardian> l_guardians= dao.getSISGuardians();
		mLog.info("Number of Guardians: " + l_guardians.size());
		
		String l_file = l_manager.createGuardianFile(l_guardians);
		if (l_file != null) {
			l_manager.sendFile(l_file, "person", 3, l_guardians.size());
		} else {
			mLog.error("Error: " + "Unable to create Snapshot File");
		}
	}
	
	public void syncEnrollments() {
		mLog.trace("In syncEnrollments() ...");

		mLog.info ("Starting to Sync the Enrollments between Infinite Campus and Blackboard");
		SnapshotFileManager l_manager = new SnapshotFileManager();
		
		List<ICBBEnrollment> l_enrollments = dao.getBBEnrollments();

		String l_file = l_manager.createEnrollmentFile(l_enrollments);
		if (l_file != null) {
			l_manager.sendFile(l_file, "membership", 1, l_enrollments.size());
		} else {
			mLog.error("Error: " + "Unable to create Snapshot File");
		}
	}
	
	public void syncGroups() {
		mLog.trace("In syncGroups() ...");

		mLog.info ("Starting to Sync the Groups between Infinite Campus and Blackboard");
		List<ICBBGroup> l_groups = dao.getBBGroups();
		ConfigData l_configData;
		for (ICBBGroup l_group:l_groups) {
			try {
				l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);
				l_manager.createGroupMembership(l_group);
			} catch (Exception e) {
				mLog.error("ERROR: ", e);
			}
		}
	}
}
