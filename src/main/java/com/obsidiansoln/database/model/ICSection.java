package com.obsidiansoln.database.model;

public class ICSection {
	
    private Long sectionID;
    private int sectionNumber;
    private Long courseID;
    private Long roomID;
    private Long teacherPersonID;
    private String termName;
    private String period;
    private String linkedCourseName;
    
	public Long getSectionID() {
		return sectionID;
	}
	public void setSectionID(Long sectionID) {
		this.sectionID = sectionID;
	}
	
	public int getSectionNumber() {
		return sectionNumber;
	}
	public void setSectionNumber(int sectionNumber) {
		this.sectionNumber = sectionNumber;
	}
	public Long getCourseID() {
		return courseID;
	}
	public void setCourseID(Long courseID) {
		this.courseID = courseID;
	}
	public Long getRoomID() {
		return roomID;
	}
	public void setRoomID(Long roomID) {
		this.roomID = roomID;
	}
	public Long getTeacherPersonID() {
		return teacherPersonID;
	}
	public void setTeacherPersonID(Long teacherPersonID) {
		this.teacherPersonID = teacherPersonID;
	}
	public String getTermName() {
		return termName;
	}
	public void setTermName(String termName) {
		this.termName = termName;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getLinkedCourseName() {
		return linkedCourseName;
	}
	public void setLinkedCourseName(String linkedCourseName) {
		this.linkedCourseName = linkedCourseName;
	}



    
}
