package com.obsidiansoln.database.model;

public class ICStaff {
	
	private String personId;
	private String userName;
	private String staffNumber;
	private String institutionRole;
	private String firstName;
	private String middleName;
	private String lastName;
	private String nameSuffix;
	private String alias;
	private String birthDate;
	private String gender;
	private String title;
	private String jobTitle;
	private String department;
	private String workPhone;
	private String email1;
	private int roleCount;
	private String availableInd;
	private String webpage;
	private String locale;
	private String country;
	
	
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getStaffNumber() {
		return staffNumber;
	}
	public void setStaffNumber(String staffNumber) {
		this.staffNumber = staffNumber;
	}
	public String getInstitutionRole() {
		return (institutionRole == null) ? "" : institutionRole;
	}
	public void setInstitutionRole(String institutionRole) {
		this.institutionRole = institutionRole;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getNameSuffix() {
		return nameSuffix;
	}
	public void setNameSuffix(String nameSuffix) {
		this.nameSuffix = nameSuffix;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getTitle() {
		return (title == null) ? "" : title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getJobTitle() {
		return (jobTitle == null) ? "" : jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getDepartment() {
		return (department == null) ? "" : department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getWorkPhone() {
		return workPhone;
	}
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}
	public String getEmail1() {
		return email1;
	}
	public void setEmail1(String email1) {
		this.email1 = email1;
	}
	public int getRoleCount() {
		return roleCount;
	}
	public void setRoleCount(int roleCount) {
		this.roleCount = roleCount;
	}
	public String getAvailableInd() {
		return "Y";
	}
	public void setAvailableInd(String availableInd) {
		this.availableInd = availableInd;
	}
	public String getWebpage() {
		return "";
	}
	public void setWebpage(String webpage) {
		this.webpage = webpage;
	}
	public String getLocale() {
		return "en_EN";
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getCountry() {
		return "US";
	}
	public void setCountry(String country) {
		this.country = country;
	}
	

}
