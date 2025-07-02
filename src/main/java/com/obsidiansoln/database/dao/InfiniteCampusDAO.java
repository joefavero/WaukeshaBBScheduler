/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.database.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import com.obsidiansoln.blackboard.coursecopy.CourseInfo;
import com.obsidiansoln.blackboard.coursecopy.PersonInfo;
import com.obsidiansoln.blackboard.coursecopy.SectionInfo;
import com.obsidiansoln.blackboard.group.GroupProxy;
import com.obsidiansoln.database.model.ICBBCourse;
import com.obsidiansoln.database.model.ICBBEnrollment;
import com.obsidiansoln.database.model.ICBBGroup;
import com.obsidiansoln.database.model.ICBBSection;
import com.obsidiansoln.database.model.ICCalendar;
import com.obsidiansoln.database.model.ICCourse;
import com.obsidiansoln.database.model.ICEnrollment;
import com.obsidiansoln.database.model.ICGuardian;
import com.obsidiansoln.database.model.ICMessage;
import com.obsidiansoln.database.model.ICNode;
import com.obsidiansoln.database.model.ICPerson;
import com.obsidiansoln.database.model.ICSection;
import com.obsidiansoln.database.model.ICSectionInfo;
import com.obsidiansoln.database.model.ICStaff;
import com.obsidiansoln.database.model.ICStudent;
import com.obsidiansoln.database.model.ICTeacher;
import com.obsidiansoln.database.model.ICTeacherList;
import com.obsidiansoln.database.model.ICTemplate;
import com.obsidiansoln.database.model.ICUser;
import com.obsidiansoln.database.model.UpdateCourseInfo;

public class InfiniteCampusDAO {

	private static Logger mLog = LoggerFactory.getLogger(InfiniteCampusDAO.class);

	private NamedParameterJdbcTemplate template;  

	// Constructor
	public  InfiniteCampusDAO() {

	}

	// Constructor
	public  InfiniteCampusDAO(NamedParameterJdbcTemplate template) {
		this.template = template;
	}


	public void setTemplate(NamedParameterJdbcTemplate template) {    
		this.template = template;      
	}    

