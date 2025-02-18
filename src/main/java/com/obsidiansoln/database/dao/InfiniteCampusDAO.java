package com.obsidiansoln.database.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.obsidiansoln.database.model.ICCalendar;
import com.obsidiansoln.database.model.ICCourse;
import com.obsidiansoln.database.model.ICEnrollment;
import com.obsidiansoln.database.model.ICPeriod;
import com.obsidiansoln.database.model.ICPerson;
import com.obsidiansoln.database.model.ICSection;
import com.obsidiansoln.database.model.ICSectionInfo;
import com.obsidiansoln.database.model.ICStaff;
import com.obsidiansoln.database.model.ICStudent;
import com.obsidiansoln.database.model.ICTeacher;
import com.obsidiansoln.database.model.ICTemplate;
import com.obsidiansoln.database.model.ICTerm;
import com.obsidiansoln.database.model.ICUser;

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
		mLog.info("In getCoursesByUsername ...");
		List<ICCourse> l_returnList = new ArrayList<ICCourse>();
		String userSQL = "select distinct Course.courseID,"
				+ " Course.calendarID, "
				+ " Course.name as courseName, "
				+ " Course.number as courseNumber,"
				+ " Calendar.endYear, "
				+ " School.name as schoolName, "
				+ " UserAccount.username as teacherName, "
				+ " (select distinct count(*)"
				+ " From Section"
				+ " Inner Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID"
				+ " Inner Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1"
				+ " Inner Join SchoolYear on SchoolYear.endYear=calendar.endYear"
				+ " where Section.courseID = Course.courseID and Section.teacherPersonId = (select personId from UserAccount where username = :username)"
				+ " and Trial.active = 1 and schoolyear.active=1) as sectionCount,"
				+ " a.bbCourseID as bbCourseId, "
				+ " a.bbCOURSE_ID as blackboardId, "
				+ " a.bbCOURSE_NAME as blackboardName "
				+ " from Section"
				+ " left join Course with (nolock) on Course.courseID = Section.courseID"
				+ " left join Calendar with (nolock) on Calendar.calendarID = Course.calendarID"
				+ " left join School with (nolock) on School.schoolID = Calendar.schoolID "
				+ " left join Teacher with (nolock) on Teacher.sectionID = Section.sectionID"
				+ " left join UserAccount with (nolock) on UserAccount.personID = Teacher.personID "
				+ " left join SDWBlackboardSchedulerSISCourseSections b on b.sectionID = Section.sectionID"
				+ " left join SDWBlackboardSchedulerBBCourses a on a.bbCourseId = b.bbCourseID\n"
				+ " where Section.teacherPersonID = (select personId from UserAccount where username = :username)"
				+ " and (Calendar.endYear=year(GETDATE()) or Calendar.endYear=year(GETDATE())+1)"
				+ " and Section.externalLMSExclude = 0 and Course.externalLMSExclude = 0";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("username", username);
		List<ICCourse> courses = null;
		try {

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

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return l_returnList;
	}

	@Transactional(readOnly=true)
	public List<ICCourse> getCourses() {
		mLog.info("In getCourses ...");
		List<ICCourse> l_returnList = new ArrayList<ICCourse>();
		String noUserSQL = "select distinct Course.courseID,"
				+ " Course.calendarID, "
				+ " Course.name as courseName, "
				+ " Course.number as courseNumber,"
				+ " Calendar.endYear, "
				+ " School.name as schoolName, "
				+ " UserAccount.username as teacherName, "
				+ " (select distinct count(*)"
				+ " From Section"
				+ " Inner Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID"
				+ " Inner Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1"
				+ " Inner Join SchoolYear on SchoolYear.endYear=calendar.endYear"
				+ " where Section.courseID = Course.courseID "
				+ " and Trial.active = 1 and schoolyear.active=1) as sectionCount,"
				+ " a.bbCourseID as bbCourseId, "
				+ " a.bbCOURSE_ID as blackboardId, "
				+ " a.bbCOURSE_NAME as blackboardName "
				+ " from Section"
				+ " left join Course with (nolock) on Course.courseID = Section.courseID"
				+ " left join Calendar with (nolock) on Calendar.calendarID = Course.calendarID"
				+ " left join School with (nolock) on School.schoolID = Calendar.schoolID "
				+ " left join Teacher with (nolock) on Teacher.sectionID = Section.sectionID"
				+ " left join UserAccount with (nolock) on UserAccount.personID = Teacher.personID "
				+ " left join SDWBlackboardSchedulerSISCourseSections b on b.sectionID = Section.sectionID"
				+ " left join SDWBlackboardSchedulerBBCourses a on a.bbCourseId = b.bbCourseID\n"
				+ " where (Calendar.endYear=year(GETDATE()) or Calendar.endYear=year(GETDATE())+1)"
				+ " and Section.externalLMSExclude = 0 and Course.externalLMSExclude = 0";

		MapSqlParameterSource params = new MapSqlParameterSource();
		List<ICCourse> courses = null;
		try {

			courses= template.query(noUserSQL, params, new BeanPropertyRowMapper<ICCourse>(ICCourse.class));

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
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return l_returnList;
	}

	@Transactional(readOnly=true)
	public List<ICBBCourse> getBBCoursesByUsername(String username) {
		mLog.info("In getBBCoursesByUsername ...");
		String sql = "select distinct sdw.bbCourseID as bbCourseId, sdw.bbCOURSE_ID as courseId, sdw.bbCOURSE_NAME as courseName, sdw.bbDESCRIPTION as courseDescription from SDWBlackboardSchedulerBbCourses sdw"
				+ " left join SDWBlackboardSchedulerSISCourseSections on SDWBlackboardSchedulerSISCourseSections.bbCourseID=sdw.bbCourseID"
				+ " left join UserAccount on UserAccount.personID=sdw.createdByPersonID"
				+ " where UserAccount.username=:username";


		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("username", username);
		List<ICBBCourse> bbCourses = null;
		try {
			bbCourses= template.query(sql, params, new BeanPropertyRowMapper<ICBBCourse>(ICBBCourse.class));

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return bbCourses;
	}
	
	@Transactional(readOnly=true)
	public ICBBCourse getBBCourseById(String courseId) {
		mLog.info("In getBBCoursesByUsername ...");
		String sql = "select sdw.bbCourseID as bbCourseId, sdw.bbCOURSE_ID as courseId, sdw.bbCOURSE_NAME as courseName, sdw.bbDESCRIPTION as courseDescription from SDWBlackboardSchedulerBbCourses sdw"
				+ " where sdw.bbCOURSE_ID=:courseid";


		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("courseid", courseId);
		ICBBCourse bbCourse = null;
		try {
			bbCourse= template.queryForObject(sql, params, ICBBCourse.class);

		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return bbCourse;
	}

	@Transactional(readOnly=true)
	public List<ICSection> getSectionsByCourseId(String courseId) {
		mLog.trace("In getSectionsByCourseId ...");
		List<ICSection> l_returnList = new ArrayList<ICSection>();

		String sql1 = "select Distinct"
				+ " Section.sectionID, "
				+ " Section.number as sectionNumber,"
				+ " Section.courseID,"
				+ " Section.roomID,"
				+ " Section.teacherPersonID,"
				+ " Term.name as termName,"
				+ " [Period].name as period,"
				+ " a.bbCourse_ID as linkedCourseId,"
				+ " a.bbCourse_NAME as linkedCourseName,"
				+ " (select count(*) as studentCount from Roster "
				+ " inner join [Identity] on [Identity].personID = Roster.personID"
				+ " inner join UserAccount on UserAccount.personID = Roster.personID"
				+ " where Roster.sectionID = Section.sectionID"
				+ " and (Roster.endDate is null or Roster.endDate > GETDATE())) as studentNumber, "
				+ " 1 as teacherNumber"
				+ " From Course"
				+ " left Join Calendar on calendar.calendarID=course.calendarID"
				+ " left Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID"
				+ " left Join Section on section.courseID=course.courseID"
				+ " left join SDWBlackboardSchedulerSISCourseSections b on b.sectionID = Section.sectionID"
				+ " left join SDWBlackboardSchedulerBBCourses a on a.bbCourseId = b.bbCourseID"
				+ " left Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1"
				+ " left Join SectionPlacement on SectionPlacement.sectionID=section.sectionID  and SectionPlacement.trialID=trial.trialID"
				+ " left Join Term on Term.termID=sectionplacement.termID"
				+ " left Join [Period] on [Period].periodID=sectionplacement.periodID"
				+ " left Join SchoolYear on SchoolYear.endYear=calendar.endYear"
				+ " where Section.courseID = :courseid "
				+ " and Trial.active = 1 and schoolyear.active=1";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("courseid", courseId);
		List<ICSection> sections = null;
		HashMap<Long, ICSection> l_sectionList = new HashMap<Long, ICSection>();
		try {
			sections= template.query(sql1, params, new BeanPropertyRowMapper<ICSection>(ICSection.class));
			for (ICSection section : sections) {
				ICSection l_temp = l_sectionList.get(section.getSectionID());

				if (l_temp != null) {
					if (l_temp.getTermName() != null) {
						l_temp.setTermName (l_temp.getTermName().concat("/"+section.getTermName()));
					}
					if (l_temp.getPeriod() != null) {
						l_temp.setPeriod (l_temp.getPeriod().concat("/"+section.getPeriod()));
					}
					l_sectionList.put(l_temp.getSectionID(), l_temp);
				} else {
					l_sectionList.put(section.getSectionID(), section);
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
	public List<ICSection> getSectionsByCourseIdUsername(String courseId, String username) {
		mLog.trace("In getSectionsByCourseIdUsername ...");
		List<ICSection> l_returnList = new ArrayList<ICSection>();

		String sql1 = "select Distinct"
				+ " Section.sectionID, "
				+ " Section.number as sectionNumber,"
				+ " Section.courseID,"
				+ " Section.roomID,"
				+ " Section.teacherPersonID,"
				+ " Term.name as termName,"
				+ " [Period].name as period,"
				+ " a.bbCourse_ID as linkedCourseId,"
				+ " a.bbCourse_NAME as linkedCourseName,"
				+ " (select count(*) as studentCount from Roster "
				+ " inner join [Identity] on [Identity].personID = Roster.personID"
				+ " inner join UserAccount on UserAccount.personID = Roster.personID"
				+ " where Roster.sectionID = Section.sectionID"
				+ " and (Roster.endDate is null or Roster.endDate > GETDATE())) as studentNumber, "
				+ " 1 as teacherNumber"
				+ " From Course"
				+ " left Join Calendar on calendar.calendarID=course.calendarID"
				+ " left Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID"
				+ " left Join Section on section.courseID=course.courseID"
				+ " left join SDWBlackboardSchedulerSISCourseSections b on b.sectionID = Section.sectionID"
				+ " left join SDWBlackboardSchedulerBBCourses a on a.bbCourseId = b.bbCourseID"
				+ " left Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1"
				+ " left Join SectionPlacement on SectionPlacement.sectionID=section.sectionID  and SectionPlacement.trialID=trial.trialID"
				+ " left Join Term on Term.termID=sectionplacement.termID"
				+ " left Join [Period] on [Period].periodID=sectionplacement.periodID"
				+ " left Join SchoolYear on SchoolYear.endYear=calendar.endYear"
				+ " where Section.courseID = :courseid and Section.teacherPersonId = (select personId from UserAccount where username = :username)"
				+ " and Trial.active = 1 and schoolyear.active=1";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("courseid", courseId);
		params.addValue("username", username);
		List<ICSection> sections = null;
		HashMap<Long, ICSection> l_sectionList = new HashMap<Long, ICSection>();
		try {
			sections= template.query(sql1, params, new BeanPropertyRowMapper<ICSection>(ICSection.class));
			for (ICSection section : sections) {
				ICSection l_temp = l_sectionList.get(section.getSectionID());

				if (l_temp != null) {
					if (l_temp.getTermName() != null) {
						l_temp.setTermName (l_temp.getTermName().concat("/"+section.getTermName()));
					}
					if (l_temp.getPeriod() != null) {
						l_temp.setPeriod (l_temp.getPeriod().concat("/"+section.getPeriod()));
					}
					l_sectionList.put(l_temp.getSectionID(), l_temp);
				} else {
					l_sectionList.put(section.getSectionID(), section);
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
	public List<ICSection> getSectionsByCourseId2(String courseId, String username) {
		mLog.trace("In getSectionsByCourseId ...");

		String sql2 = "select Distinct"
				+ " Section.sectionID," 
				+ " Section.number as sectionNumber,"
				+ " Section.courseID,"
				+ " Section.roomID,"
				+ " Section.teacherPersonID"
				+ " From Course"
				+ " Inner Join Calendar on calendar.calendarID=course.calendarID"
				+ " Inner Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID"
				+ " Inner Join Section on section.courseID=course.courseID"
				+ " Inner Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1"
				+ " Inner Join SchoolYear on SchoolYear.endYear=calendar.endYear"
				+ " where Section.courseID = :courseid and Section.teacherPersonId = (select personId from UserAccount where username = :username)"
				+ " and Trial.active = 1 and schoolyear.active=1";

		String sql1 = "select Distinct"
				+ " Section.sectionID," 
				+ " Section.number as sectionNumber,"
				+ " Section.courseID,"
				+ " Section.roomID,"
				+ " Section.teacherPersonID,"
				+ " Term.name as termName,"
				+ " [Period].name as period"
				+ " From Course"
				+ " Inner Join Calendar on calendar.calendarID=course.calendarID"
				+ " Inner Join ScheduleStructure on ScheduleStructure.calendarID=calendar.calendarID"
				+ " Inner Join Section on section.courseID=course.courseID"
				+ " Inner Join Trial on Trial.trialID = Section.trialID  and trial.structureID=schedulestructure.structureID and trial.active=1"
				+ " Inner Join SectionPlacement on SectionPlacement.sectionID=section.sectionID  and SectionPlacement.trialID=trial.trialID"
				+ " Inner Join Term on Term.termID=sectionplacement.termID"
				+ " Inner Join [Period] on [Period].periodID=sectionplacement.periodID"
				+ " Inner Join SchoolYear on SchoolYear.endYear=calendar.endYear"
				+ " where Section.courseID = :courseid and Section.teacherPersonId = (select personId from UserAccount where username = :username)"
				+ " and Trial.active = 1 and schoolyear.active=1";

		String sql = "select Section.sectionID, Section.number as sectionNumber, Section.courseID, Section.roomID, Term.name as period from Section"
				+ " left join Trial on Trial.trialID = Section.trialID"
				+ " left join ScheduleStructure on ScheduleStructure.structureID = Trial.structureID"
				+ " left join TermSchedule on TermSchedule.structureID = ScheduleStructure.structureID"
				+ " left join Term on Term.termScheduleID = TermSchedule.termScheduleID"
				+ " where courseID = :courseid and Section.teacherPersonId = (select personId from UserAccount where username = :username)"
				+ " and Trial.active = 1";

		String termSQL = "select Term.name as termName from Section"
				+ " left join Trial on Trial.trialID = Section.trialID "
				+ " left join ScheduleStructure on ScheduleStructure.structureID = Trial.structureID "
				+ " left join TermSchedule on TermSchedule.structureID = ScheduleStructure.structureID "
				+ " left join Term on Term.termScheduleID = TermSchedule.termScheduleID"
				+ " where Section.sectionID=:sectionid";

		String periodSQL = "select Period.name as periodName from Section"
				+ " left join SectionPlacement on SectionPlacement.sectionID = Section.sectionID"
				+ " left join Period on Period.periodID = SectionPlacement.periodID"
				+ " where Section.sectionID=:sectionid";

		String linkedSQL = "select Distinct bbCourse.bbCourseID as bbCourseId, bbCourse.bbCOURSE_ID as courseId, bbCourse.bbCOURSE_NAME as courseName from SDWBlackboardSchedulerBBCourses bbCourse"
				+ " left join SDWBlackboardSchedulerSISCourseSections bbSection on bbSection.bbCourseID = bbCourse.bbCourseID"
				+ " where bbSection.sectionID=:sectionid";

		String countSQL = "select count(*) as studentCount from Roster "
				+ " inner join Section on Section.sectionID = Roster.sectionID"
				+ " inner join [Identity] on [Identity].personID = Roster.personID"
				+ " inner join UserAccount on UserAccount.personID = Roster.personID"
				+ " where Roster.sectionID = :section"
				+ " and (Roster.endDate is null or Roster.endDate > GETDATE())";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("courseid", courseId);
		params.addValue("username", username);
		List<ICSection> sections = null;
		try {
			sections= template.query(sql2, params, new BeanPropertyRowMapper<ICSection>(ICSection.class));
			for (ICSection section : sections) {
				// Get the TERMS For the Section
				MapSqlParameterSource params2 = new MapSqlParameterSource();
				params2.addValue("sectionid", section.getSectionID());
				List<ICTerm> terms= template.query(termSQL, params2, new BeanPropertyRowMapper<ICTerm>(ICTerm.class));
				String termValue = null;
				for (ICTerm term:terms) {
					if (termValue == null) {
						termValue = term.getTermName();
					} else {
						termValue = termValue.concat("/");
						termValue = termValue.concat(term.getTermName());
					}

				}
				section.setTermName(termValue);

				// Get the PERIODS For the Section
				MapSqlParameterSource params3 = new MapSqlParameterSource();
				params3.addValue("sectionid", section.getSectionID());
				List<ICPeriod> periods= template.query(periodSQL, params3, new BeanPropertyRowMapper<ICPeriod>(ICPeriod.class));
				String periodValue = null;
				for (ICPeriod period:periods) {
					if (periodValue == null) {
						periodValue = period.getPeriodName();
					} else {
						periodValue = periodValue.concat("/");
						periodValue = periodValue.concat(period.getPeriodName());
					}

				}
				section.setPeriod(periodValue);

				// Get the Linked BB Course
				MapSqlParameterSource params4 = new MapSqlParameterSource();
				params4.addValue("sectionid", section.getSectionID());
				List<ICBBCourse> linkedCourses= template.query(linkedSQL, params4, new BeanPropertyRowMapper<ICBBCourse>(ICBBCourse.class));
				String linkedCourseValue = null;
				String linkedCourseId = null;
				for (ICBBCourse linkedCourse:linkedCourses) {
					mLog.info("BB Course Name: " + linkedCourse.getCourseName());
					mLog.info("BB Course ID: " + linkedCourse.getCourseId());
					if (linkedCourseValue == null) {
						linkedCourseValue = linkedCourse.getCourseName();
						linkedCourseId = linkedCourse.getCourseId();
					} else {
						linkedCourseValue = linkedCourseValue.concat("/");
						linkedCourseValue = linkedCourseValue.concat(linkedCourse.getCourseName());
					}
				}
				section.setLinkedCourseName(linkedCourseValue);	
				section.setLinkedCourseId(linkedCourseId);

				// Get Totals
				MapSqlParameterSource params5 = new MapSqlParameterSource();
				params5.addValue("section", section.getSectionID());
				Integer studentCount = (Integer)template.queryForObject(countSQL, params5, Integer.class);
				section.setStudentNumber(studentCount);
				Integer teacherCount =Integer.valueOf(1);
				section.setTeacherNumber(teacherCount);


			}
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}
		return sections;
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
	public Long getPersonId (String p_username) {
		mLog.info("Username: " + p_username);

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
		String sql = "select Distinct UserAccount.username, UserAccount.personID as personId, Section.sectionID as sectionId from Roster "
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
		mLog.info("getTemplates called ...");
		String sql = "select bbMasterID as bbMasterId, bbCOURSE_ID as bbCourseId, bbCOURSE_NAME as bbCourseName, MasterLevel as masterLevel, MasterSubjectArea as masterSubjectArea from SDWBlackboardSchedulerMasterCourses ";

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

	@Transactional(readOnly=true)
	public List<ICTeacher> getTeachers() {
		mLog.info("getTeachers called ...");
		String sql = "select Distinct  UserAccount.personID as personId, UserAccount.username as userName, [Identity].firstName+' '+[Identity].lastName as teacherName from Section"
				+ " left join UserAccount on UserAccount.personID = Section.teacherPersonID"
				+ " left join [Identity] on [Identity].personID = Section.teacherPersonID";

		String sql2 = "select [identity].personID as personId, UserAccount.username as userName, firstname+' '+lastname as teacherName "
				+ " Inner Join (select userAccount.personID, max(id.IdentityID)max_id  from UserAccount"
				+ " Inner join EmploymentAssignment on userAccount.PersonID = employmentAssignment.personID"
				+ " Inner Join [Identity]id on UserAccount.personID = id.personID"
				+ " where employmentAssignment.endDate is null or employmentAssignment.endDate >= GETDATE()"
				+ "  group by UserAccount.personID)id on id.max_id = [Identity].identityID"
				+ "  Inner Join UserAccount on [identity].personID = userAccount.personID"
				+ " where UserAccount.email like '%@waukesha.k12.wi.us'";

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
		mLog.info("getStudents called ...");
		String sql = "select distinct "
				+ " prs.personID as personId,"
				+ " UA.username as userName,"
				+ " IsNull(LTRIM(RTRIM(REPLACE(idn.firstName,',',' '))),'')  + ' ' +"
				+ " IsNull(LTRIM(RTRIM(REPLACE(idn.lastName,',',' '))),'')  as studentName"
				+ " from Person prs with (nolock) "
				+ " cross join campusVersion v with (nolock) "
				+ " join Enrollment enr with (nolock) on prs.personID=enr.personID "
				+ "	  and (enr.endDate Is Null or enr.endDate>GETDATE()) "
				+ "	  and enr.startDate = ( "
				+ "	 		select max(enr1.startDate) "
				+ "	 		from Enrollment enr1 with (nolock) "
				+ "			join Calendar cal1 with (nolock) on cal1.calendarID=enr1.calendarID "
				+ "				and cal1.districtID=v.districtID \n"
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
				+ " 	and (HM.endDate Is Null or HM.endDate>GETDATE()) \n"
				+ "	and (HM.[secondary]=0 or HM.[secondary] Is Null) \n"
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
				+ "	  and cana.deprecated<>1 \n"
				+ " join CustomSchool csCode with (nolock) on s.schoolID=csCode.schoolID "
				+ " join CampusAttribute caCode with (nolock) on csCode.attributeID=caCode.attributeID "
				+ " 	and caCode.[object]='School' "
				+ " 	and caCode.element='legacySchoolc' "
				+ " 	and caCode.dataType='textBox' "
				+ " 	and caCode.deprecated<>1 "
				+ " join CustomSchool csType with (nolock) on s.schoolID=csType.schoolID "
				+ " 	and csType.value IN ('ES','MS','HS') \n"
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
				+ "		and calE.districtID=v.districtID \n"
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
				+ "		and calz.districtID=v.districtID \n"
				+ "		and (calz.summerSchool=0 or calz.summerSchool Is Null) "
				+ "		and calz.endyear=(select top 1 endyear from SchoolYear with (nolock) where active=1)+1 "
				+ "	join School with (nolock) on School.schoolID=calz.schoolID \n"
				+ "		and School.districtID=v.districtID \n"
				+ "	join CustomSchool csTypez with (nolock) on csTypez.schoolID=School.schoolID "
				+ "		and csTypez.attributeID=caType.attributeID "
				+ "	where enrz.personID=prs.personID "
				+ "	and enrz.active=1 "
				+ "	and (enrz.noShow=0 or enrz.noShow Is Null) "
				+ "	and enrz.serviceType IN ('P','S','N')),'N') = 'Y'";

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
		String sql = "select distinct 'A' as [Union],"
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
				+ " s.number as schoolNumber,"
				+ " cal.name as calendarName,"
				+ " LTRIM(RTRIM(REPLACE((CASE WHEN addr.number Is Null THEN '' ELSE LTRIM(RTRIM(addr.number)) END)+(CASE WHEN addr.prefix Is Null THEN '' ELSE ' '+LTRIM(RTRIM(addr.prefix)) END)+(CASE WHEN addr.street Is Null THEN '' ELSE ' '+LTRIM(RTRIM(REPLACE(addr.street,',',' '))) END)+(CASE WHEN addr.tag Is Null THEN '' ELSE ' '+LTRIM(RTRIM(addr.tag)) END)+(CASE WHEN addr.dir Is Null THEN '' ELSE ' '+LTRIM(RTRIM(addr.dir)) END),',',' '))) as address,"
				+ " IsNull(LTRIM(RTRIM(addr.apt)),'') as apartment,"
				+ " IsNull(LTRIM(RTRIM(addr.city)),'') as city,"
				+ " IsNull(LTRIM(RTRIM(addr.[state])),'') as state,"
				+ " IsNull(LTRIM(RTRIM(addr.zip)),'') as zip,"
				+ " UA.username as userName "
				+ " from Person prs with (nolock) "
				+ " cross join campusVersion v with (nolock) "
				+ " join Enrollment enr with (nolock) on prs.personID=enr.personID "
				+ "	and (enr.endDate Is Null or enr.endDate>GETDATE()) "
				+ "	and enr.startDate = ( "
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
				+ "	and enr.active=1 "
				+ " 	and (enr.noShow=0 or enr.noShow Is Null) "
				+ " 	and enr.serviceType IN ('P','S','N') "
				+ "	and enr.districtID=v.districtID "
				+ " join Calendar cal with (nolock) on enr.calendarID=cal.calendarID "
				+ " 	and cal.districtID=v.districtID "
				+ "	and ((select min(trm.startDate) "
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
				+ "	and (HL.[secondary]=0 or HL.[secondary] Is Null) "
				+ " left join [Address] addr with (nolock) on HL.addressID=addr.addressID "
				+ " 	and (addr.postOfficeBox=0 or addr.postOfficeBox Is Null) "
				+ " join School s with (nolock) on cal.schoolID=s.schoolID "
				+ " 	and s.districtID=v.districtID "
				+ " join CustomSchool csna with (nolock) on csna.schoolID=cal.schoolID "
				+ " join CampusAttribute cana with (nolock) on csna.attributeID=cana.attributeID "
				+ "	and cana.[object]='School' "
				+ "	and cana.element='schoolNameAbvr' "
				+ "	and cana.dataType='textBox' "
				+ "	and cana.deprecated<>1 "
				+ " join CustomSchool csCode with (nolock) on s.schoolID=csCode.schoolID "
				+ " join CampusAttribute caCode with (nolock) on csCode.attributeID=caCode.attributeID "
				+ " 	and caCode.[object]='School' "
				+ " 	and caCode.element='legacySchoolc' "
				+ " 	and caCode.dataType='textBox' "
				+ " 	and caCode.deprecated<>1 "
				+ " join CustomSchool csType with (nolock) on s.schoolID=csType.schoolID "
				+ " 	and csType.value IN ('ES','MS','HS') "
				+ " join CampusAttribute caType with (nolock) on csType.attributeID=caType.attributeID "
				+ "	and caType.[object]='School' "
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
				+ " UNION "
				+ " select distinct 'B' as [Union],"
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
				+ " s.number as schoolNumber,"
				+ " cal.name as calendarName,"
				+ " LTRIM(RTRIM(REPLACE((CASE WHEN addr.number Is Null THEN '' ELSE LTRIM(RTRIM(addr.number)) END)+(CASE WHEN addr.prefix Is Null THEN '' ELSE ' '+LTRIM(RTRIM(addr.prefix)) END)+(CASE WHEN addr.street Is Null THEN '' ELSE ' '+LTRIM(RTRIM(REPLACE(addr.street,',',' '))) END)+(CASE WHEN addr.tag Is Null THEN '' ELSE ' '+LTRIM(RTRIM(addr.tag)) END)+(CASE WHEN addr.dir Is Null THEN '' ELSE ' '+LTRIM(RTRIM(addr.dir)) END),',',' '))) as address,"
				+ " IsNull(LTRIM(RTRIM(addr.apt)),'') as apartment,"
				+ " IsNull(LTRIM(RTRIM(addr.city)),'') as city,"
				+ " IsNull(LTRIM(RTRIM(addr.[state])),'') as state,"
				+ " IsNull(LTRIM(RTRIM(addr.zip)),'') as zip,"
				+ " UA.username as userName "
				+ " from Person prs with (nolock) "
				+ " cross join campusVersion v with (nolock) "
				+ " join Enrollment enr with (nolock) on prs.personID=enr.personID "
				+ "	and enr.endDate>DATEADD(day,-60,GETDATE()) "
				+ "	and enr.startDate = ( "
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
				+ "	and enr.active=1 "
				+ " 	and (enr.noShow=0 or enr.noShow Is Null) "
				+ " 	and enr.serviceType IN ('P','S','N') "
				+ "	and enr.districtID=v.districtID "
				+ " join Calendar cal with (nolock) on enr.calendarID=cal.calendarID "
				+ " 	and cal.districtID=v.districtID "
				+ " 	and (cal.summerSchool=0 or cal.summerSchool Is Null) "
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
				+ "	and (HL.[secondary]=0 or HL.[secondary] Is Null) "
				+ " left join [Address] addr with (nolock) on HL.addressID=addr.addressID "
				+ " 	and (addr.postOfficeBox=0 or addr.postOfficeBox Is Null) "
				+ " join School s with (nolock) on cal.schoolID=s.schoolID "
				+ " 	and s.districtID=v.districtID "
				+ " join CustomSchool csna with (nolock) on csna.schoolID=cal.schoolID "
				+ " join CampusAttribute cana with (nolock) on csna.attributeID=cana.attributeID "
				+ "	and cana.[object]='School' "
				+ "	and cana.element='schoolNameAbvr' "
				+ "	and cana.dataType='textBox' "
				+ "	and cana.deprecated<>1 "
				+ " join CustomSchool csCode with (nolock) on s.schoolID=csCode.schoolID "
				+ " join CampusAttribute caCode with (nolock) on csCode.attributeID=caCode.attributeID "
				+ " 	and caCode.[object]='School' "
				+ " 	and caCode.element='legacySchoolc' "
				+ " 	and caCode.dataType='textBox' "
				+ " 	and caCode.deprecated<>1 "
				+ " join CustomSchool csType with (nolock) on s.schoolID=csType.schoolID "
				+ " 	and csType.value IN ('ES','MS','HS') "
				+ " join CampusAttribute caType with (nolock) on csType.attributeID=caType.attributeID "
				+ "	and caType.[object]='School' "
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
				+ " and enr.districtID=v.districtID "
				+ " and NOT prs.personID in ( "
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
				+ "	and enrz.serviceType IN ('P','S','N')),'N') = 'Y' ";



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
				+ "	  and (ea.endDate>GETDATE() or ea.endDate Is Null) \n"
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

	public Number insertBBPersonLink (PersonInfo personInfo) {
		mLog.info("insertBBCSectionLink  called ...");

		String sql = "insert into SDWBlackboardSchedulerSISCoursePersons"
				+ " (bbCourseID, personID, personType, sourcePersonType, "
				+ " modifiedByPersonID, modified) "
				+ " values (:bbCourseId, :personId, :courseId, :sectionId, :selected,"
				+ " :personId, GETDATE())";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bbCourseId", personInfo.getBbCourseId());
		params.addValue("personId", personInfo.getPersonId());
		params.addValue("personType", personInfo.getPersonType());
		params.addValue("sourcePersonType", personInfo.getSourcePersonType());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			int id = template.update(sql, params, keyHolder);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return keyHolder.getKey();

	}

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

	public Number updateBBCourseGroupSet (Number p_key, GroupProxy p_group, String p_personId) {
		mLog.info("updateBBCourseLink  called ...");

		String sql = "update SDWBlackboardSchedulerBBCourses"
				+ " set groupSetId = :groupSetId, modifiedByPersonID=:personId, modified=GETDATE()"
				+ " where  bbCourseID = :key";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("groupSetId", p_group.getId());
		params.addValue("key", p_key);
		params.addValue("personId", p_personId);
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
	public String removeSection(String p_sectionId) {
		mLog.info("removeSection  called ...");
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

	public Number deleteBBCourses() {
		mLog.info("deleteBBCourses  called ...");
		Number rows = null;
		String sql = "delete from  SDWBlackboardSchedulerBBCourses where bbCourseID>8292";
		MapSqlParameterSource params = new MapSqlParameterSource();

		try {
			rows = template.update(sql, params);
			mLog.info("Number of rows deleted: " + rows);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}


		return rows;
	}

	public Number deleteBBSections() {
		mLog.info("deleteBBSections  called ...");
		Number rows = null;
		String sql = "delete from  SDWBlackboardSchedulerSISCourseSections where courseSectionID>50292";
		MapSqlParameterSource params = new MapSqlParameterSource();

		try {
			rows = template.update(sql, params);
			mLog.info("Number of rows deleted: " + rows);
		} catch (DataAccessException l_ex) {
			mLog.error("Database Access Error", l_ex);
			return null;
		}

		return rows;
	}
}
