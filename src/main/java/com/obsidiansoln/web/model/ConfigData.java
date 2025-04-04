/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.web.model;

public class ConfigData {

    private String logLevel;
    private String workingDirectory;
    private String apiKey;
    private String adminPW;
    private String ltiKey;
    private String ltiSecret;
	private String restHost;
	private String restKey;
	private String restSecret;
    private String emailHost;
    private String emailPort;
    private String emailUsername;
    private String emailPassword;
    private boolean emailAuthenticate;
    private boolean emailUseSSL;
    private boolean emailDebug;
    private String snapshotBbInstanceId;
    private String snapshotStudentSharedUsername;
    private String snapshotStudentSharedPassword;
    private String snapshotStudentDatasource;
    private String snapshotStaffSharedUsername;
    private String snapshotStaffSharedPassword;
    private String snapshotStaffDatasource;
    private String snapshotGuardianSharedUsername;
    private String snapshotGuardianSharedPassword;
    private String snapshotGuardianDatasource;
    private String snapshotEnrollmentDatasource;
    private String snapshotStudentAssociationDatasource;
    private String snapshotStaffAssociationDatasource;
    private String snapshotGuardianAssociationDatasource;
    private String snapshotEmail;
    

	public ConfigData() {
	}
	
    public ConfigData(String p_logLevel, 
    		String p_workingDirectory,
    		String p_apiKey,
    		String p_adminPW,
    		String p_ltiKey,
    		String p_ltiSecret,
    		String p_restHost,
    		String p_restKey,
    		String p_restSecret,
    		String p_emailHost,
    		String p_emailPort,
    		String p_emailUsername,
    		String p_emailPassword,
    		boolean p_emailAuthenticate,
    		boolean p_emailUseSSL,
    		boolean p_emailDebug,
    		String p_snapshotBbInstanceId,
    		String p_snapshotStudentSharedUsername,
    		String p_snapshotStudentSharedPassword,
    		String p_snapshotStudentDatasource,
    		String p_snapshotStaffSharedUsername,
    		String p_snapshotStaffSharedPassword,
    		String p_snapshotStaffDatasource,
    		String p_snapshotGuardianSharedUsername,
    		String p_snapshotGuardianSharedPassword,
    		String p_snapshotGuardianDatasource,
    		String p_snapshotEnrollmentDatasource,
    		String p_snapshotStudentAssociationDatasource,
    		String p_snapshotStaffAssociationDatasource,
    		String p_snapshotGuardianAssociationDatasource,
    		String p_snapshotEmail) {
        this.logLevel = p_logLevel;
        this.workingDirectory = p_workingDirectory;
        this.apiKey = p_apiKey;
        this.adminPW = p_adminPW;
        this.ltiKey = p_ltiKey;
        this.ltiSecret = p_ltiSecret;
        this.restHost = p_restHost;
        this.restKey = p_restKey;
        this.restSecret = p_restSecret;
        this.emailHost = p_emailHost;
        this.emailPort = p_emailPort;
        this.emailUsername = p_emailUsername;
        this.emailPassword = p_emailPassword;
        this.emailAuthenticate = p_emailAuthenticate;
        this.emailUseSSL = p_emailUseSSL;
        this.emailDebug = p_emailDebug;
        this.snapshotBbInstanceId = p_snapshotBbInstanceId;
        this.snapshotStudentSharedUsername = p_snapshotStudentSharedUsername;
        this.snapshotStudentSharedPassword = p_snapshotStudentSharedPassword;
        this.snapshotStudentDatasource = p_snapshotStudentDatasource;
        this.snapshotStaffSharedUsername = p_snapshotStaffSharedUsername;
        this.snapshotStaffSharedPassword = p_snapshotStaffSharedPassword;
        this.snapshotStaffDatasource = p_snapshotStaffDatasource;
        this.snapshotGuardianSharedUsername = p_snapshotGuardianSharedUsername;
        this.snapshotGuardianSharedPassword = p_snapshotGuardianSharedPassword;
        this.snapshotGuardianDatasource = p_snapshotGuardianDatasource;
        this.snapshotEnrollmentDatasource = p_snapshotEnrollmentDatasource;
        this.snapshotStudentAssociationDatasource = p_snapshotStudentAssociationDatasource;
        this.snapshotStaffAssociationDatasource = p_snapshotStaffAssociationDatasource;
        this.snapshotGuardianAssociationDatasource = p_snapshotGuardianAssociationDatasource;
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

	public String getAdminPW() {
		return adminPW;
	}

	public void setAdminPW(String adminPW) {
		this.adminPW = adminPW;
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
	
	public String getSnapshotBbInstanceId() {
		return snapshotBbInstanceId;
	}

	public void setSnapshotBbInstanceId(String snapshotBbInstanceId) {
		this.snapshotBbInstanceId = snapshotBbInstanceId;
	}

	public String getSnapshotStudentSharedUsername() {
		return snapshotStudentSharedUsername;
	}

	public void setSnapshotStudentSharedUsername(String snapshotStudentSharedUsername) {
		this.snapshotStudentSharedUsername = snapshotStudentSharedUsername;
	}

	public String getSnapshotStudentSharedPassword() {
		return snapshotStudentSharedPassword;
	}

	public void setSnapshotStudentSharedPassword(String snapshotStudentSharedPassword) {
		this.snapshotStudentSharedPassword = snapshotStudentSharedPassword;
	}

	public String getSnapshotStudentDatasource() {
		return snapshotStudentDatasource;
	}

	public void setSnapshotStudentDatasource(String snapshotStudentDatasource) {
		this.snapshotStudentDatasource = snapshotStudentDatasource;
	}
	
	public String getSnapshotStaffSharedUsername() {
		return snapshotStaffSharedUsername;
	}

	public void setSnapshotStaffSharedUsername(String snapshotStaffSharedUsername) {
		this.snapshotStaffSharedUsername = snapshotStaffSharedUsername;
	}

	public String getSnapshotStaffSharedPassword() {
		return snapshotStaffSharedPassword;
	}

	public void setSnapshotStaffSharedPassword(String snapshotStaffSharedPassword) {
		this.snapshotStaffSharedPassword = snapshotStaffSharedPassword;
	}

	public String getSnapshotStaffDatasource() {
		return snapshotStaffDatasource;
	}

	public void setSnapshotStaffDatasource(String snapshotStaffDatasource) {
		this.snapshotStaffDatasource = snapshotStaffDatasource;
	}
	
	public String getSnapshotGuardianSharedUsername() {
		return snapshotGuardianSharedUsername;
	}

	public void setSnapshotGuardianSharedUsername(String snapshotGuardianSharedUsername) {
		this.snapshotGuardianSharedUsername = snapshotGuardianSharedUsername;
	}

	public String getSnapshotGuardianSharedPassword() {
		return snapshotGuardianSharedPassword;
	}

	public void setSnapshotGuardianSharedPassword(String snapshotGuardianSharedPassword) {
		this.snapshotGuardianSharedPassword = snapshotGuardianSharedPassword;
	}

	public String getSnapshotGuardianDatasource() {
		return snapshotGuardianDatasource;
	}

	public void setSnapshotGuardianDatasource(String snapshotGuardianDatasource) {
		this.snapshotGuardianDatasource = snapshotGuardianDatasource;
	}
	
	public String getSnapshotEnrollmentDatasource() {
		return snapshotEnrollmentDatasource;
	}

	public void setSnapshotEnrollmentDatasource(String snapshotEnrollmentDatasource) {
		this.snapshotEnrollmentDatasource = snapshotEnrollmentDatasource;
	}

	
	public String getSnapshotStudentAssociationDatasource() {
		return snapshotStudentAssociationDatasource;
	}

	public void setSnapshotStudentAssociationDatasource(String snapshotStudentAssociationDatasource) {
		this.snapshotStudentAssociationDatasource = snapshotStudentAssociationDatasource;
	}

	public String getSnapshotStaffAssociationDatasource() {
		return snapshotStaffAssociationDatasource;
	}

	public void setSnapshotStaffAssociationDatasource(String snapshotStaffAssociationDatasource) {
		this.snapshotStaffAssociationDatasource = snapshotStaffAssociationDatasource;
	}

	public String getSnapshotGuardianAssociationDatasource() {
		return snapshotGuardianAssociationDatasource;
	}

	public void setSnapshotGuardianAssociationDatasource(String snapshotGuardianAssociationDatasource) {
		this.snapshotGuardianAssociationDatasource = snapshotGuardianAssociationDatasource;
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