	@Transactional(readOnly=true)
	public List<ICCalendar> getByYearEnd(String username) {
		String sql = "select Calendar.CalendarId as calendarID, Calendar.name, School.name as schoolName from Calendar"
				+ " left join School on School.schoolID = Calendar.schoolID"
				+ " where Calendar.schoolID in (select schoolId from EmploymentAssignment" 
				+ " where personId = (select personId from UserAccount where username = :username) and startDate <= GETDATE() and (endDate > GETDATE() or endDate is null))" 
				+ " and Calendar.endYear=year(GETDATE())";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("username", username);
		List<ICCalendar> calendars = null;

		try {
			calendars = template.query(sql, params, new BeanPropertyRowMapper<ICCalendar>(ICCalendar.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return calendars;
	}

	@Transactional(readOnly=true)
	public List<ICCourse> getCoursesByUsername(String username) {
		mLog.trace("In getCoursesByUsername ...");
		List<ICCourse> l_returnList = new ArrayList<ICCourse>();
		String userSQL = "select distinct Course.courseID,"
				+ " Course.calendarID, "
				+ " Course.name as courseName, "
				+ " Course.number as courseNumber,"
				+ " Calendar.endYear, "
				+ " School.name as schoolName, "
				+ " UserAccount.username as teacherName, "
				+ " (select distinct count(*) "
				+ " From SDWBlackboardSchedulerSISCourseSections "
				+ " where SDWBlackboardSchedulerSISCourseSections.courseID = Course.courseID) as linkedCount, "
				+ " (select distinct count(*)"
				+ " From Section"
				+ " Inner Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID"
				+ " Inner Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1"
				+ " Inner Join SchoolYear on SchoolYear.endYear=calendar.endYear"
				+ " where Section.courseID = Course.courseID and Section.teacherPersonId = (select personId from UserAccount where username = :username)"
				+ " and Trial.active = 1) as sectionCount,"
				+ " a.bbCourseID as bbCourseId, "
				+ " a.bbCOURSE_ID as blackboardId, "
				+ " a.bbCOURSE_NAME as blackboardName "
				+ " from Section with (nolock)"
				+ " left join Course with (nolock) on Course.courseID = Section.courseID"
				+ " left join Calendar with (nolock) on Calendar.calendarID = Course.calendarID"
				+ " left join School with (nolock) on School.schoolID = Calendar.schoolID "
				+ " left join Teacher with (nolock) on Teacher.sectionID = Section.sectionID"
				+ " left join UserAccount with (nolock) on UserAccount.personID = Teacher.personID "
				+ " left join SDWBlackboardSchedulerSISCourseSections b with (nolock) on b.sectionID = Section.sectionID"
				+ " left join SDWBlackboardSchedulerBBCourses a with (nolock) on a.bbCourseId = b.bbCourseID"
				+ " where Section.teacherPersonID = (select personId from UserAccount where username = :username)"
				+ " and ((Calendar.enddate >= CAST(GETDATE() as date)) or Calendar.endYear=year(GETDATE())+1) "
				+ " and Section.externalLMSExclude = 0 and Course.externalLMSExclude = 0"
				+ " and (select distinct count(*) "
				+ " From Section "
				+ " Inner Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID "
				+ " Inner Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1 "
				+ " Inner Join SchoolYear on SchoolYear.endYear=calendar.endYear "
				+ " where Section.courseID = Course.courseID and Section.teacherPersonId = (select personId from UserAccount where username = :username) "
				+ " and Trial.active = 1) > 0";

		String userSQLAdmin = "select distinct Course.courseID,"
				+ " Course.calendarID, "
				+ " Course.name as courseName, "
				+ " Course.number as courseNumber,"
				+ " Calendar.endYear, "
				+ " School.name as schoolName, "
				+ " UserAccount.username as teacherName, "
				+ " (select distinct count(*) "
				+ " From SDWBlackboardSchedulerSISCourseSections "
				+ " where SDWBlackboardSchedulerSISCourseSections.courseID = Course.courseID) as linkedCount, "
				+ " (select distinct count(*) "
				+ " From Section "
				+ " Inner Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID "
				+ " Inner Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1 "
				+ " Inner Join SchoolYear on SchoolYear.endYear=calendar.endYear "
				+ " where Section.courseID = Course.courseID "
				+ " and Trial.active = 1 ) as sectionCount, "
				+ " a.bbCourseID as bbCourseId, "
				+ " a.bbCOURSE_ID as blackboardId, "
				+ " a.bbCOURSE_NAME as blackboardName "
				+ " from Section with (nolock) "
				+ " left join Course with (nolock) on Course.courseID = Section.courseID "
				+ " left join Calendar with (nolock) on Calendar.calendarID = Course.calendarID "
				+ " left join School with (nolock) on School.schoolID = Calendar.schoolID "
				+ " left join Teacher with (nolock) on Teacher.sectionID = Section.sectionID "
				+ " left join UserAccount with (nolock) on UserAccount.personID = Teacher.personID "
				+ " left join SDWBlackboardSchedulerSISCourseSections b with (nolock) on b.sectionID = Section.sectionID "
				+ " left join SDWBlackboardSchedulerBBCourses a with (nolock) on a.bbCourseId = b.bbCourseID "
				+ " where ((Calendar.enddate >= CAST(GETDATE() as date)) or Calendar.endYear=year(GETDATE())+1) "
				+ " and Section.externalLMSExclude = 0 and Course.externalLMSExclude = 0 "
				+ " and (select distinct count(*) "
				+ "				 From Section "
				+ "				 Inner Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID "
				+ "				 Inner Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1 "
				+ "				 Inner Join SchoolYear on SchoolYear.endYear=calendar.endYear "
				+ "				 where Section.courseID = Course.courseID "
				+ "				 and Trial.active = 1) > 0 and UserAccount.userName is not null";

		MapSqlParameterSource params = new MapSqlParameterSource();

		List<ICCourse> courses = null;
		try {
			if (username.equals("admin")) {
				courses= template.query(userSQLAdmin, params, new BeanPropertyRowMapper<ICCourse>(ICCourse.class));

				// Now remove duplicates and add configure Linked Courses
				HashMap<String, ICCourse> l_courseList = new HashMap<String, ICCourse>();
				for (ICCourse course : courses) {

					ICCourse l_temp = l_courseList.get(course.getCourseID());

					if (l_temp != null) {
						if (course.getBbCourseId() != null) {
							if (l_temp.getLinkedCourses() != null) {
								l_temp.getLinkedCourses().add(course.getBlackboardName());
							} else {
								List<String> l_linkedCourses = new ArrayList<String>();
								l_linkedCourses.add(course.getBlackboardName());
								l_temp.setLinkedCourses(l_linkedCourses);
							}
						}
						l_courseList.put(String.valueOf(course.getCourseID())+course.getTeacherName() , l_temp);
					} else {
						if (course.getBbCourseId() != null) {
							if (course.getLinkedCourses() != null) {
								course.getLinkedCourses().add(course.getBlackboardName());
							} else {
								List<String> l_linkedCourses = new ArrayList<String>();
								l_linkedCourses.add(course.getBlackboardName());
								course.setLinkedCourses(l_linkedCourses);
							}
						}
						l_courseList.put(String.valueOf(course.getCourseID())+course.getTeacherName(), course);
					}
				}
				// Iterating HashMap through for loop
				for (Map.Entry<String, ICCourse> set : l_courseList.entrySet()) {
					l_returnList.add(set.getValue());
				}
				return l_returnList;
			} else {
				params.addValue("username", username);
				courses= template.query(userSQL, params, new BeanPropertyRowMapper<ICCourse>(ICCourse.class));

				// Now remove duplicates and add configure Linked Courses
				HashMap<Long, ICCourse> l_courseList = new HashMap<Long, ICCourse>();
				for (ICCourse course : courses) {

					ICCourse l_temp = l_courseList.get(course.getCourseID());

					if (l_temp != null) {
						if (course.getBbCourseId() != null) {
							if (l_temp.getLinkedCourses() != null) {
								l_temp.getLinkedCourses().add(course.getBlackboardName());
							} else {
								List<String> l_linkedCourses = new ArrayList<String>();
								l_linkedCourses.add(course.getBlackboardName());
								l_temp.setLinkedCourses(l_linkedCourses);
							}
						}
						l_courseList.put(l_temp.getCourseID(), l_temp);
					} else {
						if (course.getBbCourseId() != null) {
							if (course.getLinkedCourses() != null) {
								course.getLinkedCourses().add(course.getBlackboardName());
							} else {
								List<String> l_linkedCourses = new ArrayList<String>();
								l_linkedCourses.add(course.getBlackboardName());
								course.setLinkedCourses(l_linkedCourses);
							}
						}
						l_courseList.put(course.getCourseID(), course);
					}
				}
				// Iterating HashMap through for loop
				for (Map.Entry<Long, ICCourse> set : l_courseList.entrySet()) {
					l_returnList.add(set.getValue());
				}
			}

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return l_returnList;
	}

	@Transactional(readOnly=true)
	public List<ICBBCourse> getBBCoursesByUsername(String username) {
		mLog.trace("In getBBCoursesByUsername ...");
		String sql = "select distinct sdw.bbCourseID as id, sdw.bbCOURSE_ID as bbCourseId,  Calendar.name as calendarName,"
				+ "	 (select top 1 UserAccount.username from UserAccount  where UserAccount.personID = sdw.createdByPersonID) as userName,"
				+ "	 sdw.schoolYear,"
				+ "	 sdw.bbCOURSE_NAME as bbCourseName, "
				+ "	 sdw.bbDESCRIPTION as bbCourseDescription, "
				+ "	 sdw.groupSetId as groupSetId "
				+ "	 from SDWBlackboardSchedulerBbCourses sdw"
				+ "	 left join SDWBlackboardSchedulerSISCourseSections on SDWBlackboardSchedulerSISCourseSections.bbCourseID=sdw.bbCourseID"
				+ "  left join SDWBlackboardSchedulerSISCoursePersons sdwp on sdwp.bbCourseID =sdw.bbCourseID"
				+ "	 left join UserAccount on UserAccount.personID=sdw.createdByPersonID or UserAccount.personID=sdwp.personID"
				+ "	 left join Calendar on Calendar.calendarID=sdw.calendarID "
				+ "	 where (UserAccount.username=:username"
				+ "	 and (Calendar.endYear=year(GETDATE()) or Calendar.endYear=year(GETDATE())+1))"
				+ "  or (UserAccount.username=:username and sdwp.personType='T' and (Calendar.endYear=year(GETDATE()) or Calendar.endYear=year(GETDATE())+1))";

		String sqlAdmin = "select distinct sdw.bbCourseID as id, sdw.bbCOURSE_ID as bbCourseId,  Calendar.name as calendarName,"
				+ " (select top 1 UserAccount.username from UserAccount  where UserAccount.personID = sdw.createdByPersonID) as userName, "
				+ " sdw.schoolYear,"
				+ " sdw.bbCOURSE_NAME as bbCourseName, "
				+ " sdw.bbDESCRIPTION as bbCourseDescription, "
				+ " sdw.groupSetId as groupSetId "
				+ " from SDWBlackboardSchedulerBbCourses sdw"
				+ " left join SDWBlackboardSchedulerSISCourseSections on SDWBlackboardSchedulerSISCourseSections.bbCourseID=sdw.bbCourseID"
				+ " left join UserAccount on UserAccount.personID=sdw.createdByPersonID"
				+ " left join Calendar on Calendar.calendarID=sdw.calendarID"
				+ " where (Calendar.endYear=year(GETDATE()) or Calendar.endYear=year(GETDATE())+1)";

		MapSqlParameterSource params = new MapSqlParameterSource();

		List<ICBBCourse> bbCourses = null;
		try {
			if (username.equals("admin")) {
				bbCourses= template.query(sqlAdmin, params, new BeanPropertyRowMapper<ICBBCourse>(ICBBCourse.class));
			} else {
				params.addValue("username", username);
				bbCourses= template.query(sql, params, new BeanPropertyRowMapper<ICBBCourse>(ICBBCourse.class));
			}

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return bbCourses;
	}

	@Transactional(readOnly=true)
	public List<ICBBSection> getBBSectionsByCourseIdUsername(String courseId, String username) {
		mLog.info("In getBBCoursesByUsername ...");
		List<ICBBSection> l_returnList = new ArrayList<ICBBSection>();
		String sql = "select distinct sdws.sectionID as sectionID, "
				+ "			 Course.courseID as courseId, "
				+ "			 Course.name as courseName, "
				+ "          Section.number as sectionNumber, "
				+ "          UserAccount.username as teacherName, "
				+ "          Term.name as termName, "
				+ "          [Period].name as period, "
				+ "          (select count(*) as studentCount from Roster "
				+ "				 inner join UserAccount on UserAccount.personID = Roster.personID "
				+ "				 where Roster.sectionID = Section.sectionID "
				+ "				 and (Roster.endDate is null or Roster.endDate > GETDATE())) as studentNumber, "
				+ "         1 as teacherNumber "
				+ "				 from SDWBlackboardSchedulerSISCourseSections sdws "
				+ "				 left join SDWBlackboardSchedulerBBCourses sdw on sdw.bbCourseID = sdws.bbCourseID "
				+ "         left join Section on section.sectionID= sdws.sectionID "
				+ "         left join Course on course.courseID=Section.courseID "
				+ "         left join UserAccount on UserAccount.personID=Section.teacherPersonID "
				+ "         left join Calendar on Calendar.calendarID=sdw.calendarID "
				+ "         left Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID "
				+ "         left Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1 "
				+ "			left Join SectionPlacement on SectionPlacement.sectionID=section.sectionID  and SectionPlacement.trialID=trial.trialID "
				+ "         left Join Term on Term.termID=sectionplacement.termID "
				+ "         left Join [Period] on [Period].periodID=sectionplacement.periodID "
				+ "		where UserAccount.username=:username and sdw.bbCOURSE_ID = :courseid "
				+ "         and (Calendar.endYear=year(GETDATE()) or Calendar.endYear=year(GETDATE())+1)";

		String sqlAdmin = "select distinct sdws.sectionID as sectionID, "
				+ "			 Course.courseID as courseId, "
				+ "			 Course.name as courseName, "
				+ "          Section.number as sectionNumber, "
				+ "          UserAccount.username as teacherName, "
				+ "          Term.name as termName, "
				+ "          [Period].name as period, "
				+ "          (select count(*) as studentCount from Roster "
				+ "				 inner join UserAccount on UserAccount.personID = Roster.personID "
				+ "				 where Roster.sectionID = Section.sectionID "
				+ "				 and (Roster.endDate is null or Roster.endDate > GETDATE())) as studentNumber, "
				+ "         1 as teacherNumber "
				+ "				 from SDWBlackboardSchedulerSISCourseSections sdws "
				+ "				 left join SDWBlackboardSchedulerBBCourses sdw on sdw.bbCourseID = sdws.bbCourseID "
				+ "         left join Section on section.sectionID= sdws.sectionID "
				+ "         left join Course on course.courseID=Section.courseID "
				+ "         left join UserAccount on UserAccount.personID=Section.teacherPersonID "
				+ "         left join Calendar on Calendar.calendarID=sdw.calendarID "
				+ "         left Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID "
				+ "         left Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1 "
				+ "			left Join SectionPlacement on SectionPlacement.sectionID=section.sectionID  and SectionPlacement.trialID=trial.trialID "
				+ "         left Join Term on Term.termID=sectionplacement.termID "
				+ "         left Join [Period] on [Period].periodID=sectionplacement.periodID "
				+ "		  where sdw.bbCOURSE_ID = :courseid "
				+ "         and (Calendar.endYear=year(GETDATE()) or Calendar.endYear=year(GETDATE())+1)";

		MapSqlParameterSource params = new MapSqlParameterSource();

		List<ICBBSection> bbSections = null;
		HashMap<String, ICBBSection> l_sectionList = new HashMap<String, ICBBSection>();
		try {
			if (username.equals("admin")) {
				params.addValue("courseid", courseId);
				bbSections= template.query(sqlAdmin, params, new BeanPropertyRowMapper<ICBBSection>(ICBBSection.class));
				for (ICBBSection bbSection : bbSections) {
					ICBBSection l_temp = l_sectionList.get(Long.valueOf(bbSection.getSectionID()));

					if (l_temp != null) {
						if (l_temp.getTermName() != null) {
							l_temp.setTermName (l_temp.getTermName().concat("/"+bbSection.getTermName()));
						}

						l_sectionList.put(String.valueOf(bbSection.getSectionID())+bbSection.getTeacherName(), l_temp);
					} else {
						l_sectionList.put(String.valueOf(bbSection.getSectionID())+bbSection.getTeacherName(), bbSection);
						
						// Now Update the Teacher Count
						List<ICTeacherList> l_teacherList = this.getTeacherList(bbSection.getCourseId(), Integer.valueOf(bbSection.getSectionNumber()));
						bbSection.setTeacherNumber(Long.valueOf(l_teacherList.size()));
					}
				}

				// Iterating HashMap through for loop
				for (Map.Entry<String, ICBBSection> set : l_sectionList.entrySet()) {
					l_returnList.add(set.getValue());
				}
			} else {
				params.addValue("courseid", courseId);
				params.addValue("username", username);
				bbSections= template.query(sql, params, new BeanPropertyRowMapper<ICBBSection>(ICBBSection.class));
				for (ICBBSection bbSection : bbSections) {
					ICBBSection l_temp = l_sectionList.get(Long.valueOf(bbSection.getSectionID()));

					if (l_temp != null) {
						if (l_temp.getTermName() != null) {
							l_temp.setTermName (l_temp.getTermName().concat("/"+bbSection.getTermName()));
						}

						l_sectionList.put(String.valueOf(bbSection.getSectionID()), l_temp);
					} else {
						l_sectionList.put(String.valueOf(bbSection.getSectionID()), bbSection);
					}
				}

				// Iterating HashMap through for loop
				for (Map.Entry<String, ICBBSection> set : l_sectionList.entrySet()) {
					l_returnList.add(set.getValue());
				}
			}
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return l_returnList;
	}

	@Transactional(readOnly=true)
	public List<ICBBEnrollment> getBBEnrollments() {
		mLog.info("In getBBEnrollments ...");

		String sql2 = "select sdw.bbCourseID as bbCourseId,"
				+ " sdw.bbCOURSE_ID as courseId,"
				+ " sdws.sectionID as sectionId,"
				+ " Section.number as sectionNumber, "
				+ " Roster.personID as personId,"
				+ " Person.studentNumber as studentNumber,"
				+ " UserAccount.username as userName,"
				+ " cal.endDate as calEndDate"
				+ " from SDWBlackboardSchedulerBbCourses sdw"
				+ "		left join SDWBlackboardSchedulerSISCourseSections sdws on sdws.bbCourseID=sdw.bbCourseID"
				+ "     left join Roster on Roster.sectionID = sdws.sectionID"
				+ "     left join Course on Course.courseID = sdws.courseID"
				+ "     left join Section on Section.sectionID = Roster.sectionID"
				+ "     left join Person on Person.personID = Roster.personID "
				+ "     left join UserAccount on UserAccount.personID = Roster.personID "
				+ "		Inner join Calendar cal on sdw.calendarID = cal.calendarID"
				+ "     Inner join enrollment on Person.personID = enrollment.personID and cal.calendarID = enrollment.calendarID"
				+ "				where (Roster.endDate is null or Roster.endDate > GETDATE()) and "
				+ "             (sdws.sectionID is not null) and "
				+ "             (Cal.endDate is null or Cal.endDate >= GETDATE()) and "
				+ "             ((Enrollment.endDate is null or Enrollment.endDate > GETDATE())) and "
				+ "             UserAccount.userName is not null and "
				+ "             sdws.IsSelected = 1";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICBBEnrollment> bbEnrollments = null;
		try {
			bbEnrollments= template.query(sql2, params, new BeanPropertyRowMapper<ICBBEnrollment>(ICBBEnrollment.class));
			
			// Now we need to add extra students and extra teacher
			List<ICBBEnrollment> l_enrollments = this.getExtraEnrollments();
			for (ICBBEnrollment l_enrollment : l_enrollments) {
		
				if (l_enrollment.getRole().equals("T")) {
					l_enrollment.setRole("Instructor");
				} else if (l_enrollment.getRole().equals("S")) {
					l_enrollment.setRole("Student");
				}
				bbEnrollments.add(l_enrollment);
			}
			mLog.info("Number of Extra Enrollment: " + l_enrollments.size());
			
			// Now we need to add the Instructors
			List<ICBBEnrollment> l_teacherEnrollments = this.getTeacherEnrollments();
			for (ICBBEnrollment l_enrollment : l_teacherEnrollments) {
				bbEnrollments.add(l_enrollment);
			}
			mLog.info("Number of Teacher Enrollment: " + l_enrollments.size());
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return bbEnrollments;
	}

	@Transactional(readOnly=true)
	public List<ICBBEnrollment> getExtraEnrollments() {
		mLog.info("In getExtraEnrollments ...");

		String sql = "select distinct sdwp.personID as personId, "
				+ "				          sdwc.bbCOURSE_ID as courseId, "
				+ "				          prs.studentNumber as studentNumber, "
				+ "				          sdwp.personType as role "
				+ "								  from SDWBlackboardSchedulerSISCoursePersons sdwp "
				+ "								  left join SDWBlackboardSchedulerBBCourses sdwc on sdwc.bbCourseId=sdwp.bbCourseID "
				+ "								  left join UserAccount on UserAccount.personID = sdwp.personID "
				+ "				          left join Person prs with (nolock) on UserAccount.personID=prs.personID "
				+ "				          left join Calendar on Calendar.calendarID=sdwc.calendarID "
				+ "								  where (Calendar.endYear=year(GETDATE()) or Calendar.endYear=year(GETDATE())+1) and "
				+ "				             UserAccount.isSAMLAccount=1 and sdwp.personType='S' "
				+ " UNION "
				+ " select distinct sdwp.personID as personId, "
				+ "				          sdwc.bbCOURSE_ID as courseId, "
				+ "                  prs.staffNumber as studentNumber, "
				+ "				          sdwp.personType as role  "
				+ "								  from SDWBlackboardSchedulerSISCoursePersons sdwp "
				+ "								  left join SDWBlackboardSchedulerBBCourses sdwc on sdwc.bbCourseId=sdwp.bbCourseID "
				+ "								  left join UserAccount on UserAccount.personID = sdwp.personID "
				+ "				          join Person prs with (nolock) on UserAccount.personID=prs.personID "
				+ "				       	    and NOT (prs.staffNumber Is Null OR prs.staffNumber='') "
				+ "				          left join Calendar on Calendar.calendarID=sdwc.calendarID "
				+ "								  where (Calendar.endYear=year(GETDATE()) or Calendar.endYear=year(GETDATE())+1) and "
				+ "				             UserAccount.isSAMLAccount=1 and sdwp.personType='T'";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICBBEnrollment> bbEnrollments = null;
		try {
			bbEnrollments= template.query(sql, params, new BeanPropertyRowMapper<ICBBEnrollment>(ICBBEnrollment.class));

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return bbEnrollments;
	}
	
	@Transactional(readOnly=true)
	public List<ICBBEnrollment> getTeacherEnrollments() {
		mLog.info("In getTeacherEnrollments ...");

		String sql = "select distinct sdwc.bbCOURSE_ID as courseId, "
				+ "      Section.teacherPersonID as personId, "
				+ "      Person.staffNumber as studentNumber, "
				+ "      'Instructor' as role "
				+ "   from SDWBlackboardSchedulerSISCourseSections sdws"
				+ "     left join SDWBlackboardSchedulerBBCourses sdwc on sdwc.bbCourseId=sdws.bbCourseId"
				+ "     left join Section on Section.sectionID=sdws.sectionID"
				+ "     left join Person on Person.personID = Section.teacherPersonID"
				+ "     left join UserAccount on UserAccount.personID = Person.personID"
				+ "   where Person.studentNumber is not null and UserAccount.isSAMLAccount=1";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICBBEnrollment> bbEnrollments = null;
		try {
			bbEnrollments= template.query(sql, params, new BeanPropertyRowMapper<ICBBEnrollment>(ICBBEnrollment.class));

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return bbEnrollments;
	}
	
	@Transactional(readOnly=true)
	public List<ICBBGroup> getBBGroups() {
		mLog.info("In getBBGroups ...");

		String sql2 = "select distinct sdws.groupId as groupId,"
				+ " sdw.bbCOURSE_ID as courseId,"
				+ " sdws.sectionID as sectionId,"
				+ " UserAccount.username as userName"
				+ " from SDWBlackboardSchedulerSISCourseSections sdws"
				+ "		left join SDWBlackboardSchedulerBbCourses sdw on sdw.bbCourseID=sdws.bbCourseID"
				+ "        left join Roster on Roster.sectionID = sdws.sectionID"
				+ "        left join Course on Course.courseID = sdws.courseID"
				+ "        left join Section on Section.sectionID = Roster.sectionID"
				+ "		left join [Identity] on [Identity].personID = Roster.personID"
				+ "		left join UserAccount on UserAccount.personID = Roster.personID"
				+ "		Inner join Calendar cal on sdw.calendarID = cal.calendarID"
				+ "				where (Roster.endDate is null or Roster.endDate > GETDATE()) "
				+ "   and (sdws.sectionID is not null) "
				+ "   and (Cal.endDate is null or Cal.endDate >= GETDATE())"
				+ "   and sdws.groupId is not null";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICBBGroup> bbGroups = null;
		try {
			bbGroups= template.query(sql2, params, new BeanPropertyRowMapper<ICBBGroup>(ICBBGroup.class));

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return bbGroups;
	}

	@Transactional(readOnly=true)
	public ICBBCourse getBBCourseById(String courseId) {
		mLog.trace("In getBBCoursesById ...");
		mLog.debug("COURSE ID: " + courseId);
		String sql = "select sdw.bbCourseID as bbCourseId, sdw.bbCOURSE_ID as courseId, sdw.bbCOURSE_NAME as courseName, sdw.bbDESCRIPTION as courseDescription, sdw.groupSetId from SDWBlackboardSchedulerBbCourses sdw"
				+ " where sdw.bbCOURSE_ID=:courseid";


		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("courseid", courseId);
		ICBBCourse bbCourse = null;
		try {
			bbCourse= (ICBBCourse) template.queryForObject(sql, params, new BeanPropertyRowMapper(ICBBCourse.class));

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return bbCourse;
	}
	
	@Transactional(readOnly=true)
	public ICBBCourse getBBCourseByBBId(String bbCourseId) {
		mLog.trace("In getBBCoursesById ...");
		mLog.debug("BB COURSE ID: " + bbCourseId);
		String sql = "select sdw.bbCourseID as bbCourseId, sdw.bbCOURSE_ID as courseId, sdw.bbCOURSE_NAME as courseName, sdw.bbDESCRIPTION as courseDescription, sdw.groupSetId from SDWBlackboardSchedulerBbCourses sdw"
				+ " where sdw.bbCourseID=:bbCourseid";


		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bbCourseid", bbCourseId);
		ICBBCourse bbCourse = null;
		try {
			bbCourse= (ICBBCourse) template.queryForObject(sql, params, new BeanPropertyRowMapper(ICBBCourse.class));

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return bbCourse;
	}

	@Transactional(readOnly=true)
	public List<ICSection> getSectionsByCourseIdUsername(String courseId, String username) {
		mLog.trace("In getSectionsByCourseIdUsername ...");
		List<ICSection> l_returnList = new ArrayList<ICSection>();

		String sql = "select Distinct"
				+ " Section.sectionID, "
				+ " Section.number as sectionNumber,"
				+ " Section.courseID,"
				+ " Section.teacherPersonID,"
				+ " Term.name as termName,"
				+ " [Period].name as period,"
				+ " Periodschedule.name as periodDay,"
				+ " a.bbCourse_ID as linkedCourseId,"
				+ " a.bbCourse_NAME as linkedCourseName,"
				+ " (select count(*) as studentCount from Roster "
				+ " inner join UserAccount on UserAccount.personID = Roster.personID"
				+ " where Roster.sectionID = Section.sectionID"
				+ " and (Roster.endDate is null or Roster.endDate > GETDATE())) as studentNumber, "
				+ " 1 as teacherNumber, "
				+ " School.name as schoolName"
				+ " From Course"
				+ " left Join Calendar on calendar.calendarID=course.calendarID"
				+ " left join School with (nolock) on School.schoolID = Calendar.schoolID"
				+ " left Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID"
				+ " left Join Section on section.courseID=course.courseID"
				+ " left join SDWBlackboardSchedulerSISCourseSections b on b.sectionID = Section.sectionID"
				+ " left join SDWBlackboardSchedulerBBCourses a on a.bbCourseId = b.bbCourseID"
				+ " left Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1"
				+ " left Join SectionPlacement on SectionPlacement.sectionID=section.sectionID  and SectionPlacement.trialID=trial.trialID"
				+ " left Join Term on Term.termID=sectionplacement.termID"
				+ " left Join [Period] on [Period].periodID=sectionplacement.periodID"
				+ " left Join SchoolYear on SchoolYear.endYear=calendar.endYear"
				+ " left join PeriodSchedule on PeriodSchedule.periodScheduleID = Period.periodScheduleID"
				+ " where Section.courseID = :courseid and Section.teacherPersonId = (select personId from UserAccount where username = :username)"
				+ " and Trial.active = 1 ";

		String sqlAdmin = "select Distinct"
				+ " Section.sectionID, "
				+ " Section.number as sectionNumber,"
				+ " Section.courseID,"
				+ " Section.teacherPersonID,"
				+ " Term.name as termName,"
				+ " [Period].name as period,"
				+ " Periodschedule.name as periodDay,"
				+ " a.bbCourse_ID as linkedCourseId,"
				+ " a.bbCourse_NAME as linkedCourseName,"
				+ " (select count(*) as studentCount from Roster "
				+ " inner join UserAccount on UserAccount.personID = Roster.personID"
				+ " where Roster.sectionID = Section.sectionID"
				+ " and (Roster.endDate is null or Roster.endDate > GETDATE())) as studentNumber, "
				+ " 1 as teacherNumber, "
				+ " School.name as schoolName"
				+ " From Course"
				+ " left Join Calendar on calendar.calendarID=course.calendarID"
				+ " left join School with (nolock) on School.schoolID = Calendar.schoolID"
				+ " left Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID"
				+ " left Join Section on section.courseID=course.courseID"
				+ " left join SDWBlackboardSchedulerSISCourseSections b on b.sectionID = Section.sectionID"
				+ " left join SDWBlackboardSchedulerBBCourses a on a.bbCourseId = b.bbCourseID"
				+ " left Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1"
				+ " left Join SectionPlacement on SectionPlacement.sectionID=section.sectionID  and SectionPlacement.trialID=trial.trialID"
				+ " left Join Term on Term.termID=sectionplacement.termID"
				+ " left Join [Period] on [Period].periodID=sectionplacement.periodID"
				+ " left Join SchoolYear on SchoolYear.endYear=calendar.endYear"
				+ " left join PeriodSchedule on PeriodSchedule.periodScheduleID = Period.periodScheduleID"
				+ " where Section.courseID = :courseid "
				+ " and Trial.active = 1 ";

		MapSqlParameterSource params = new MapSqlParameterSource();

		List<ICSection> sections = null;
		HashMap<Long, ICSection> l_sectionList = new HashMap<Long, ICSection>();
		try {
			if (username.equals("admin")) {
				params.addValue("courseid", courseId);
				sections= template.query(sqlAdmin, params, new BeanPropertyRowMapper<ICSection>(ICSection.class));
			} else {
				params.addValue("courseid", courseId);
				params.addValue("username", username);
				sections= template.query(sql, params, new BeanPropertyRowMapper<ICSection>(ICSection.class));
			}

			for (ICSection section : sections) {
				ICSection l_temp = l_sectionList.get(section.getSectionID());

				if (l_temp != null) {
					if (l_temp.getTermName() != null) {
						if (!l_temp.getTermName().contains(section.getTermName())) {
							l_temp.setTermName (l_temp.getTermName().concat("/"+section.getTermName()));
						}
					}

					l_sectionList.put(l_temp.getSectionID(), l_temp);
				} else {
					l_sectionList.put(section.getSectionID(), section);

					// Now Update the Teacher Count
					List<ICTeacherList> l_teacherList = this.getTeacherList(section.getCourseID(), section.getSectionNumber());
					section.setTeacherNumber(l_teacherList.size());
				}


			}

			// Iterating HashMap through for loop
			for (Map.Entry<Long, ICSection> set : l_sectionList.entrySet()) {
				l_returnList.add(set.getValue());
			}
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return l_returnList;
	}



	@Transactional(readOnly=true)
	public ICSectionInfo getSectionInfo(String p_section) {
		mLog.info("Section ID: " + p_section);

		String sql = "select Course.calendarID, Section.sectionID, Section.courseID, Section.number as sectionNumber from Section"
				+ " left join Course on Course.courseID = Section.courseID"
				+ " where Section.sectionID = :sectionId";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("sectionId", p_section);
		ICSectionInfo l_section = null;

		try {
			l_section = (ICSectionInfo) template.queryForObject(sql, params, new BeanPropertyRowMapper(ICSectionInfo.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return l_section;
	}

	@Transactional(readOnly=true)
	public List<ICTeacherList> getTeacherList(Long p_courseId, int p_sectionNumber) {
		mLog.trace("getTeacherList called ...");

		String sql = "select distinct Course.courseID as courseId, Section.teacherPersonID as teacherId, UserAccount.username as username, 'Instructor' as role  "
				+ " from Section"
				+ " inner join Course on Course.courseID = Section.courseID "
				+ " inner join UserAccount on UserAccount.personID=Section.teacherPersonID "
				+ " where Course.courseID = :courseId and Section.number = :sectionNumber";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("courseId", p_courseId);
		params.addValue("sectionNumber", p_sectionNumber);
		List<ICTeacherList> l_teachers = null;

		try {
			l_teachers = template.query(sql,params,  new BeanPropertyRowMapper<ICTeacherList>(ICTeacherList.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return l_teachers;
	}

	@Transactional(readOnly=true)
	public Long getPersonId (String p_username) {
		mLog.trace("getPersonId called ...");

		String sql = "select personID from UserAccount where username=:userName";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userName", p_username);
		ICPerson l_personId = null;
		try {
			l_personId = (ICPerson) template.queryForObject(sql, params, new BeanPropertyRowMapper(ICPerson.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return l_personId.getPersonId();
	}

	@Transactional(readOnly=true)
	public List<ICEnrollment> getEnrollmentsForSections(List<String> sectionList) {
		mLog.trace("getEnrollments called ...");
		String sql = "select Distinct UserAccount.username, UserAccount.personID as personId, Section.sectionID as sectionId,"
				+ " 'Student' as role from Roster "
				+ " inner join Section on Section.sectionID = Roster.sectionID"
				+ " inner join [Identity] on [Identity].personID = Roster.personID"
				+ " inner join UserAccount on UserAccount.personID = Roster.personID"
				+ " where Roster.sectionID in (:sections)"
				+ " and (Roster.endDate is null or Roster.endDate > GETDATE())";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("sections", sectionList);
		List<ICEnrollment> enrollments = null;

		try {
			enrollments = template.query(sql,params,  new BeanPropertyRowMapper<ICEnrollment>(ICEnrollment.class));

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return enrollments;
	}

	@Transactional(readOnly=true)
	public List<ICTemplate> getTemplates() {
		mLog.trace("getTemplates called ...");
		String sql = "select bbMasterID as bbMasterId, bbCOURSE_ID as bbCourseId, bbCOURSE_NAME as bbCourseName, MasterLevel as masterLevel, MasterSubjectArea as masterSubjectArea from SDWBlackboardSchedulerMasterCourses order by bbCOURSE_NAME";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICTemplate> templates = null;
		try {
			templates = template.query(sql,params,  new BeanPropertyRowMapper<ICTemplate>(ICTemplate.class)); template.query(sql,params,  new BeanPropertyRowMapper<ICTemplate>(ICTemplate.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return templates;
	}

	@Transactional
	public void updateTemplates(List<ICTemplate> p_templates) {
		mLog.trace("updateTemplates called ...");

		String deleteSql = "delete from SDWBlackboardSchedulerMasterCourses";

		String insertSql = "insert into SDWBlackboardSchedulerMasterCourses "
				+ "  (bbCOURSE_ID, bbCOURSE_NAME, MasterLevel, MasterSubjectArea ) "
				+ " values (:bbCourseId, :bbCourseName, :masterLevel, :masterSubjectArea )";


		MapSqlParameterSource params = new MapSqlParameterSource();
		Number l_rows = null;
		try {
			// First Remove all the Messages
			l_rows = template.update(deleteSql, params);

			for (ICTemplate l_template : p_templates) {
				mLog.debug("Master ID: " + l_template.getBbMasterId());
				mLog.debug("Master BB Course ID: " + l_template.getBbCourseId());
				mLog.debug("Master BB Course Name: " + l_template.getBbCourseName());

				params.addValue("bbCourseId", l_template.getBbCourseId());
				params.addValue("bbCourseName", l_template.getBbCourseName());
				params.addValue("masterLevel", l_template.getMasterLevel());
				params.addValue("masterSubjectArea", l_template.getMasterSubjectArea());
				KeyHolder keyHolder = new GeneratedKeyHolder();
				int id = template.update(insertSql, params, keyHolder);
			}
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			throw new RuntimeException("Rollback");
		}
	}


	@Transactional(readOnly=true)
	public List<ICTeacher> getTeachers() {
		mLog.trace("getTeachers called ...");
		String sql = "select distinct UA.personID as personId, "
				+ "					UA.username as username, "
				+ "					idn.firstName+ ' '+idn.lastName as teacherName "
				+ "				 from UserAccount UA with (nolock)  "
				+ "				 cross join campusVersion v with (nolock)  "
				+ "				 join Person prs with (nolock) on UA.personID=prs.personID  "
				+ "					  and NOT (prs.staffNumber Is Null OR prs.staffNumber='')  "
				+ "				 join [Identity] idn with (nolock) on UA.personID=idn.personID  "
				+ "					  and prs.currentIdentityID=idn.identityID  "
				+ "					  and idn.districtID=v.districtID  "
				+ "				 join Contact con with (nolock) on prs.personID=con.personID  "
				+ "				 join Employment with (nolock) on Employment.personID=UA.personID  "
				+ "					  and (Employment.endDate>GETDATE() or Employment.endDate Is Null)  "
				+ "					  and Employment.districtID=v.districtID  "
				+ "				 join EmploymentAssignment ea with (nolock) on ea.personID=Employment.personID  "
				+ "					  and (ea.endDate>GETDATE() or ea.endDate Is Null)  "
				+ "				 join EmploymentAssignmentLocation eal with (nolock) on eal.assignmentID=ea.assignmentID  "
				+ "				 left join Department with (nolock) on ea.departmentID=Department.departmentID  "
				+ "					  and ea.schoolID=eal.schoolID  "
				+ "				 join School with (nolock) on eal.schoolID=School.schoolID  "
				+ "				 where (UA.[disable] Is Null OR UA.[disable]=0)  "
				+ "				  and (UA.lock Is Null OR UA.lock=0)  "
				+ "				  and NOT (UA.username Is Null OR UA.username='')  "
				+ "				  and NOT ((UA.LDAPDN Is Null or UA.LDAPDN='') and UA.isSAMLAccount = 0) "
				+ "				  and (UA.expiresDate Is Null or UA.expiresDate>=GETDATE())"
				+ "               and UA.isSAMLAccount = 1";

		String sql2 = "select [identity].personID, firstname, lastname, UserAccount.username, UserAccount.email from [Identity] "
				+ "Inner Join (select userAccount.personID, max(id.IdentityID)max_id  from UserAccount "
				+ "Inner join EmploymentAssignment on userAccount.PersonID = employmentAssignment.personID "
				+ "Inner Join [Identity]id on UserAccount.personID = id.personID "
				+ "where employmentAssignment.endDate is null or employmentAssignment.endDate >= GETDATE() "
				+ "group by UserAccount.personID)id on id.max_id = [Identity].identityID "
				+ "Inner Join UserAccount on [identity].personID = userAccount.personID "
				+ "where UserAccount.email like '%@waukesha.k12.wi.us'";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICTeacher> teachers = null;
		try {
			teachers = template.query(sql,params,  new BeanPropertyRowMapper<ICTeacher>(ICTeacher.class)); template.query(sql,params,  new BeanPropertyRowMapper<ICTemplate>(ICTemplate.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return teachers;
	}

	@Transactional(readOnly=true)
	public List<ICStudent> getStudents() {
		mLog.trace("getStudents called ...");
		String sql = "select distinct "
				+ " prs.personID as personId,"
				+ " UA.username as userName,"
				+ " IsNull(LTRIM(RTRIM(REPLACE(idn.firstName,',',' '))),'')  + ' ' +"
				+ " IsNull(LTRIM(RTRIM(REPLACE(idn.lastName,',',' '))),'') + ' - ' + IsNull(LTRIM(RTRIM(Prs.studentNumber)),'')   as studentName"
				+ " from Person prs with (nolock) "
				+ " cross join campusVersion v with (nolock) "
				+ " join Enrollment enr with (nolock) on prs.personID=enr.personID "
				+ "	  and (enr.endDate Is Null or enr.endDate>GETDATE()) "
				+ "	  and enr.startDate = ( "
				+ "	 		select max(enr1.startDate) "
				+ "	 		from Enrollment enr1 with (nolock) "
				+ "			join Calendar cal1 with (nolock) on cal1.calendarID=enr1.calendarID "
				+ "				and cal1.districtID=v.districtID "
				+ "			join schoolyear sy1 with (nolock) on sy1.endyear=cal1.endyear and sy1.active=1 "
				+ "	 		where enr1.districtID=v.districtID "
				+ "	 		and enr1.personID=prs.personID "
				+ "	 		and enr1.active=1 "
				+ "	 		and (enr1.noShow=0 or enr1.noShow Is Null) "
				+ "	 		and enr1.serviceType=enr.serviceType "
				+ "	 		and (enr1.endDate Is Null or enr1.endDate>GETDATE()) "
				+ "			and ((select min(trm.startDate) "
				+ "			 from Term trm with (nolock) "
				+ "			 join TermSchedule ts with (nolock) on ts.termScheduleID=trm.termScheduleID "
				+ "			 join ScheduleStructure ss with (nolock) on ss.structureID=ts.structureID "
				+ "			 and ss.calendarID=cal1.calendarID)>=GETDATE() "
				+ "			or enr1.startDate<GETDATE()) "
				+ "	 		) "
				+ "	  and enr.active=1 "
				+ " 	and (enr.noShow=0 or enr.noShow Is Null) "
				+ " 	and enr.serviceType IN ('P','S','N') "
				+ "	  and enr.districtID=v.districtID "
				+ " join Calendar cal with (nolock) on enr.calendarID=cal.calendarID "
				+ " 	and cal.districtID=v.districtID "
				+ "	  and ((select min(trm.startDate) "
				+ "	 from Term trm with (nolock) "
				+ "	 join TermSchedule ts with (nolock) on ts.termScheduleID=trm.termScheduleID "
				+ "	 join ScheduleStructure ss with (nolock) on ss.structureID=ts.structureID "
				+ "	 and ss.calendarID=cal.calendarID)>=GETDATE() "
				+ "	or enr.startDate<GETDATE()) "
				+ " join schoolyear sy with (nolock) on sy.endyear=cal.endyear and sy.active=1 "
				+ " join [Identity] idn with (nolock) on prs.currentIdentityID=idn.identityID "
				+ " 	and v.districtID=idn.districtID "
				+ " join Contact con with (nolock) on con.personID=prs.personID "
				+ " left join HouseholdMember HM with (nolock) on prs.personID=HM.personID "
				+ " 	and (HM.endDate Is Null or HM.endDate>GETDATE()) "
				+ "	and (HM.[secondary]=0 or HM.[secondary] Is Null) "
				+ " left join Household with (nolock) on HM.householdID=Household.householdID "
				+ " left join HouseholdLocation HL with (nolock) on Household.householdID=HL.householdID "
				+ " 	and (HL.endDate Is Null or HL.endDate>GETDATE()) "
				+ " 	and HL.startDate=(select max(hlz.startDate) from HouseholdLocation hlz where hlz.householdID=HL.householdID and (HLz.endDate Is Null or HLz.endDate>GETDATE()) and HLz.mailing=1) "
				+ " 	and HL.mailing=1 "
				+ "	  and (HL.[secondary]=0 or HL.[secondary] Is Null) "
				+ " left join [Address] addr with (nolock) on HL.addressID=addr.addressID "
				+ " 	and (addr.postOfficeBox=0 or addr.postOfficeBox Is Null) "
				+ " join School s with (nolock) on cal.schoolID=s.schoolID "
				+ " 	and s.districtID=v.districtID "
				+ " join CustomSchool csna with (nolock) on csna.schoolID=cal.schoolID "
				+ " join CampusAttribute cana with (nolock) on csna.attributeID=cana.attributeID "
				+ "	  and cana.[object]='School' "
				+ "	  and cana.element='schoolNameAbvr' "
				+ "	  and cana.dataType='textBox' "
				+ "	  and cana.deprecated<>1 "
				+ " join CustomSchool csCode with (nolock) on s.schoolID=csCode.schoolID "
				+ " join CampusAttribute caCode with (nolock) on csCode.attributeID=caCode.attributeID "
				+ " 	and caCode.[object]='School' "
				+ " 	and caCode.element='legacySchoolc' "
				+ " 	and caCode.dataType='textBox' "
				+ " 	and caCode.deprecated<>1 "
				+ " join CustomSchool csType with (nolock) on s.schoolID=csType.schoolID "
				+ " 	and csType.value IN ('ES','MS','HS') "
				+ " join CampusAttribute caType with (nolock) on csType.attributeID=caType.attributeID "
				+ "	  and caType.[object]='School' "
				+ " 	and caType.element='schoolType' "
				+ " 	and caType.dataType='dropList' "
				+ " 	and caType.deprecated<>1 "
				+ " join CampusDictionary cdEnr on enr.serviceType=cdEnr.code "
				+ " 	and cdEnr.active=1 "
				+ " join campusattribute caEnr on caEnr.attributeID=cdEnr.attributeID "
				+ " 	and caEnr.[object]='Enrollment' "
				+ " 	and caEnr.element='serviceType' "
				+ " 	and caEnr.dataType='dropList' "
				+ " 	and caEnr.deprecated<>1  "
				+ " join UserAccount UA with (nolock) on prs.personID=UA.personID "
				+ " 	and NOT (UA.username Is Null or UA.username='') "
				+ " 	and [UA].[disable]<>1 "
				+ " 	and [UA].[lock]<>1 	and (UA.expiresDate Is Null or UA.expiresDate >= GETDATE()) "
				+ " where NOT (prs.studentNumber IS Null or prs.studentNumber='') "
				+ "  and enr.districtID=v.districtID "
				+ "  and UA.isSAMLAccount = 1"
				+ " UNION "
				+ " select distinct "
				+ " prs.personID as personId,"
				+ " UA.username as userName,"
				+ " IsNull(LTRIM(RTRIM(REPLACE(idn.firstName,',',' '))),'')  + ' ' +"
				+ " IsNull(LTRIM(RTRIM(REPLACE(idn.lastName,',',' '))),'')  as studentName"
				+ " from Person prs with (nolock) "
				+ " cross join campusVersion v with (nolock) "
				+ " join Enrollment enr with (nolock) on prs.personID=enr.personID "
				+ "	  and enr.endDate>DATEADD(day,-60,GETDATE()) "
				+ "	  and enr.startDate = ( "
				+ "	 		select max(enr1.startDate) "
				+ "	 		from Enrollment enr1 with (nolock) "
				+ "			join Calendar cal1 with (nolock) on cal1.calendarID=enr1.calendarID "
				+ "				and cal1.districtID=v.districtID "
				+ "				and (cal1.summerSchool=0 or cal1.summerSchool Is Null) "
				+ "			join schoolyear sy1 with (nolock) on sy1.endyear=cal1.endyear and sy1.active=1 "
				+ "	 		where enr1.districtID=v.districtID "
				+ "	 		and enr1.personID=prs.personID "
				+ "	 		and enr1.active=1 "
				+ "	 		and (enr1.noShow=0 or enr1.noShow Is Null) "
				+ "	 		and enr1.serviceType=enr.serviceType "
				+ "	 		and enr1.endDate>DATEADD(day,-60,GETDATE()) "
				+ "	 	) "
				+ "	  and enr.active=1 "
				+ " 	and (enr.noShow=0 or enr.noShow Is Null) "
				+ " 	and enr.serviceType IN ('P','S','N') "
				+ "	  and enr.districtID=v.districtID "
				+ " join Calendar cal with (nolock) on enr.calendarID=cal.calendarID "
				+ " 	and cal.districtID=v.districtID "
				+ " 	and (cal.summerSchool=0 or cal.summerSchool Is Null) "
				+ " join schoolyear sy with (nolock) on sy.endyear=cal.endyear and sy.active=1 "
				+ " join [Identity] idn with (nolock) on prs.currentIdentityID=idn.identityID "
				+ " 	and v.districtID=idn.districtID "
				+ " join Contact con with (nolock) on con.personID=prs.personID "
				+ " left join HouseholdMember HM with (nolock) on prs.personID=HM.personID "
				+ " 	and (HM.endDate Is Null or HM.endDate>GETDATE()) "
				+ "	  and (HM.[secondary]=0 or HM.[secondary] Is Null) "
				+ " left join Household with (nolock) on HM.householdID=Household.householdID "
				+ " left join HouseholdLocation HL with (nolock) on Household.householdID=HL.householdID "
				+ " 	and (HL.endDate Is Null or HL.endDate>GETDATE()) "
				+ " 	and HL.startDate=(select max(hlz.startDate) from HouseholdLocation hlz where hlz.householdID=HL.householdID and (HLz.endDate Is Null or HLz.endDate>GETDATE()) and HLz.mailing=1) "
				+ " 	and HL.mailing=1 "
				+ "	  and (HL.[secondary]=0 or HL.[secondary] Is Null) "
				+ " left join [Address] addr with (nolock) on HL.addressID=addr.addressID "
				+ " 	and (addr.postOfficeBox=0 or addr.postOfficeBox Is Null) "
				+ " join School s with (nolock) on cal.schoolID=s.schoolID "
				+ " 	and s.districtID=v.districtID "
				+ " join CustomSchool csna with (nolock) on csna.schoolID=cal.schoolID "
				+ " join CampusAttribute cana with (nolock) on csna.attributeID=cana.attributeID "
				+ "	  and cana.[object]='School' "
				+ "	  and cana.element='schoolNameAbvr' "
				+ "	  and cana.dataType='textBox' "
				+ "	  and cana.deprecated<>1 "
				+ " join CustomSchool csCode with (nolock) on s.schoolID=csCode.schoolID "
				+ " join CampusAttribute caCode with (nolock) on csCode.attributeID=caCode.attributeID "
				+ " 	and caCode.[object]='School' "
				+ " 	and caCode.element='legacySchoolc' "
				+ " 	and caCode.dataType='textBox' "
				+ " 	and caCode.deprecated<>1 "
				+ " join CustomSchool csType with (nolock) on s.schoolID=csType.schoolID "
				+ " 	and csType.value IN ('ES','MS','HS') "
				+ " join CampusAttribute caType with (nolock) on csType.attributeID=caType.attributeID "
				+ "	  and caType.[object]='School' "
				+ " 	and caType.element='schoolType' "
				+ " 	and caType.dataType='dropList' "
				+ " 	and caType.deprecated<>1 "
				+ " join CampusDictionary cdEnr on enr.serviceType=cdEnr.code "
				+ " 	and cdEnr.active=1 "
				+ " join campusattribute caEnr on caEnr.attributeID=cdEnr.attributeID "
				+ " 	and caEnr.[object]='Enrollment' "
				+ " 	and caEnr.element='serviceType' "
				+ " 	and caEnr.dataType='dropList' "
				+ " 	and caEnr.deprecated<>1  "
				+ " join UserAccount UA with (nolock) on prs.personID=UA.personID "
				+ " 	and NOT (UA.username Is Null or UA.username='') "
				+ " 	and [UA].[disable]<>1 "
				+ " 	and [UA].[lock]<>1 	and (UA.expiresDate Is Null or UA.expiresDate >= GETDATE()) "
				+ " where NOT (prs.studentNumber IS Null or prs.studentNumber='') "
				+ "  and enr.districtID=v.districtID "
				+ "  and NOT prs.personID in ( "
				+ "	select enrE.personID "
				+ "	from Enrollment enrE with (nolock) "
				+ "	join Calendar calE with (nolock) on calE.calendarID=enrE.calendarID "
				+ "		and calE.districtID=v.districtID "
				+ "		and (calE.summerSchool=0 or calE.summerSchool Is Null) "
				+ "	join schoolyear syE with (nolock) on syE.endyear=calE.endyear and syE.active=1 "
				+ "	where (enrE.endDate Is Null or enrE.endDate>GETDATE()) "
				+ "	and enrE.active=1 "
				+ "	and (enr.noShow=0 or enrE.noShow Is Null) "
				+ "	and enrE.serviceType IN ('P','S','N') "
				+ "	and enrE.districtID=v.districtID) "
				+ " and IsNull( "
				+ "	(select distinct 'Y' "
				+ "	from enrollment enrz with (nolock) "
				+ "	join Calendar calz with (nolock) on calz.calendarID=enrz.calendarID "
				+ "		and calz.districtID=v.districtID "
				+ "		and (calz.summerSchool=0 or calz.summerSchool Is Null) "
				+ "		and calz.endyear=(select top 1 endyear from SchoolYear with (nolock) where active=1)+1 "
				+ "	join School with (nolock) on School.schoolID=calz.schoolID "
				+ "		and School.districtID=v.districtID "
				+ "	join CustomSchool csTypez with (nolock) on csTypez.schoolID=School.schoolID "
				+ "		and csTypez.attributeID=caType.attributeID "
				+ "	where enrz.personID=prs.personID "
				+ "	and enrz.active=1 "
				+ "	and (enrz.noShow=0 or enrz.noShow Is Null) "
				+ "	and enrz.serviceType IN ('P','S','N')),'N') = 'Y'"
				+ " and UA.isSAMLAccount = 1";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICStudent> students = null;
		try {
			students = template.query(sql,params,  new BeanPropertyRowMapper<ICStudent>(ICStudent.class)); template.query(sql,params,  new BeanPropertyRowMapper<ICTemplate>(ICTemplate.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return students;
	}

	@Transactional(readOnly=true)
	public List<ICUser> getSISStudents() {
		mLog.info("getSISStudents called ...");
		String sql = "select distinct 'A' as [Union], "
				+ " prs.personID as personId,"
				+ " IsNull(LTRIM(RTRIM(Prs.studentNumber)),'') studentNumber,"
				+ " IsNull(LTRIM(RTRIM(REPLACE(idn.firstName,',',' '))),'') as firstName,"
				+ " IsNull(LTRIM(RTRIM(REPLACE(idn.middleName,',',' '))),'') as middleName,"
				+ " IsNull(LTRIM(RTRIM(REPLACE(idn.lastName,',',' '))),'') as lastName,"
				+ " IsNull(CONVERT(varchar(10),idn.birthdate,112),'') as birthDate,"
				+ " IsNull(idn.gender,'') as gender,"
				+ " (CASE WHEN Household.phone Is Null THEN '' ELSE LTRIM(RTRIM(Household.phone)) END) as homePhone1,"
				+ " (CASE WHEN con.homePhone Is Null THEN '' ELSE LTRIM(RTRIM(con.homePhone)) END) as homePhone2,"
				+ " (CASE WHEN con.cellPhone Is Null THEN '' ELSE LTRIM(RTRIM(con.cellPhone)) END) as cellPhone,"
				+ " (CASE WHEN con.email Is Null THEN '' ELSE LTRIM(RTRIM(REPLACE(con.email,',','.'))) END) as email1,"
				+ " (CASE WHEN con.secondaryEmail Is Null THEN '' ELSE LTRIM(RTRIM(REPLACE(con.secondaryEmail,',','.'))) END) as email2,"
				+ " IsNull(enr.grade,'') as gradeLevel,"
				+ " enr.serviceType as serviceType,"
				+ " customSchool.value as institutionRole,"
				+ " s.number as schoolNumber,"
				+ " cal.name as calendarName,"
				+ " LTRIM(RTRIM(REPLACE((CASE WHEN addr.number Is Null THEN '' ELSE LTRIM(RTRIM(addr.number)) END)+(CASE WHEN addr.prefix Is Null THEN '' ELSE ' '+LTRIM(RTRIM(addr.prefix)) END)+(CASE WHEN addr.street Is Null THEN '' ELSE ' '+LTRIM(RTRIM(REPLACE(addr.street,',',' '))) END)+(CASE WHEN addr.tag Is Null THEN '' ELSE ' '+LTRIM(RTRIM(addr.tag)) END)+(CASE WHEN addr.dir Is Null THEN '' ELSE ' '+LTRIM(RTRIM(addr.dir)) END),',',' '))) as address,"
				+ " IsNull(LTRIM(RTRIM(addr.apt)),'') as apartment,"
				+ " IsNull(LTRIM(RTRIM(addr.city)),'') as city,"
				+ " IsNull(LTRIM(RTRIM(addr.[state])),'') as state,"
				+ " IsNull(LTRIM(RTRIM(addr.zip)),'') as zip,"
				+ " UA.username as userName "
				+ "				 from Person prs with (nolock) "
				+ "				 join Enrollment enr with (nolock) on prs.personID=enr.personID "
				+ "					and (enr.endDate Is Null or enr.endDate>GETDATE()) "
				+ "					and enr.active=1 "
				+ "				 	and enr.serviceType IN ('P','S','N')  "
				+ "				 join Calendar cal with (nolock) on enr.calendarID=cal.calendarID "
				+ "				 join schoolyear sy with (nolock) on sy.endyear=cal.endyear and sy.active=1 "
				+ "				 join [Identity] idn with (nolock) on prs.currentIdentityID=idn.identityID "
				+ "				 join Contact con with (nolock) on con.personID=prs.personID "
				+ "				 left join HouseholdMember HM with (nolock) on prs.personID=HM.personID "
				+ "				 	and (HM.endDate Is Null or HM.endDate>GETDATE()) "
				+ "					and (HM.[secondary]=0 or HM.[secondary] Is Null) "
				+ "				 left join Household with (nolock) on HM.householdID=Household.householdID "
				+ "				 left join HouseholdLocation HL with (nolock) on Household.householdID=HL.householdID "
				+ "				 	and (HL.endDate Is Null or HL.endDate>GETDATE()) "
				+ "				 	and HL.startDate=(select max(hlz.startDate) from HouseholdLocation hlz where hlz.householdID=HL.householdID and (HLz.endDate Is Null or HLz.endDate>GETDATE()) and HLz.mailing=1) "
				+ "				 	and HL.mailing=1 "
				+ "					and (HL.[secondary]=0 or HL.[secondary] Is Null) "
				+ "				 left join [Address] addr with (nolock) on HL.addressID=addr.addressID "
				+ "				 	and (addr.postOfficeBox=0 or addr.postOfficeBox Is Null) "
				+ "				 join School s with (nolock) on cal.schoolID=s.schoolID "
				+ "				 join customschool  on s.schoolID = customschool.schoolID and customschool.attributeID = 621 "
				+ "				 join UserAccount UA with (nolock) on prs.personID=UA.personID "
				+ "				 	and NOT (UA.username Is Null or UA.username='') "
				+ "				 	and [UA].[disable]<>1 "
				+ "				 	and [UA].[lock]<>1 	and (UA.expiresDate Is Null or UA.expiresDate >= GETDATE()) "
				+ "				 where NOT (prs.studentNumber IS Null or prs.studentNumber='') ";



		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICUser> users = null;
		try {
			users = template.query(sql,params,  new BeanPropertyRowMapper<ICUser>(ICUser.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return users;
	}

	@Transactional(readOnly=true)
	public List<ICStaff> getSISStaff() {
		mLog.info("getSISStaff called ...");
		String sql = "select distinct UA.personID as personId,"
				+ "	UA.username as userName,"
				+ " prs.staffNumber as staffNumber,"
				+ " School.number  as institutionRole,"
				+ "	IsNull(LTRIM(RTRIM(REPLACE(idn.firstName,',',' '))),'') as firstName,"
				+ "	IsNull(LTRIM(RTRIM(REPLACE(idn.middleName,',',' '))),'') as middleName,"
				+ "	IsNull(LTRIM(RTRIM(REPLACE(idn.lastName,',',' '))),'') as lastName ,"
				+ "	IsNull(LTRIM(RTRIM(REPLACE(idn.suffix,',',' '))),'') as nameSuffix,"
				+ "	IsNull(LTRIM(RTRIM(REPLACE(idn.alias,',',' '))),'') as alias,"
				+ "	IsNull(CONVERT(varchar(10),idn.birthdate,112),'') as birthDate,"
				+ "	IsNull(idn.gender,'') as gender,"
				+ " (CASE WHEN ea.teacher Is Null or ea.teacher<>1 THEN 'Staff' ELSE 'Faculty' END) jobTitle,"
				+ " (CASE WHEN con.workPhone Is Null THEN '' ELSE LTRIM(RTRIM(con.workPhone)) END) as workPhone,"
				+ " (CASE WHEN con.email Is Null THEN '' ELSE LTRIM(RTRIM(REPLACE(con.email,',',' '))) END) as email1,"
				+ " (select count (*) from School sz with (nolock) "
				+ "	    join EmploymentAssignmentLocation ealz with (nolock) on ealz.schoolID=sz.schoolID "
				+ "	    join EmploymentAssignment eaz with (nolock) on eaz.assignmentID=ealz.assignmentID  "
				+ "		and eaz.personID=UA.personID  "
				+ "		and (eaz.endDate>GETDATE() or eaz.endDate Is Null)) as roleCount "
				+ " from UserAccount UA with (nolock) "
				+ " cross join campusVersion v with (nolock) "
				+ " join Person prs with (nolock) on UA.personID=prs.personID "
				+ "	  and NOT (prs.staffNumber Is Null OR prs.staffNumber='') "
				+ " join [Identity] idn with (nolock) on UA.personID=idn.personID "
				+ "	  and prs.currentIdentityID=idn.identityID "
				+ "	  and idn.districtID=v.districtID "
				+ " join Contact con with (nolock) on prs.personID=con.personID "
				+ " join Employment with (nolock) on Employment.personID=UA.personID "
				+ "	  and (Employment.endDate>GETDATE() or Employment.endDate Is Null) "
				+ "	  and Employment.districtID=v.districtID "
				+ " join EmploymentAssignment ea with (nolock) on ea.personID=Employment.personID "
				+ "	  and (ea.endDate>GETDATE() or ea.endDate Is Null) "
				+ " join EmploymentAssignmentLocation eal with (nolock) on eal.assignmentID=ea.assignmentID "
				+ " left join Department with (nolock) on ea.departmentID=Department.departmentID "
				+ "	  and ea.schoolID=eal.schoolID "
				+ " join School with (nolock) on eal.schoolID=School.schoolID "
				+ " where (UA.[disable] Is Null OR UA.[disable]=0) "
				+ "  and (UA.lock Is Null OR UA.lock=0) "
				+ "  and NOT (UA.username Is Null OR UA.username='') "
				+ "  and NOT ((UA.LDAPDN Is Null or UA.LDAPDN='') and UA.isSAMLAccount = 0)"
				+ "  and (UA.expiresDate Is Null or UA.expiresDate>=GETDATE()) ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICStaff> staff = null;
		try {
			staff = template.query(sql,params,  new BeanPropertyRowMapper<ICStaff>(ICStaff.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return staff;
	}

	@Transactional(readOnly=true)
	public List<ICGuardian> getSISGuardians() {
		mLog.info("getGuardian called ...");
		String sql = "select distinct prs.personID as personId, "
				+ "  RelatedPair.personID2 as bbPersonID, "
				+ "  cdEnr.seq,RelatedPair.seq relatedPairSeq, "
				+ "  prs.studentNumber as studentNumber, "
				+ "  UAs.Username as studentUsername, "
				+ "  HM.householdID, "
				+ "  RPhm.householdID RPhouseholdID, "
				+ "  IsNull(Household.legacyKey,'') householdLegacyKey, "
				+ "  (CASE WHEN Household.phonePrivate=1 OR Household.phone Is Null THEN '' ELSE LTRIM(RTRIM(Household.phone)) END) householdPhone, "
				+ "  HL.addressID hlAddressID, "
				+ "  (CASE WHEN RPprs.legacyKey Is Null OR LTRIM(RTRIM(RPprs.legacyKey))='' OR IsNumeric(SUBSTRING(RPprs.legacyKey,2,LEN(RPprs.legacyKey)-1))=0 OR SUBSTRING(RPprs.legacyKey,1,1)<>'C' OR LEN(RPprs.legacyKey)<>8 THEN 'C'+CAST(RPprs.personID as varchar(10)) ELSE SUBSTRING(RPprs.legacyKey,2,LEN(RPprs.legacyKey)-1) END) as contactNumber, "
				+ "  RelatedPair.name RPrelationship, "
				+ "  IsNull(LTRIM(RTRIM(REPLACE(RPidn.firstName,',',' '))),'') as firstName, "
				+ "  IsNull(LTRIM(RTRIM(REPLACE(RPidn.middleName,',',' '))),'') as middleName, "
				+ "  IsNull(LTRIM(RTRIM(REPLACE(RPidn.lastName,',',' '))),'') as lastName, "
				+ "  IsNull(LTRIM(RTRIM(REPLACE(RPidn.suffix,',',' '))),'') RPnamesfx, "
				+ "  (CASE WHEN Household.phone Is Null THEN '' ELSE LTRIM(RTRIM(Household.phone)) END) HPhomePhone, "
				+ "  (CASE WHEN RPcon.homePhone Is Null THEN '' ELSE LTRIM(RTRIM(RPcon.homePhone)) END) RPhomePhone, "
				+ "  (CASE WHEN RPcon.cellPhone Is Null THEN '' ELSE LTRIM(RTRIM(RPcon.cellPhone)) END) RPcellPhone, "
				+ "  (CASE WHEN RPcon.email Is Null THEN '' ELSE LTRIM(RTRIM(REPLACE(RPcon.email,',','.'))) END) as email, "
				+ "  RPhl.addressID RPhlAddressID, "
				+ "  IsNull((CASE WHEN RPh.legacyKey Is Null OR LTRIM(RTRIM(RPh.legacyKey))='' THEN 'C'+CAST(RPhm.householdID as varchar(10)) ELSE SUBSTRING(RPh.legacyKey,2,LEN(RPh.legacyKey)-1) END),'') RPhouseholdNumber, "
				+ "  LTRIM(RTRIM(REPLACE((CASE WHEN RPaddr.number Is Null THEN '' ELSE LTRIM(RTRIM(RPaddr.number)) END)+(CASE WHEN RPaddr.prefix Is Null THEN '' ELSE ' '+LTRIM(RTRIM(RPaddr.prefix)) END)+(CASE WHEN RPaddr.street Is Null THEN '' ELSE ' '+LTRIM(RTRIM(REPLACE(RPaddr.street,',',' '))) END)+(CASE WHEN RPaddr.tag Is Null THEN '' ELSE ' '+LTRIM(RTRIM(RPaddr.tag)) END)+(CASE WHEN RPaddr.dir Is Null THEN '' ELSE ' '+LTRIM(RTRIM(RPaddr.dir)) END),',',' '))) as street1, "
				+ "  IsNull(LTRIM(RTRIM(RPaddr.apt)),'') RPApt, "
				+ "  IsNull(LTRIM(RTRIM(RPaddr.city)),'') as city, "
				+ "  IsNull(LTRIM(RTRIM(RPaddr.[state])),'') as state, "
				+ "  IsNull(LTRIM(RTRIM(RPaddr.zip)),'') as zip, "
				+ "  IsNull(CAST(CSBbUsername.customID as varchar(10)),'') CSbbUsernameCustomID, "
				+ "  IsNull(CSBbUsername.value,'') as bbUsername, "
				+ "  IsNull(CAST(CSbbPassword.customID as varchar(10)),'') CSbbPasswordCustomID, "
				+ "  IsNull(CSbbPassword.value,'') as bbPassword "
				+ " from Person prs with (nolock) "
				+ "  join Enrollment enr with (nolock) on prs.personID=enr.personID  "
				+ "	   and (enr.endDate Is Null or enr.endDate>GETDATE())  "
				+ "	   and enr.startDate=(  "
				+ "		select max(enr1.startDate)  "
				+ "		from Enrollment enr1 with (nolock)  "
				+ "		where enr1.calendarID=enr.calendarID  "
				+ "		and enr1.districtID=enr.districtID  "
				+ "		and enr1.personID=prs.personID  "
				+ "		and enr1.active=1  "
				+ "		and (enr1.noShow=0 or enr1.noShow Is Null)  "
				+ "		and enr1.serviceType=enr.serviceType  "
				+ "		and (enr1.endDate Is Null or enr1.endDate>GETDATE())  "
				+ "	)  "
				+ "	and enr.active=1  "
				+ "	and (enr.noShow=0 or enr.noShow Is Null)  "
				+ "	and enr.serviceType IN ('P') "
				+ " join [Identity] idn with (nolock) on prs.currentIdentityID=idn.identityID  "
				+ "	  and prs.personID=idn.personID  "
				+ "	  and enr.districtID=idn.districtID "
				+ " join Contact con with (nolock) on con.personID=prs.personID "
				+ " join UserAccount UAs with (nolock) on UAs.personID=prs.personID "
				+ "	  and NOT (UAs.username Is Null or LTRIM(RTRIM(UAs.username))='') "
				+ "	  and [UAs].[disable]<>1  "
				+ "	  and [UAs].[lock]<>1  "
				+ "	  and (UAs.expiresDate Is Null or UAs.expiresDate>=GETDATE())  "
				+ " join HouseholdMember HM with (nolock) on prs.personID=HM.personID  "
				+ "	  and (HM.endDate Is Null or HM.endDate>GETDATE())  "
				+ "	  and (HM.[secondary] Is Null or HM.[secondary]=0)  "
				+ " join Household with (nolock) on HM.householdID=Household.householdID  "
				+ " join HouseholdLocation HL with (nolock) on Household.householdID=HL.householdID  "
				+ "	  and (HL.endDate Is Null or HL.endDate>GETDATE())  "
				+ "	  and HL.startDate=(select max(hlz.startDate) from HouseholdLocation hlz where hlz.householdID=HL.householdID and (HLz.endDate Is Null or HLz.endDate>GETDATE()) and (HLz.[private] Is Null or HLz.[private]=0) and HLz.mailing=1 and (HLz.[secondary] Is Null or HLz.[secondary]=0))  "
				+ "	  and (HL.[private] Is Null or HL.[private]=0)  "
				+ "	  and HL.mailing=1  "
				+ "	  and (HL.[secondary] Is Null or HL.[secondary]=0)  "
				+ " join Calendar cal with (nolock) on enr.calendarID=cal.calendarID  "
				+ " join schoolyear sy with (nolock) on sy.endyear=cal.endyear and sy.active=1  "
				+ " join School with (nolock) on cal.schoolID=School.schoolID  "
				+ " join CustomSchool cslsc on cslsc.schoolID=School.schoolID  "
				+ " join CampusAttribute calsc on cslsc.attributeID=calsc.attributeID "
				+ "	  and calsc.[object]='School' "
				+ "	  and calsc.element='legacySchoolc' "
				+ "	  and calsc.dataType='textBox' "
				+ "	  and calsc.deprecated<>1 "
				+ " join CustomSchool csType with (nolock) on School.schoolID=csType.schoolID  "
				+ "	  and csType.value IN ('ES','MS','HS')  "
				+ " join CampusAttribute caType with (nolock) on csType.attributeID=caType.attributeID  "
				+ " 	and caType.[object]='School'  "
				+ " 	and caType.element='schoolType'  "
				+ " 	and caType.dataType='dropList'  "
				+ " 	and caType.deprecated<>1  "
				+ " join CampusDictionary cdEnr on enr.serviceType=cdEnr.code  "
				+ "	  and cdEnr.active=1  "
				+ " join campusattribute caEnr on caEnr.attributeID=cdEnr.attributeID  "
				+ "	  and caEnr.[object]='Enrollment'  "
				+ "	  and caEnr.element='serviceType'  "
				+ "	  and caEnr.dataType='dropList'  "
				+ "	  and caEnr.deprecated<>1  "
				+ " join RelatedPair with (nolock) on prs.personID=RelatedPair.personID1  "
				+ "	  and ((NOT RelatedPair.guardian Is Null and RelatedPair.guardian=1) or RelatedPair.name='Blackboard')  "
				+ "	  and (RelatedPair.endDate Is Null or RelatedPair.endDate>GETDATE())  "
				+ " join HouseholdMember RPhm with (nolock) on RelatedPair.personID2=RPhm.personID  "
				+ "	  and (RPhm.endDate Is Null or RPhm.endDate>GETDATE())  "
				+ " left join Household RPh with (nolock) on RPhm.householdID=RPh.householdID  "
				+ " left join HouseholdLocation RPhl with (nolock) on RPh.householdID=RPhl.householdID  "
				+ "	  and (RPhl.endDate Is Null or RPhl.endDate>GETDATE())  "
				+ "	  and RPhl.startDate=(select max(startDate) from HouseholdLocation where householdID=RPhl.householdID) "
				+ "	  and RPhl.mailing=1 "
				+ " left join [Address] RPaddr with (nolock) on RPhl.addressID=RPaddr.addressID  "
				+ "	  and (RPaddr.postOfficeBox Is Null or RPaddr.postOfficeBox=0)  "
				+ " join Person RPprs with (nolock) on RPprs.personID=RelatedPair.personID2  "
				+ " join [Identity] RPidn with (nolock) on RPprs.currentIdentityID=RPidn.identityID  "
				+ "	  and RPprs.personID=RPidn.personID  "
				+ " join Contact RPcon on RPprs.personID=RPcon.personID  "
				+ " left join CustomStudent CSBbUsername with (nolock) on CSBbUsername.personID=RelatedPair.personID2  "
				+ "	  and CSBbUsername.enrollmentID Is Null  "
				+ "	  and CSBbUsername.attributeID= 672  "
				+ " left join CustomStudent CSbbPassword with (nolock) on CSbbPassword.personID=RelatedPair.personID2  "
				+ "	  and CSbbPassword.enrollmentID Is Null  "
				+ "	  and CSbbPassword.attributeID= 673  "
				+ " where NOT (prs.studentNumber Is Null or LTRIM(RTRIM(prs.studentNumber))='') "
				+ " UNION "
				+ " select distinct prs.personID as personId, "
				+ " OB.personIDobserver as bbPersonID, "
				+ " cdEnr.seq, "
				+ " 9999 relatedPairSeq, "
				+ " prs.studentNumber as studentNumber, "
				+ " UAs.Username as studentUsername, "
				+ " HM.householdID, "
				+ " -1 RPhouseholdID, "
				+ " IsNull(Household.legacyKey,'') householdLegacyKey, "
				+ " (CASE WHEN Household.phonePrivate=1 OR Household.phone Is Null THEN '' ELSE LTRIM(RTRIM(Household.phone)) END) householdPhone, "
				+ " HL.addressID hlAddressID, "
				+ " (CASE WHEN RPprs.legacyKey Is Null OR LTRIM(RTRIM(RPprs.legacyKey))='' OR IsNumeric(SUBSTRING(RPprs.legacyKey,2,LEN(RPprs.legacyKey)-1))=0 OR SUBSTRING(RPprs.legacyKey,1,1)<>'C' OR LEN(RPprs.legacyKey)<>8 THEN 'C'+CAST(RPprs.personID as varchar(10)) ELSE SUBSTRING(RPprs.legacyKey,2,LEN(RPprs.legacyKey)-1) END) as contactNumber, "
				+ " 'Observer' RPrelationship, "
				+ " IsNull(LTRIM(RTRIM(REPLACE(RPidn.firstName,',',' '))),'') as firstName, "
				+ " IsNull(LTRIM(RTRIM(REPLACE(RPidn.middleName,',',' '))),'') as middleName, "
				+ " IsNull(LTRIM(RTRIM(REPLACE(RPidn.lastName,',',' '))),'') as lastName, "
				+ " IsNull(LTRIM(RTRIM(REPLACE(RPidn.suffix,',',' '))),'') RPnamesfx, "
				+ " '' HPhomePhone, "
				+ " '' RPhomePhone, "
				+ " '' RPcellPhone, "
				+ " (CASE WHEN RPcon.email Is Null THEN '' ELSE LTRIM(RTRIM(REPLACE(RPcon.email,',','.'))) END) as email, "
				+ " -1 RPhlAddressID, "
				+ " '' RPhouseholdNumber, "
				+ " '' street1, "
				+ " '' RPApt, "
				+ " '' as city, "
				+ " '' as state, "
				+ " '' as zip, "
				+ " IsNull(CAST(CSBbUsername.customID as varchar(10)),'') CSbbUsernameCustomID, "
				+ " IsNull(CSBbUsername.value,'')  as bbUsername, "
				+ " IsNull(CAST(CSbbPassword.customID as varchar(10)),'') CSbbPasswordCustomID, "
				+ " IsNull(CSbbPassword.value,'') as bbPassword "
				+ " from Person prs with (nolock)  "
				+ " join Enrollment enr with (nolock) on prs.personID=enr.personID  "
				+ " 	and (enr.endDate Is Null or enr.endDate>GETDATE())  "
				+ " 	and enr.startDate=(  "
				+ "	 		select max(enr1.startDate)  "
				+ "	 		from Enrollment enr1 with (nolock)  "
				+ "	 		where enr1.calendarID=enr.calendarID  "
				+ "	 		and enr1.districtID=enr.districtID  "
				+ "	 		and enr1.personID=prs.personID  "
				+ "	 		and enr1.active=1  "
				+ "	 		and (enr1.noShow=0 or enr1.noShow Is Null)  "
				+ "	 		and enr1.serviceType=enr.serviceType  "
				+ "	 		and (enr1.endDate Is Null or enr1.endDate>GETDATE())  "
				+ "	 	)  "
				+ "	  and enr.active=1  "
				+ "	  and (enr.noShow=0 or enr.noShow Is Null)  "
				+ "	  and enr.serviceType IN ('P')  "
				+ " join [Identity] idn with (nolock) on prs.currentIdentityID=idn.identityID  "
				+ "	  and prs.personID=idn.personID  "
				+ " 	and enr.districtID=idn.districtID  "
				+ " join Contact con with (nolock) on con.personID=prs.personID  "
				+ " join UserAccount UAs with (nolock) on UAs.personID=prs.personID  "
				+ "	  and NOT (UAs.username Is Null or LTRIM(RTRIM(UAs.username))='')  "
				+ "	  and [UAs].[disable]<>1  "
				+ "	  and [UAs].[lock]<>1  "
				+ "	 and (UAs.expiresDate Is Null or UAs.expiresDate>=GETDATE())  "
				+ " join HouseholdMember HM with (nolock) on prs.personID=HM.personID  "
				+ " 	and (HM.endDate Is Null or HM.endDate>GETDATE())  "
				+ " 	and (HM.[secondary] Is Null or HM.[secondary]=0)  "
				+ " join Household with (nolock) on HM.householdID=Household.householdID  "
				+ " join HouseholdLocation HL with (nolock) on Household.householdID=HL.householdID  "
				+ " 	and (HL.endDate Is Null or HL.endDate>GETDATE())  "
				+ " 	and HL.startDate=(select max(hlz.startDate) from HouseholdLocation hlz where hlz.householdID=HL.householdID and (HLz.endDate Is Null or HLz.endDate>GETDATE()) and (HLz.[private] Is Null or HLz.[private]=0) and HLz.mailing=1)  "
				+ " 	and (HL.[private] Is Null or HL.[private]=0)  "
				+ " 	and HL.mailing=1  "
				+ " join Calendar cal with (nolock) on enr.calendarID=cal.calendarID  "
				+ " join schoolyear sy with (nolock) on sy.endyear=cal.endyear  "
				+ "	  and sy.active=1  "
				+ " join School with (nolock) on cal.schoolID=School.schoolID  "
				+ " join CustomSchool cslsc on cslsc.schoolID=School.schoolID  "
				+ " join CampusAttribute calsc on cslsc.attributeID=calsc.attributeID  "
				+ " 	and calsc.[object]='School'  "
				+ " 	and calsc.element='legacySchoolc'  "
				+ " 	and calsc.dataType='textBox'  "
				+ " 	and calsc.deprecated<>1  "
				+ " join CustomSchool csType with (nolock) on School.schoolID=csType.schoolID  "
				+ " 	and csType.value IN ('ES','MS','HS')  "
				+ " join CampusAttribute caType with (nolock) on csType.attributeID=caType.attributeID  "
				+ "  	and caType.[object]='School'  "
				+ "  	and caType.element='schoolType'  "
				+ "  	and caType.dataType='dropList'  "
				+ "  	and caType.deprecated<>1  "
				+ " join CampusDictionary cdEnr on enr.serviceType=cdEnr.code  "
				+ " 	and cdEnr.active=1  "
				+ " join campusattribute caEnr on caEnr.attributeID=cdEnr.attributeID  "
				+ " 	and caEnr.[object]='Enrollment'  "
				+ " 	and caEnr.element='serviceType'  "
				+ " 	and caEnr.dataType='dropList'  "
				+ " 	and caEnr.deprecated<>1  "
				+ " join SDWObserver OB with (nolock) on OB.personIDstudent=prs.personID  "
				+ "	  and (OB.endDate Is Null or OB.endDate>GETDATE())  "
				+ " join Person RPprs with (nolock) on RPprs.personID=OB.personIDobserver  "
				+ " join [Identity] RPidn with (nolock) on RPprs.currentIdentityID=RPidn.identityID  "
				+ "	  and RPprs.personID=RPidn.personID  "
				+ " join Contact RPcon on RPprs.personID=RPcon.personID  "
				+ " left join CustomStudent CSBbUsername with (nolock) on CSBbUsername.personID=OB.personIDobserver   "
				+ " 	and CSBbUsername.enrollmentID Is Null "
				+ " 	and CSBbUsername.attributeID = 672  "
				+ " left join CustomStudent CSbbPassword with (nolock) on CSbbPassword.personID=OB.personIDobserver  "
				+ " 	and CSbbPassword.enrollmentID Is Null  "
				+ " 	and CSbbPassword.attributeID= 673  "
				+ " where NOT (prs.studentNumber Is Null or LTRIM(RTRIM(prs.studentNumber))='')"
				+ " ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICGuardian> guardians= null;
		try {
			guardians= template.query(sql,params,  new BeanPropertyRowMapper<ICGuardian>(ICGuardian.class));
			for (ICGuardian l_guardian : guardians) {
				if (l_guardian.getBbUsername() == null || l_guardian.getBbUsername().isBlank()) {
					l_guardian.setBbUsername("C"+l_guardian.getBbPersonId());
					this.insertBBUsername(l_guardian.getBbUsername(), l_guardian.getPersonId());

				}
				if (l_guardian.getBbPassword() == null || l_guardian.getBbPassword().isBlank()) {
					Random random = new Random();
					int number = random.nextInt(900) + 100; // Generates number between 100 and 999
					String l_password = l_guardian.getFirstName().substring(0, 1).toLowerCase() + l_guardian.getLastName().toLowerCase();
					if (l_password.length() > 6) {
						l_guardian.setBbPassword(l_password.substring(0,5)+number);
					} else {
						l_guardian.setBbPassword(l_password+number);
					}
					this.insertBBPassword(l_guardian.getBbPassword(), l_guardian.getPersonId());
				}
			}
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return guardians;
	}

	@Transactional
	public Number insertBBCourseLink (CourseInfo courseInfo) {
		mLog.info("insertBBCourseLink  called ...");

		String sql = "insert into SDWBlackboardSchedulerBBCourses"
				+ " (schoolYear, calendarID, bbCOURSE_ID, bbCOURSE_NAME, bbDESCRIPTION, bbEXTERNAL_COURSE_KEY, "
				+ " createdByPersonID, created, "
				+ " modifiedByPersonID, modified) "
				+ " values (:schoolYear, :calendarId, :bbCourseId, :bbCourseName, :bbDescription, :bbExternalCourseKey, "
				+ " :personId, GETDATE(), "
				+ " :personId, GETDATE())";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("schoolYear", courseInfo.getEndYear());
		params.addValue("calendarId", courseInfo.getCalendarId());
		params.addValue("bbCourseId", courseInfo.getTargetCourseId());
		params.addValue("bbCourseName", courseInfo.getTargetCourseName());
		params.addValue("bbDescription", courseInfo.getTargetCourseDescription());
		params.addValue("bbExternalCourseKey", courseInfo.getTargetCourseId());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		params.addValue("personId", courseInfo.getPersonId());
		try {
			int id = template.update(sql, params, keyHolder);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return keyHolder.getKey();

	}

	@Transactional
	public Number insertBBSectionLink (SectionInfo sectionInfo) {
		mLog.info("insertBBCSectionLink  called ...");

		String sql = "insert into SDWBlackboardSchedulerSISCourseSections"
				+ " (bbCourseID, calendarID, courseID, sectionID, isSelected, "
				+ " modifiedByPersonID, modified, groupId) "
				+ " values (:bbCourseId, :calendarId, :courseId, :sectionId, :selected,"
				+ " :personId, GETDATE(), :groupId)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bbCourseId", sectionInfo.getBbCourseId());
		params.addValue("calendarId", sectionInfo.getCalendarId());
		params.addValue("courseId", sectionInfo.getCourseId());
		params.addValue("sectionId", sectionInfo.getSectionId());
		params.addValue("personId", sectionInfo.getPersonId());
		params.addValue("selected", sectionInfo.isSelected());
		params.addValue("groupId", sectionInfo.getGroupId());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			int id = template.update(sql, params, keyHolder);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return keyHolder.getKey();

	}

	@Transactional
	public Number insertBBPersonLink (PersonInfo personInfo) {
		mLog.trace("insertBBCSectionLink  called ...");

		String sql = "insert into SDWBlackboardSchedulerSISCoursePersons"
				+ " (bbCourseID, personID, personType, sourcePersonType, "
				+ " modifiedByPersonID, modified) "
				+ " values (:bbCourseId, :personId, :personType, :sourcePersonType,"
				+ " :modifiedByPersonId, GETDATE())";

		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("bbCourseId", personInfo.getBbCourseId());
		params.addValue("personId", personInfo.getPersonId());
		params.addValue("personType", personInfo.getPersonType());
		params.addValue("sourcePersonType", personInfo.getSourcePersonType());
		params.addValue("modifiedByPersonId", personInfo.getModifiedByPersonId());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			int id = template.update(sql, params, keyHolder);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return keyHolder.getKey();

	}

	@Transactional
	public Number deleteBBPersonLink (Long key, Long personId) {
		mLog.trace("deleteBBPersonLink called ...");

		String sql = "delete from SDWBlackboardSchedulerSISCoursePersons"
				+ " where bbCourseId =:bbCourseId and personId=:personId";

		MapSqlParameterSource params = new MapSqlParameterSource();
		Number rows = null;

		params.addValue("bbCourseId", key);
		params.addValue("personId", personId);
		try {
			rows = template.update(sql, params);
			mLog.info("Number of rows deleted: " + rows);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return rows;
	}

	@Transactional
	public Number updateBBCourseLink (Number p_key, CourseInfo courseInfo) {
		mLog.info("updateBBCourseLink  called ...");

		String sql = "update SDWBlackboardSchedulerBBCourses"
				+ " set bbCOURSE_ID = :bbCourseId, bbEXTERNAL_COURSE_KEY=:bbCourseId, modifiedByPersonID=:personId, modified=GETDATE()"
				+ " where  bbCourseID = :key";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bbCourseId", courseInfo.getTargetCourseId());
		params.addValue("key", p_key);
		params.addValue("personId", courseInfo.getPersonId());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		Number id = null;

		try {
			id = template.update(sql, params, keyHolder);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return id;

	}

	@Transactional
	public Number updateBBCourseInfo (UpdateCourseInfo courseInfo) {
		mLog.trace("updateBBCourseInfo  called ...");

		String sql = "update SDWBlackboardSchedulerBBCourses"
				+ " set bbCOURSE_NAME = :bbCourseName, bbDESCRIPTION=:bbCourseDescription, modified=GETDATE()"
				+ " where  bbCOURSE_ID = :bbCourseId";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bbCourseName", courseInfo.getBbCourseName());
		params.addValue("bbCourseDescription", courseInfo.getBbCourseDescription());
		params.addValue("bbCourseId", courseInfo.getBbCourseId());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		Number id = null;

		try {
			id = template.update(sql, params, keyHolder);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return id;

	}

	@Transactional
	public Number updateBBCourseGroupSet (Number p_key, GroupProxy p_group, String p_personId) {
		mLog.trace("updateBBCourseLink  called ...");

		String sql = "update SDWBlackboardSchedulerBBCourses"
				+ " set groupSetId = :groupSetId, modifiedByPersonID=:personId, modified=GETDATE()"
				+ " where  bbCourseID = :key";
		Number id = null;
		if (p_group != null) {
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("groupSetId", p_group.getId());
			params.addValue("key", p_key);
			params.addValue("personId", p_personId);
			KeyHolder keyHolder = new GeneratedKeyHolder();


			try {
				id = template.update(sql, params, keyHolder);
			} catch (DataAccessException l_ex) {
				mLog.error("Database Access Error", l_ex);
			}
		} else {
			mLog.info("Unable to BB Course Groups Set.  Group is empty");
		}

		return id;

	}

	@Transactional
	public String removeSection(String p_sectionId) {
		mLog.trace("removeSection  called ...");
		Number rows = null;

		String sql1 = "select b.bbCOURSE_ID from SDWBlackboardSchedulerSISCourseSections a"
				+ " left join SDWBlackboardSchedulerBbCourses b on b.bbCourseID = a.bbCourseID"
				+ " where a.sectionID=:sectionid";

		String sql2 = "delete from SDWBlackboardSchedulerSISCourseSections where sectionID=:sectionid";


		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("sectionid", p_sectionId);

		String l_courseId = null;
		try {
			l_courseId = (String) template.queryForObject(sql1, params, String.class);
			mLog.info("COURSE ID: " + l_courseId);

			// Now deleted the Section
			rows = template.update(sql2, params);
			mLog.info("Number of rows deleted: " + rows);

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return l_courseId;
	}

	@Transactional
	public List<ICEnrollment> addSection(String p_courseId, String p_sectionId, Long p_personId, String p_groupId) {
		mLog.info("addSection  called ...");
		Number rows = null;

		String sql1 = "select a.bbCourseID from SDWBlackboardSchedulerBBCourses a"
				+ " where a.bbCOURSE_ID =:courseid";

		String sql2 = "insert into SDWBlackboardSchedulerSISCourseSections"
				+ " (bbCourseID, calendarID, courseID, sectionID, isSelected, "
				+ " modifiedByPersonID, modified, groupId) "
				+ " values (:bbCourseId, :calendarId, :courseid, :sectionId, :selected,"
				+ " :personId, GETDATE(), :groupId)";


		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("courseid", p_courseId);

		String l_courseId;
		List<ICEnrollment> l_enrollments = null;
		try {
			l_courseId = (String) template.queryForObject(sql1, params, String.class);
			mLog.info("BB Course ID: " + l_courseId);

			// Get Section Info
			ICSectionInfo l_sectionInfo = this.getSectionInfo(p_sectionId);
			mLog.info("Got Section Info");

			// Now add the Section
			params.addValue("bbCourseId", l_courseId);
			params.addValue("courseid", l_sectionInfo.getCourseID());
			params.addValue("calendarId", l_sectionInfo.getCalendarID());
			params.addValue("sectionId", p_sectionId);
			params.addValue("selected", true);
			params.addValue("personId", p_personId);
			params.addValue("groupId", p_groupId);
			rows = template.update(sql2, params);
			mLog.info("Number of rows inserted: " + rows);

			List<String> sections = new ArrayList<String> ();
			sections.add(p_sectionId);
			l_enrollments = this.getEnrollmentsForSections(sections);

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return l_enrollments;
	}

	@Transactional
	public Number deleteBBCourses(String p_bbCourseId) {
		mLog.info("deleteBBCourses  called ...");
		Number rows = null;
		String sql = "delete from  SDWBlackboardSchedulerBBCourses where bbCourseID=:bbCourseId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bbCourseId", p_bbCourseId);

		try {
			rows = template.update(sql, params);
			mLog.info("Number of rows deleted: " + rows);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return rows;
	}

	@Transactional
	public Number deleteBBSections(String p_bbCourseId) {
		mLog.info("deleteBBSections  called ...");
		Number rows = null;
		String sql = "delete from  SDWBlackboardSchedulerSISCourseSections where bbCourseID=:bbCourseId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bbCourseId", p_bbCourseId);

		try {
			rows = template.update(sql, params);
			mLog.info("Number of rows deleted: " + rows);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return rows;
	}
	
	@Transactional
	public Number deleteBBPersons(String p_bbCourseId) {
		mLog.info("deleteBBPersons  called ...");
		Number rows = null;
		String sql = "delete from  SDWBlackboardSchedulerSISCoursePersons where bbCourseID=:bbCourseId";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bbCourseId", p_bbCourseId);

		try {
			rows = template.update(sql, params);
			mLog.info("Number of rows deleted: " + rows);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return rows;
	}

	@Transactional(readOnly=true)
	public List<ICStudent> getBBStudents (String p_courseId) {
		mLog.trace("getBBStudents  called ...");
		mLog.debug("COURSE ID: " + p_courseId);
		String sql = "select distinct sdwp.personID as personId, "
				+ "  UserAccount.username as userName, "
				+ "  [Identity].firstName + ' ' + [Identity].lastName as studentName from SDWBlackboardSchedulerSISCoursePersons sdwp "
				+ "  left join SDWBlackboardSchedulerBBCourses sdwc on sdwc.bbCourseId=sdwp.bbCourseID "
				+ "  left join UserAccount on UserAccount.personID = sdwp.personID "
				+ "  left join [Identity] on [Identity].personID = sdwp.personID "
				+ "  where sdwp.personType='S' and sdwc.bbCOURSE_ID=:courseId"
				+ "  and UserAccount.isSAMLAccount=1";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICStudent> l_students = null;
		try {
			params.addValue("courseId", p_courseId);
			l_students = template.query(sql,params,  new BeanPropertyRowMapper<ICStudent>(ICStudent.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return l_students;
	}

	@Transactional(readOnly=true)
	public List<ICTeacher> getBBTeachers (String p_courseId) {
		mLog.trace("getBBTeachers  called ...");
		mLog.debug("COURSE ID: " + p_courseId);
		String sql = "select distinct sdwp.personID as personId, "
				+ "  UserAccount.username as userName, "
				+ "  [Identity].firstName + ' ' + [Identity].lastName as teacherName from SDWBlackboardSchedulerSISCoursePersons sdwp "
				+ "  left join SDWBlackboardSchedulerBBCourses sdwc on sdwc.bbCourseId=sdwp.bbCourseID "
				+ "  left join UserAccount on UserAccount.personID = sdwp.personID"
				+ "  left join [Identity] on [Identity].personID = sdwp.personID"
				+ "  where sdwp.personType='T' and sdwc.bbCOURSE_ID=:courseId"
				+ "  and UserAccount.isSAMLAccount = 1";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICTeacher> l_teachers = null;
		try {
			params.addValue("courseId", p_courseId);
			l_teachers = template.query(sql,params,  new BeanPropertyRowMapper<ICTeacher>(ICTeacher.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return l_teachers;
	}

	@Transactional(readOnly=true)
	public List<ICMessage> getMessages () {
		mLog.trace("getMessages  called ...");
		String sql = "select Message as message"
				+ "  from SDWBlackboardSchedulerMessages ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICMessage> l_messages = null;
		try {
			l_messages= template.query(sql,params,  new BeanPropertyRowMapper<ICMessage>(ICMessage.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return l_messages;
	}

	@Transactional
	public void  addMessages (List<String> p_messages) {
		mLog.info("addMessages  called ...");
		String deleteSql = "delete"
				+ "  from SDWBlackboardSchedulerMessages ";
		String insertSql = "insert into SDWBlackboardSchedulerMessages "
				+ "  (Message) "
				+ " values (:Message)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICMessage> l_messages = null;
		Number l_rows=null;
		try {
			// First Remove all the Messages
			l_rows = template.update(deleteSql, params);
			mLog.info("Number of rows deleted: " + l_rows);

			for (String l_message : p_messages) {
				params.addValue("Message", l_message);
				KeyHolder keyHolder = new GeneratedKeyHolder();
				int id = template.update(insertSql, params, keyHolder);
			}

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			throw new RuntimeException("Rollback");
		}
	}

	@Transactional(readOnly=true)
	public ICNode getNode (String p_schoolName) {
		mLog.trace("getNode  called ...");
		String sql = "select st.value as prefix, customschool.value as schoolValue from School"
				+ "    join"
				+ "    ( select * from CustomSchool where attributeID = 622 ) as st"
				+ "       on school.schoolID = st.schoolID\n"
				+ "    join CustomSchool on school.schoolID = CustomSchool.schoolID"
				+ "    where CustomSchool.attributeID in (620) and school.name=:schoolName ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		ICNode l_node = null;
		try {
			params.addValue("schoolName", p_schoolName);
			l_node= (ICNode) template.queryForObject(sql, params, new BeanPropertyRowMapper(ICNode.class));
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return l_node;
	}

	@Transactional
	public Number insertBBUsername (String p_bbUsername, String p_personId) {
		mLog.trace("insertBBUserName called ...");

		String sql = "insert INTO CustomStudent (personID,enrollmentID,attributeID,value,[date],districtID) "
				+ "values (:personId,Null,672,:bbusername,GETDATE(),Null)";

		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("personId", p_personId);
		params.addValue("bbusername", p_bbUsername);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			int id = template.update(sql, params, keyHolder);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return keyHolder.getKey();

	}

	@Transactional
	public Number insertBBPassword (String p_bbPassword, String p_personId) {
		mLog.trace("insertBBPassword called ...");

		String sql = "insert INTO CustomStudent (personID,enrollmentID,attributeID,value,[date],districtID) "
				+ "values (:personId,Null,673,:bbpw,GETDATE(),Null)";

		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("personId", p_personId);
		params.addValue("bbpw", p_bbPassword);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			int id = template.update(sql, params, keyHolder);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return keyHolder.getKey();

	}
}
