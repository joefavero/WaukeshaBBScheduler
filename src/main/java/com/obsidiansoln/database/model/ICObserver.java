package com.obsidiansoln.database.model;

public class ICObserver {
	private String contactNumber;
	private String observee;
	
	
	public ICObserver(String contactNumber, String observee) {
		super();
		this.contactNumber = contactNumber;
		this.observee = observee;
	}
	
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getObservee() {
		return observee;
	}
	public void setObservee(String observee) {
		this.observee = observee;
	}
	
	
}
