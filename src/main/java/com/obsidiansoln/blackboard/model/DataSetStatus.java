package com.obsidiansoln.blackboard.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSetStatus {
	
	private int completedCount;
	private String dataSetUid;
	private int errorCount;
	private Date lastEntryDate;
	private String machineName;
	private int queuedCount;
	private Date startDate;
	private int warningCount;
	public int getCompletedCount() {
		return completedCount;
	}
	public void setCompletedCount(int completedCount) {
		this.completedCount = completedCount;
	}
	public String getDataSetUid() {
		return dataSetUid;
	}
	public void setDataSetUid(String dataSetUid) {
		this.dataSetUid = dataSetUid;
	}
	public int getErrorCount() {
		return errorCount;
	}
	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	public Date getLastEntryDate() {
		return lastEntryDate;
	}
	public void setLastEntryDate(Date lastEntryDate) {
		this.lastEntryDate = lastEntryDate;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public int getQueuedCount() {
		return queuedCount;
	}
	public void setQueuedCount(int queuedCount) {
		this.queuedCount = queuedCount;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public int getWarningCount() {
		return warningCount;
	}
	public void setWarningCount(int warningCount) {
		this.warningCount = warningCount;
	}

	
}
