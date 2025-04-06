/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.web.service;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obsidiansoln.util.BBSchedulerUtil;
import com.obsidiansoln.util.PropertiesBean;
import com.obsidiansoln.web.model.ConfigData;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class BBSchedulerService {

	private static Logger mLog = LoggerFactory.getLogger(BBSchedulerService.class);
	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm");

	private String formatDate(Timestamp l_ts) {
		if (l_ts == null)
			return "&nbsp;";

		return DATE_FORMAT.format(l_ts);
	}


	public ConfigData getConfigData() throws Exception {
		SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Properties l_props = PropertiesBean.getInstance().getProperties();

		String l_logLevel = l_props.getProperty(BBSchedulerUtil.LOG_LEVEL);
		if (l_logLevel== null) {
			l_logLevel= "DEBUG";
		}
		ch.qos.logback.classic.Logger logger = getLogger();
		if (logger != null)
		{
			mLog.debug("Setting log verbosity to [{}]", l_logLevel);
			logger.setLevel(Level.valueOf(l_logLevel));
		}
		String l_workingDirectory = l_props.getProperty(BBSchedulerUtil.WORKING_DIRECTORY);
		if ( l_workingDirectory== null) {
			l_workingDirectory= "/var/log";
		}
		
		String l_apiKey = l_props.getProperty(BBSchedulerUtil.API_KEY);
		if ( l_apiKey== null) {
			l_apiKey = "test";
		}
		
		String l_adminPW = l_props.getProperty(BBSchedulerUtil.ADMIN_PW);
		if ( l_adminPW== null) {
			l_adminPW = "NEED TO SET ADMIN PW";
		}
		
		
		String l_ltiKey = l_props.getProperty(BBSchedulerUtil.LTI_KEY);
		if ( l_ltiKey== null) {
			l_ltiKey = "test";
		}
		
		String l_ltiSecret = l_props.getProperty(BBSchedulerUtil.LTI_SECRET);
		if ( l_ltiSecret== null) {
			l_ltiSecret = "test";
		}
		
		String l_restHost = l_props.getProperty(BBSchedulerUtil.REST_HOST);
		if ( l_restHost== null) {
			l_restHost= "localhost";
		}

		String l_restKey = l_props.getProperty(BBSchedulerUtil.REST_KEY);
		if ( l_restKey== null) {
			l_restKey= "NEED TO SET REST KEY";
		}

		String l_restSecret = l_props.getProperty(BBSchedulerUtil.REST_SECRET);
		if ( l_restSecret== null) {
			l_restSecret = "NEED TO SET REST SECRET";
		}

		String l_emailFrom = l_props.getProperty(BBSchedulerUtil.EMAIL_FROM);
		if ( l_emailFrom == null) {
			l_emailFrom= "";
		}
		
		String l_emailHost = l_props.getProperty(BBSchedulerUtil.EMAIL_HOST);
		if ( l_emailHost == null) {
			l_emailHost= "smtp.gmail.com";
		}
		
		String l_emailPort = l_props.getProperty(BBSchedulerUtil.EMAIL_PORT);
		if ( l_emailPort == null) {
			l_emailPort= "465";
		}
		String l_emailUsername = l_props.getProperty(BBSchedulerUtil.EMAIL_USERNAME);
		if ( l_emailUsername == null) {
			l_emailUsername= "joefavero@gmail.com";
		}
		String l_emailPassword = l_props.getProperty(BBSchedulerUtil.EMAIL_PASSWORD);
		if ( l_emailPassword == null) {
			l_emailPassword= "dtxfdxsvzszquwgh";
		}
		
		boolean l_isEmailAuthenticate = true;
		if ( l_props.getProperty(BBSchedulerUtil.EMAIL_AUTHENTICATE) != null) {
			l_isEmailAuthenticate= l_props.getProperty(BBSchedulerUtil.EMAIL_AUTHENTICATE).equals("true") ? true : false;
		} 
		
		boolean l_isEmailUseSSL = true;
		if ( l_props.getProperty(BBSchedulerUtil.EMAIL_USE_SSL) != null) {
			l_isEmailUseSSL = l_props.getProperty(BBSchedulerUtil.EMAIL_USE_SSL).equals("true") ? true : false;
		} 
		
		boolean l_isEmailDebug = true;
		if ( l_props.getProperty(BBSchedulerUtil.EMAIL_DEBUG) != null) {
			l_isEmailDebug= l_props.getProperty(BBSchedulerUtil.EMAIL_DEBUG).equals("true") ? true : false;
		} 
		
		String l_snapshotBbInstanceId = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_BB_INSTANCE_ID);
		if ( l_snapshotBbInstanceId == null) {
			l_snapshotBbInstanceId= "";
		}
		
		String l_snapshotStudentSharedUsername = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_STUDENT_SHARED_USERNAME);
		if ( l_snapshotStudentSharedUsername == null) {
			l_snapshotStudentSharedUsername= "";
		}
		
		String l_snapshotStudentSharedPassword = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_STUDENT_SHARED_PASSWORD);
		if ( l_snapshotStudentSharedPassword == null) {
			l_snapshotStudentSharedPassword= "";
		}
		
		String l_snapshotStudentDatasource = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_STUDENT_DATASOURCE);
		if ( l_snapshotStudentDatasource == null) {
			l_snapshotStudentDatasource = "";
		}
		
		String l_snapshotStaffSharedUsername = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_STAFF_SHARED_USERNAME);
		if ( l_snapshotStaffSharedUsername == null) {
			l_snapshotStaffSharedUsername= "";
		}
		
		String l_snapshotStaffSharedPassword = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_STAFF_SHARED_PASSWORD);
		if ( l_snapshotStaffSharedPassword == null) {
			l_snapshotStaffSharedPassword= "";
		}
		
		String l_snapshotStaffDatasource = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_STAFF_DATASOURCE);
		if ( l_snapshotStaffDatasource == null) {
			l_snapshotStaffDatasource = "";
		}
		
		String l_snapshotGuardianSharedUsername = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_GUARDIAN_SHARED_USERNAME);
		if ( l_snapshotGuardianSharedUsername == null) {
			l_snapshotGuardianSharedUsername= "";
		}
		
		String l_snapshotGuardianSharedPassword = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_GUARDIAN_SHARED_PASSWORD);
		if ( l_snapshotGuardianSharedPassword == null) {
			l_snapshotGuardianSharedPassword= "";
		}
		
		String l_snapshotGuardianDatasource = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_GUARDIAN_DATASOURCE);
		if ( l_snapshotGuardianDatasource == null) {
			l_snapshotGuardianDatasource = "";
		}
		
		String l_snapshotEnrollmentDatasource = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_ENROLLMENT_DATASOURCE);
		if ( l_snapshotEnrollmentDatasource == null) {
			l_snapshotEnrollmentDatasource = "";
		}
		String l_snapshotStudentAssociationDatasource = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_STUDENT_ASSOCIATION_DATASOURCE);
		if ( l_snapshotStudentAssociationDatasource == null) {
			l_snapshotStudentAssociationDatasource = "";
		}
		String l_snapshotStaffAssociationDatasource = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_STAFF_ASSOCIATION_DATASOURCE);
		if ( l_snapshotStaffAssociationDatasource == null) {
			l_snapshotStaffAssociationDatasource = "";
		}
		String l_snapshotGuardianAssociationDatasource = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_GUARDIAN_ASSOCIATION_DATASOURCE);
		if ( l_snapshotGuardianAssociationDatasource == null) {
			l_snapshotGuardianAssociationDatasource = "";
		}
		String l_snapshotEmail = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_EMAIL);
		if ( l_snapshotEmail == null) {
			l_snapshotEmail= "";
		}
		
		
		ConfigData l_data = new ConfigData(l_logLevel,
				l_workingDirectory,
				l_apiKey,
				l_adminPW,
				l_ltiKey,
				l_ltiSecret,
				l_restHost,
				l_restKey,
				l_restSecret,
				l_emailFrom,
				l_emailHost,
				l_emailPort,
				l_emailUsername,
				l_emailPassword,
				l_isEmailAuthenticate,
				l_isEmailUseSSL,
				l_isEmailDebug,
				l_snapshotBbInstanceId,
				l_snapshotStudentSharedUsername,
				l_snapshotStudentSharedPassword,
				l_snapshotStudentDatasource,
				l_snapshotStaffSharedUsername,
				l_snapshotStaffSharedPassword,
				l_snapshotStaffDatasource,
				l_snapshotGuardianSharedUsername,
				l_snapshotGuardianSharedPassword,
				l_snapshotGuardianDatasource,
				l_snapshotEnrollmentDatasource,
				l_snapshotStudentAssociationDatasource,
				l_snapshotStaffAssociationDatasource,
				l_snapshotGuardianAssociationDatasource,
				l_snapshotEmail);

		return l_data;
	}
	
	public void saveConfigData(ConfigData p_data) throws Exception {
		SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		PropertiesBean l_pbean = PropertiesBean.getInstance();
		Properties l_props = PropertiesBean.getInstance().getProperties();

		l_props.setProperty(BBSchedulerUtil.LOG_LEVEL, p_data.getLogLevel());
		ch.qos.logback.classic.Logger logger = getLogger();
		if (logger != null)
		{
			mLog.debug("Setting log verbosity to [{}]", p_data.getLogLevel());
			logger.setLevel(Level.valueOf(p_data.getLogLevel()));
		}
		
		l_props.setProperty(BBSchedulerUtil.WORKING_DIRECTORY, p_data.getWorkingDirectory());
		

		l_props.setProperty(BBSchedulerUtil.API_KEY, p_data.getApiKey());
		l_props.setProperty(BBSchedulerUtil.ADMIN_PW, p_data.getAdminPW());
		l_props.setProperty(BBSchedulerUtil.LTI_KEY, p_data.getLtiKey());
		l_props.setProperty(BBSchedulerUtil.LTI_SECRET, p_data.getLtiSecret());
		l_props.setProperty(BBSchedulerUtil.REST_HOST, p_data.getRestHost());
		l_props.setProperty(BBSchedulerUtil.REST_KEY, p_data.getRestKey());
		l_props.setProperty(BBSchedulerUtil.REST_SECRET, p_data.getRestSecret());
		l_props.setProperty(BBSchedulerUtil.EMAIL_FROM, p_data.getEmailFrom());
		l_props.setProperty(BBSchedulerUtil.EMAIL_HOST, p_data.getEmailHost());
		l_props.setProperty(BBSchedulerUtil.EMAIL_PORT, p_data.getEmailPort());
		l_props.setProperty(BBSchedulerUtil.EMAIL_USERNAME, p_data.getEmailUsername());
		l_props.setProperty(BBSchedulerUtil.EMAIL_PASSWORD, p_data.getEmailPassword());
		l_props.setProperty(BBSchedulerUtil.EMAIL_HOST, p_data.getEmailHost());
		if ( p_data.isEmailAuthenticate()) {
			l_props.setProperty(BBSchedulerUtil.EMAIL_AUTHENTICATE, "true");
		} else {
			l_props.setProperty(BBSchedulerUtil.EMAIL_AUTHENTICATE, "false");
		}
		if ( p_data.isEmailUseSSL()) {
			l_props.setProperty(BBSchedulerUtil.EMAIL_USE_SSL, "true");
		} else {
			l_props.setProperty(BBSchedulerUtil.EMAIL_USE_SSL, "false");
		}
		if ( p_data.isEmailDebug()) {
			l_props.setProperty(BBSchedulerUtil.EMAIL_DEBUG, "true");
		} else {
			l_props.setProperty(BBSchedulerUtil.EMAIL_DEBUG, "false");
		}
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_BB_INSTANCE_ID, p_data.getSnapshotBbInstanceId());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_STUDENT_SHARED_USERNAME, p_data.getSnapshotStudentSharedUsername());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_STUDENT_SHARED_PASSWORD, p_data.getSnapshotStudentSharedPassword());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_STUDENT_DATASOURCE, p_data.getSnapshotStudentDatasource());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_STAFF_SHARED_USERNAME, p_data.getSnapshotStaffSharedUsername());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_STAFF_SHARED_PASSWORD, p_data.getSnapshotStaffSharedPassword());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_STAFF_DATASOURCE, p_data.getSnapshotStaffDatasource());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_GUARDIAN_SHARED_USERNAME, p_data.getSnapshotGuardianSharedUsername());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_GUARDIAN_SHARED_PASSWORD, p_data.getSnapshotGuardianSharedPassword());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_GUARDIAN_DATASOURCE, p_data.getSnapshotGuardianDatasource());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_ENROLLMENT_DATASOURCE, p_data.getSnapshotEnrollmentDatasource());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_STUDENT_ASSOCIATION_DATASOURCE, p_data.getSnapshotStudentAssociationDatasource());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_STAFF_ASSOCIATION_DATASOURCE, p_data.getSnapshotStaffAssociationDatasource());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_GUARDIAN_ASSOCIATION_DATASOURCE, p_data.getSnapshotGuardianAssociationDatasource());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_EMAIL, p_data.getSnapshotEmail());


		l_pbean.writeProperties(l_props);
	}
	
	private ch.qos.logback.classic.Logger getLogger()
	{
		LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
		return context.getLogger("com.obsidiansoln");
	}
	
}
