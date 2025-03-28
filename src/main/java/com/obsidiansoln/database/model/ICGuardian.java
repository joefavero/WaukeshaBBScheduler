package com.obsidiansoln.database.model;

public class ICGuardian {
	private String personId;
	private String bbPersonId;
	private String studentNumber;
	private String studentUsername;
	private String contactNumber;
	private String firstName;
	private String lastName;
	private String systemRole;
	private String institutionRole;
	private String rowStatus;
	private String company;
	private String email;
	private String bbUsername;
	private String bbPassword;
	private String street1;
	private String street2;
	private String title;
	private String state;
	private String city;
	private String zip;
	private String department;
	private String jobTitle;
	private String availableInd;
	private String locale;

	
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getBbPersonId() {
		return bbPersonId;
	}
	public void setBbPersonId(String bbPersonId) {
		this.bbPersonId = bbPersonId;
	}
	public String getStudentNumber() {
		return studentNumber;
	}
	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}

	public String getStudentUsername() {
		return studentUsername;
	}
	public void setStudentUsername(String studentUsername) {
		this.studentUsername = studentUsername;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getSystemRole() {
		return "O";
	}
	public void setSystemRole(String systemRole) {
		this.systemRole = systemRole;
	}
	public String getInstitutionRole() {
		return "SDW";
	}
	
	public String getRowStatus() {
		return "enabled";
	}
	public void setRowStatus(String rowStatus) {
		this.rowStatus = rowStatus;
	}
	public String getCompany() {
		return "School District Of Waukesha - Parent/Guardian";
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBbUsername() {
		return bbUsername;
	}
	public void setBbUsername(String bbUsername) {
		this.bbUsername = bbUsername;
	}
	public String getBbPassword() {
		return bbPassword;
	}
	public void setBbPassword(String bbPassword) {
		this.bbPassword = bbPassword;
	}
	public void setInstitutionRole(String institutionRole) {
		this.institutionRole = institutionRole;
	}
	
	public String getStreet1() {
		return street1;
	}
	public void setStreet1(String street1) {
		this.street1 = street1;
	}
	public String getStreet2() {
		if (street2 == null) {
			return "";
		} 
		return street2;
	}
	public void setStreet2(String street2) {
		this.street2 = street2;
	}
	public String getTitle() {
		return "Parent/Guardian";
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String getDepartment() {
		return "Parent";
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getJobTitle() {
		return"Parent/Guardian";
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getAvailableInd() {
		return "Y";
	}
	public void setAvailableInd(String availableInd) {
		this.availableInd = availableInd;
	}
	public String getLocale() {
		return "en_EN";
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}

	
	
}
