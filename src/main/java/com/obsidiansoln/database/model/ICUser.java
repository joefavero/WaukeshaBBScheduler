package com.obsidiansoln.database.model;

public class ICUser {
	private String union;
	private String personId;
	private String studentNumber;
	private String firstName;
	private String middleName;
	private String lastName;
	private String birthDate;
	private String gender;
	private String homePhone1;
	private String homePhone2;
	private String cellPhone;
	private String email1;
	private String email2;
	private String gradeLevel;
	private String serviceType;
	private String schoolNumber;
	private String calendarName;
	private String address;
	private String apartment;
	private String city;
	private String state;
	private String zip;
	private String userName;
	private String department;
	private String country;
	private String jobTitle;
	private String availableInd;
	private String webpage;
	private String locale;
	
	
	public String getUnion() {
		return union;
	}
	public void setUnion(String union) {
		this.union = union;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getStudentNumber() {
		return studentNumber;
	}
	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public String getMiddleName() {
		return (middleName == null) ? "" : middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getEmail1() {
		return email1;
	}
	public void setEmail1(String email1) {
		this.email1 = email1;
	}
	public String getEmail2() {
		return email2;
	}
	public void setEmail2(String email2) {
		this.email2 = email2;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public String getGradeLevel() {
		switch (gradeLevel) {
			case "01":
			case "02":
			case "03":
			case "04":
			case "05":
			case "06":
			case "07":
			case "08":
				gradeLevel="K-8";
				break;
			case "09":
			case "10":
			case "11":
			case "12":
				gradeLevel="High School";
				break;
			default:
				gradeLevel="";
				break;
	}
		return gradeLevel;
	}
	public void setGradeLevel(String gradeLevel) {
		this.gradeLevel = gradeLevel;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public String getSchoolNumber() {
		return schoolNumber;
	}
	public void setSchoolNumber(String schoolNumber) {
		this.schoolNumber = schoolNumber;
	}
	public String getCalendarName() {
		return calendarName;
	}
	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getApartment() {
		return apartment;
	}
	public void setApartment(String apartment) {
		this.apartment = apartment;
	}
	public String getCity() {
		return (city == null) ? "" : city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return (state == null) ? "" : state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return (zip == null) ? "" : zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String getDepartment() {
		return "Student";
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getCountry() {
		return "US";
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getHomePhone1() {
		return homePhone1;
	}
	public void setHomePhone1(String homePhone1) {
		this.homePhone1 = homePhone1;
	}
	public String getHomePhone2() {
		return homePhone2;
	}
	public void setHomePhone2(String homePhone2) {
		this.homePhone2 = homePhone2;
	}
	public String getCellPhone() {
		return cellPhone;
	}
	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}
	
	public String getJobTitle() {
		return "Student";
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


}
