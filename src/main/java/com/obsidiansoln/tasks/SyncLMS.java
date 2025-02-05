package com.obsidiansoln.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.obsidiansoln.blackboard.sis.SnapshotFileManager;
import com.obsidiansoln.database.dao.InfiniteCampusDAO;
import com.obsidiansoln.database.model.ICStaff;
import com.obsidiansoln.database.model.ICUser;


@Component("BBSchedulerTasks")
public class SyncLMS {

	private static Logger mLog = LoggerFactory.getLogger(SyncLMS.class);
	@Autowired
	private InfiniteCampusDAO dao;

	public SyncLMS() {
		super();
	}

	public void syncUsers() {
		mLog.trace("In syncUsers() ...");

		mLog.info ("Starting to Sync the Users between Infinite Campus and Blackboard");
		SnapshotFileManager l_manager = new SnapshotFileManager();
		
		// Get Students
		List<ICUser> l_students = dao.getSISStudents();
		mLog.info("Number of Students: " + l_students.size());
		
		// Get Staff
		
		// Add Staff
		List<ICStaff> l_staffs = dao.getSISStaff();
		mLog.info("Number of Staff: " + l_staffs.size());
		
		String l_file = l_manager.createFile(l_students, l_staffs);
		if (l_file != null) {
			l_manager.sendFile(l_file);
		} else {
			mLog.error("Error: " + "Unable to create Snapshot File");
		}


	}
}
