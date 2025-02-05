/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.web.model;

import java.sql.Timestamp;

public class ConfigData {

    private String logLevel;
    private String workingDirectory;
    private String apiKey;
    private String ltiKey;
    private String ltiSecret;
	private String restHost;
	private String restKey;
	private String restSecret;
    private Timestamp semester1StartDate;
    private Timestamp semester1EndDate;
    private Timestamp semester2StartDate;
    private Timestamp semester2EndDate;
    private String defaultSemester;
    private String defaultTerm;
    private String maxReportDays;
    private boolean isDetailed;
    private String adminReportInstructor;
    private String adminReportEmail;
    private String adminReportPhone;
    private String maxPDFEntries;
    private String emailFrom;
    private String emailHost;
    private String emailPort;
    private String emailUsername;
    private String emailPassword;
    private boolean emailAuthenticate;
    private boolean emailUseSSL;
    private boolean emailDebug;
    private String emailNote;
    private String parentInviteMessage;
    private String parentExistingMessage;
    private String snapshotBbInstanceId;
    private String snapshotSharedUsername;
    private String snapshotSharedPassword;
    private String snapshotEmail;
    

	public ConfigData() {
	}
	
    public ConfigData(String p_logLevel, 
    		String p_workingDirectory,
    		String p_apiKey,
    		String p_ltiKey,
    		String p_ltiSecret,
    		String p_restHost,
    		String p_restKey,
    		String p_restSecret,
    		Timestamp p_semester1StartDate, 
    		Timestamp p_semester1EndDate, 
    		Timestamp p_semester2StartDate, 
    		Timestamp p_semester2EndDate, 
    		String p_defaultSemester, 
    		String p_defaultTerm, 
    		String p_maxReportDays, 
    		boolean p_isDetailed,
    		String p_adminReportInstructor,
    		String p_adminReportEmail,
    		String p_adminReportPhone,
    		String p_maxPDFEntries,
    		String p_emailFrom,
    		String p_emailHost,
    		String p_emailPort,
    		String p_emailUsername,
    		String p_emailPassword,
    		boolean p_emailAuthenticate,
    		boolean p_emailUseSSL,
    		boolean p_emailDebug,
    		String p_emailNote,
    		String p_parentInviteMessage,
    		String p_parentExistingMessage,
    		String p_snapshotBbInstanceId,
    		String p_snapshotSharedUsername,
    		String p_snapshotSharedPassword,
    		String p_snapshotEmail) {
        this.logLevel = p_logLevel;
        this.workingDirectory = p_workingDirectory;
        this.apiKey = p_apiKey;
        this.ltiKey = p_ltiKey;
        this.ltiSecret = p_ltiSecret;
        this.restHost = p_restHost;
        this.restKey = p_restKey;
        this.restSecret = p_restSecret;
        this.semester1StartDate = p_semester1StartDate;
        this.semester1EndDate = p_semester1EndDate;
        this.semester2StartDate = p_semester2StartDate;
        this.semester2EndDate = p_semester2EndDate;
        this.defaultSemester = p_defaultSemester;
        this.defaultTerm = p_defaultTerm;
        this.maxReportDays = p_maxReportDays;
        this.isDetailed = p_isDetailed;
        this.adminReportInstructor = p_adminReportInstructor;
        this.adminReportEmail = p_adminReportEmail;
        this.adminReportPhone = p_adminReportPhone;
        this.maxPDFEntries = p_maxPDFEntries;
        this.emailFrom = p_emailFrom;
        this.emailHost = p_emailHost;
        this.emailPort = p_emailPort;
        this.emailUsername = p_emailUsername;
        this.emailPassword = p_emailPassword;
        this.emailAuthenticate = p_emailAuthenticate;
        this.emailUseSSL = p_emailUseSSL;
        this.emailDebug = p_emailDebug;
        this.emailNote = p_emailNote;
        this.parentInviteMessage = p_parentInviteMessage;
        this.parentExistingMessage = p_parentExistingMessage;
        this.snapshotBbInstanceId = p_snapshotBbInstanceId;
        this.snapshotSharedUsername = p_snapshotSharedUsername;
        this.snapshotSharedPassword = p_snapshotSharedPassword;
        this.snapshotEmail = p_snapshotEmail;
    }
	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}
	
    public String getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getLtiKey() {
		return ltiKey;
	}

	public void setLtiKey(String ltiKey) {
		this.ltiKey = ltiKey;
	}

	public String getLtiSecret() {
		return ltiSecret;
	}

	public void setLtiSecret(String ltiSecret) {
		this.ltiSecret = ltiSecret;
	}

	public String getRestHost() {
		return restHost;
	}

	public void setRestHost(String restHost) {
		this.restHost = restHost;
	}

	public String getRestKey() {
		return restKey;
	}

	public void setRestKey(String restKey) {
		this.restKey = restKey;
	}

	public String getRestSecret() {
		return restSecret;
	}

	public void setRestSecret(String restSecret) {
		this.restSecret = restSecret;
	}

	public Timestamp getSemester1StartDate() {
		return semester1StartDate;
	}

	public void setSemester1StartDate(Timestamp semester1StartDate) {
		this.semester1StartDate = semester1StartDate;
	}

	public Timestamp getSemester1EndDate() {
		return semester1EndDate;
	}

	public void setSemester1EndDate(Timestamp semester1EndDate) {
		this.semester1EndDate = semester1EndDate;
	}

	public Timestamp getSemester2StartDate() {
		return semester2StartDate;
	}

	public void setSemester2StartDate(Timestamp semester2StartDate) {
		this.semester2StartDate = semester2StartDate;
	}

	public Timestamp getSemester2EndDate() {
		return semester2EndDate;
	}

	public void setSemester2EndDate(Timestamp semester2EndDate) {
		this.semester2EndDate = semester2EndDate;
	}

	public String getDefaultSemester() {
		return defaultSemester;
	}

	public void setDefaultSemester(String p_defaultSemester) {
		this.defaultSemester = p_defaultSemester;
	}
	
	public String getDefaultTerm() {
		return defaultTerm;
	}

	public void setDefaultTerm(String p_defaultTerm) {
		this.defaultTerm = p_defaultTerm;
	}
	

	public String getMaxReportDays() {
		return maxReportDays;
	}

	public void setMaxReportDays(String p_maxReportDays) {
		this.maxReportDays = p_maxReportDays;
	}
	
	
	public boolean isDetailed() {
		return isDetailed;
	}

	public void setDetailed(boolean isDetailed) {
		this.isDetailed = isDetailed;
	}

	
	public String getAdminReportInstructor() {
		return adminReportInstructor;
	}

	public void setAdminReportInstructor(String adminReportInstructor) {
		this.adminReportInstructor = adminReportInstructor;
	}

	public String getAdminReportEmail() {
		return adminReportEmail;
	}

	public void setAdminReportEmail(String adminReportEmail) {
		this.adminReportEmail = adminReportEmail;
	}

	public String getAdminReportPhone() {
		return adminReportPhone;
	}

	public void setAdminReportPhone(String adminReportPhone) {
		this.adminReportPhone = adminReportPhone;
	}

	public String getMaxPDFEntries() {
		return maxPDFEntries;
	}

	public void setMaxPDFEntries(String maxPDFEntries) {
		this.maxPDFEntries = maxPDFEntries;
	}
	
	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
	

	public String getEmailHost() {
		return emailHost;
	}

	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}

	public String getEmailPort() {
		return emailPort;
	}

	public void setEmailPort(String emailPort) {
		this.emailPort = emailPort;
	}

	public String getEmailUsername() {
		return emailUsername;
	}

	public void setEmailUsername(String emailUsername) {
		this.emailUsername = emailUsername;
	}

	public String getEmailPassword() {
		return emailPassword;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

	public boolean isEmailAuthenticate() {
		return emailAuthenticate;
	}

	public void setEmailAuthenticate(boolean emailAuthenticate) {
		this.emailAuthenticate = emailAuthenticate;
	}

	public boolean isEmailUseSSL() {
		return emailUseSSL;
	}

	public void setEmailUseSSL(boolean emailUseSSL) {
		this.emailUseSSL = emailUseSSL;
	}

	public boolean isEmailDebug() {
		return emailDebug;
	}

	public void setEmailDebug(boolean emailDebug) {
		this.emailDebug = emailDebug;
	}
	
	public String getEmailNote() {
		return emailNote;
	}

	public void setEmailNote(String emailNote) {
		this.emailNote = emailNote;
	}
	
	public String getParentInviteMessage() {
		return parentInviteMessage;
	}

	public void setParentInviteMessage(String parentInviteMessage) {
		this.parentInviteMessage = parentInviteMessage;
	}
	
	public String getParentExistingMessage() {
		return parentExistingMessage;
	}

	public void setParentExistingMessage(String parentExistingMessage) {
		this.parentExistingMessage = parentExistingMessage;
	}

	
	public String getSnapshotBbInstanceId() {
		return snapshotBbInstanceId;
	}

	public void setSnapshotBbInstanceId(String snapshotBbInstanceId) {
		this.snapshotBbInstanceId = snapshotBbInstanceId;
	}

	public String getSnapshotSharedUsername() {
		return snapshotSharedUsername;
	}

	public void setSnapshotSharedUsername(String snapshotSharedUsername) {
		this.snapshotSharedUsername = snapshotSharedUsername;
	}

	public String getSnapshotSharedPassword() {
		return snapshotSharedPassword;
	}

	public void setSnapshotSharedPassword(String snapshotSharedPassword) {
		this.snapshotSharedPassword = snapshotSharedPassword;
	}

	public String getSnapshotEmail() {
		return snapshotEmail;
	}

	public void setSnapshotEmail(String snapshotEmail) {
		this.snapshotEmail = snapshotEmail;
	}

	public String toString() {
		return "ConfigData [logLevel=" + logLevel + "]";
	}
	
}
