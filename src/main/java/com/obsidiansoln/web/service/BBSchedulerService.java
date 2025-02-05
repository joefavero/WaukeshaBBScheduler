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

		Timestamp l_semester1StartTimestamp = null;
		if (l_props.getProperty(BBSchedulerUtil.SEMESTER1_START_DATE) != null && !(l_props.getProperty(BBSchedulerUtil.SEMESTER1_START_DATE).isEmpty())) {
			l_sdf.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
			Date l_semester1StartDate = l_sdf.parse(l_props.getProperty(BBSchedulerUtil.SEMESTER1_START_DATE));
			l_semester1StartTimestamp = new Timestamp(l_semester1StartDate.getTime());
		}

		Timestamp l_semester1EndTimestamp = null;
		if (l_props.getProperty(BBSchedulerUtil.SEMESTER1_END_DATE) != null && !(l_props.getProperty(BBSchedulerUtil.SEMESTER1_END_DATE).isEmpty())) {
			l_sdf.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
			Date l_semester1EndDate = l_sdf.parse(l_props.getProperty(BBSchedulerUtil.SEMESTER1_END_DATE));
			l_semester1EndTimestamp = new Timestamp(l_semester1EndDate.getTime());
		}

		Timestamp l_semester2StartTimestamp = null;
		if (l_props.getProperty(BBSchedulerUtil.SEMESTER2_START_DATE) != null && !(l_props.getProperty(BBSchedulerUtil.SEMESTER2_START_DATE).isEmpty())) {
			l_sdf.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
			Date l_semester2StartDate = l_sdf.parse(l_props.getProperty(BBSchedulerUtil.SEMESTER2_START_DATE));
			l_semester2StartTimestamp = new Timestamp(l_semester2StartDate.getTime());
		}

		Timestamp l_semester2EndTimestamp = null;
		if (l_props.getProperty(BBSchedulerUtil.SEMESTER2_END_DATE) != null && !(l_props.getProperty(BBSchedulerUtil.SEMESTER2_END_DATE).isEmpty())) {
			l_sdf.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
			Date l_semester2EndDate = l_sdf.parse(l_props.getProperty(BBSchedulerUtil.SEMESTER2_END_DATE));
			l_semester2EndTimestamp = new Timestamp(l_semester2EndDate.getTime());
		}
		String l_defaultSemester = l_props.getProperty(BBSchedulerUtil.DEFAULT_SEMESTER);
		if ( l_defaultSemester== null) {
			l_defaultSemester= "Semester 1";
		}
		String l_defaultTerm = l_props.getProperty(BBSchedulerUtil.DEFAULT_TERM);
		if ( l_defaultTerm== null) {
			l_defaultTerm= "ALL";
		}
		String l_maxReportDays = l_props.getProperty(BBSchedulerUtil.MAX_REPORT_DAYS);
		if ( l_maxReportDays== null) {
			l_maxReportDays= "30";
		}
		
		boolean l_isDetailed = false;
		if ( l_props.getProperty(BBSchedulerUtil.PARENT_TOOL_LEVEL) != null) {
			l_isDetailed = l_props.getProperty(BBSchedulerUtil.PARENT_TOOL_LEVEL).equals("true") ? true : false;
		} 
		
		String l_adminReportInstructor = l_props.getProperty(BBSchedulerUtil.ADMIN_REPORT_INSTRUCTOR);
		if ( l_adminReportInstructor == null) {
			l_adminReportInstructor= "";
		}
		
		String l_adminReportEmail = l_props.getProperty(BBSchedulerUtil.ADMIN_REPORT_EMAIL);
		if ( l_adminReportEmail == null) {
			l_adminReportEmail= "";
		}
		
		String l_adminReportPhone = l_props.getProperty(BBSchedulerUtil.ADMIN_REPORT_PHONE);
		if ( l_adminReportPhone == null) {
			l_adminReportPhone= "";
		}
		
		String l_maxPDFEntires = l_props.getProperty(BBSchedulerUtil.MAX_PDF_ENTRIES);
		if ( l_maxPDFEntires == null) {
			l_maxPDFEntires= "120";
		}
		
		String l_emailFrom = l_props.getProperty(BBSchedulerUtil.PARENT_EMAIL_FROM);
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
		
		String l_emailNote = l_props.getProperty(BBSchedulerUtil.EMAIL_NOTE);
		if ( l_emailNote == null) {
			l_emailNote= "";
		}
		
		String l_parentInviteMessage = l_props.getProperty(BBSchedulerUtil.PARENT_INVITE_MESSAGE);
		if ( l_parentInviteMessage == null) {
			l_parentInviteMessage= "";
		}
		
		String l_parentExistingMessage = l_props.getProperty(BBSchedulerUtil.PARENT_EXISTING_MESSAGE);
		if ( l_parentExistingMessage == null) {
			l_parentExistingMessage= "";
		}
		
		String l_snapshotBbInstanceId = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_BB_INSTANCE_ID);
		if ( l_snapshotBbInstanceId == null) {
			l_snapshotBbInstanceId= "";
		}
		
		String l_snapshotSharedUsername = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_SHARED_USERNAME);
		if ( l_snapshotSharedUsername == null) {
			l_snapshotSharedUsername= "";
		}
		
		String l_snapshotSharedPassword = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_SHARED_PASSWORD);
		if ( l_snapshotSharedPassword == null) {
			l_snapshotSharedPassword= "";
		}
		
		String l_snapshotEmail = l_props.getProperty(BBSchedulerUtil.SNAPSHOT_EMAIL);
		if ( l_snapshotEmail == null) {
			l_snapshotEmail= "";
		}
		
		
		ConfigData l_data = new ConfigData(l_logLevel,
				l_workingDirectory,
				l_apiKey,
				l_ltiKey,
				l_ltiSecret,
				l_restHost,
				l_restKey,
				l_restSecret,
				l_semester1StartTimestamp, 
				l_semester1EndTimestamp, 
				l_semester2StartTimestamp, 
				l_semester2EndTimestamp, 
				l_defaultSemester, 
				l_defaultTerm, 
				l_maxReportDays, 
				l_isDetailed,
				l_adminReportInstructor,
				l_adminReportEmail,
				l_adminReportPhone,
				l_maxPDFEntires,
				l_emailFrom,
				l_emailHost,
				l_emailPort,
				l_emailUsername,
				l_emailPassword,
				l_isEmailAuthenticate,
				l_isEmailUseSSL,
				l_isEmailDebug,
				l_emailNote,
				l_parentInviteMessage,
				l_parentExistingMessage,
				l_snapshotBbInstanceId,
				l_snapshotSharedUsername,
				l_snapshotSharedPassword,
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
		
		if (p_data.getSemester1StartDate() != null) {
			Calendar l_tempCalendar = Calendar.getInstance();
			l_tempCalendar.setTimeInMillis(p_data.getSemester1StartDate().getTime());
			l_tempCalendar.set(Calendar.MILLISECOND, 0);
			l_tempCalendar.set(Calendar.SECOND, 0);
			l_tempCalendar.set(Calendar.MINUTE, 0);
			l_tempCalendar.set(Calendar.HOUR, 0);
			l_props.setProperty(BBSchedulerUtil.SEMESTER1_START_DATE, l_sdf.format(l_tempCalendar.getTime()));
		} else {
			l_props.setProperty(BBSchedulerUtil.SEMESTER1_START_DATE, "");
		}

		l_props.setProperty(BBSchedulerUtil.API_KEY, p_data.getApiKey());
		l_props.setProperty(BBSchedulerUtil.LTI_KEY, p_data.getLtiKey());
		l_props.setProperty(BBSchedulerUtil.LTI_SECRET, p_data.getLtiSecret());
		l_props.setProperty(BBSchedulerUtil.REST_HOST, p_data.getRestHost());
		l_props.setProperty(BBSchedulerUtil.REST_KEY, p_data.getRestKey());
		l_props.setProperty(BBSchedulerUtil.REST_SECRET, p_data.getRestSecret());
		
		if (p_data.getSemester1EndDate() != null) {
			Calendar l_tempCalendar = Calendar.getInstance();
			l_tempCalendar.setTimeInMillis(p_data.getSemester1EndDate().getTime());
			l_tempCalendar.set(Calendar.HOUR_OF_DAY, 23);
			l_tempCalendar.set(Calendar.MINUTE, 59);
			l_tempCalendar.set(Calendar.SECOND, 59);
			l_tempCalendar.set(Calendar.MILLISECOND, 999);
			l_props.setProperty(BBSchedulerUtil.SEMESTER1_END_DATE, l_sdf.format(l_tempCalendar.getTime()));
		} else  {
			l_props.setProperty(BBSchedulerUtil.SEMESTER1_END_DATE, "");
		}

		if (p_data.getSemester2StartDate() != null) {
			Calendar l_tempCalendar = Calendar.getInstance();
			l_tempCalendar.setTimeInMillis(p_data.getSemester2StartDate().getTime());
			l_tempCalendar.set(Calendar.MILLISECOND, 0);
			l_tempCalendar.set(Calendar.SECOND, 0);
			l_tempCalendar.set(Calendar.MINUTE, 0);
			l_tempCalendar.set(Calendar.HOUR, 0);
			l_props.setProperty(BBSchedulerUtil.SEMESTER2_START_DATE, l_sdf.format(l_tempCalendar.getTime()));
		} else {
			l_props.setProperty(BBSchedulerUtil.SEMESTER2_START_DATE, "");
		}

		if (p_data.getSemester2EndDate() != null) {
			Calendar l_tempCalendar = Calendar.getInstance();
			l_tempCalendar.setTimeInMillis(p_data.getSemester2EndDate().getTime());
			l_tempCalendar.set(Calendar.HOUR_OF_DAY, 23);
			l_tempCalendar.set(Calendar.MINUTE, 59);
			l_tempCalendar.set(Calendar.SECOND, 59);
			l_tempCalendar.set(Calendar.MILLISECOND, 999);
			l_props.setProperty(BBSchedulerUtil.SEMESTER2_END_DATE, l_sdf.format(l_tempCalendar.getTime()));
		} else  {
			l_props.setProperty(BBSchedulerUtil.SEMESTER2_END_DATE, "");
		}

		l_props.setProperty(BBSchedulerUtil.DEFAULT_SEMESTER, p_data.getDefaultSemester());
		l_props.setProperty(BBSchedulerUtil.DEFAULT_TERM, p_data.getDefaultTerm());
		l_props.setProperty(BBSchedulerUtil.MAX_REPORT_DAYS, p_data.getMaxReportDays());
		if ( p_data.isDetailed()) {
			l_props.setProperty(BBSchedulerUtil.PARENT_TOOL_LEVEL, "true");
		} else {
			l_props.setProperty(BBSchedulerUtil.PARENT_TOOL_LEVEL, "false");
		}

		l_props.setProperty(BBSchedulerUtil.ADMIN_REPORT_INSTRUCTOR, p_data.getAdminReportInstructor());
		l_props.setProperty(BBSchedulerUtil.ADMIN_REPORT_EMAIL, p_data.getAdminReportEmail());
		l_props.setProperty(BBSchedulerUtil.ADMIN_REPORT_PHONE, p_data.getAdminReportPhone());
		l_props.setProperty(BBSchedulerUtil.MAX_PDF_ENTRIES, p_data.getMaxPDFEntries());
		l_props.setProperty(BBSchedulerUtil.PARENT_EMAIL_FROM, p_data.getEmailFrom());
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

		l_props.setProperty(BBSchedulerUtil.EMAIL_NOTE, p_data.getEmailNote());
		
		l_props.setProperty(BBSchedulerUtil.PARENT_INVITE_MESSAGE, p_data.getParentInviteMessage());
		
		l_props.setProperty(BBSchedulerUtil.PARENT_EXISTING_MESSAGE, p_data.getParentExistingMessage());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_BB_INSTANCE_ID, p_data.getSnapshotBbInstanceId());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_SHARED_USERNAME, p_data.getSnapshotSharedUsername());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_SHARED_PASSWORD, p_data.getSnapshotSharedPassword());
		
		l_props.setProperty(BBSchedulerUtil.SNAPSHOT_EMAIL, p_data.getSnapshotEmail());


		l_pbean.writeProperties(l_props);
	}
	
	private ch.qos.logback.classic.Logger getLogger()
	{
		LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
		return context.getLogger("com.obsidiansoln");
	}
	
}
