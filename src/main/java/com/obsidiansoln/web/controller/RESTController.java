package com.obsidiansoln.web.controller;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obsidiansoln.blackboard.course.CourseProxy;
import com.obsidiansoln.blackboard.coursecopy.CourseInfo;
import com.obsidiansoln.blackboard.coursecopy.SectionInfo;
import com.obsidiansoln.blackboard.membership.EnrollmentProxy;
import com.obsidiansoln.blackboard.model.BBRestCounts;
import com.obsidiansoln.blackboard.sis.SnapshotFileManager;
import com.obsidiansoln.blackboard.term.TermProxy;
import com.obsidiansoln.blackboard.user.ParentProxy;
import com.obsidiansoln.blackboard.user.UserProxy;
import com.obsidiansoln.database.dao.InfiniteCampusDAO;
import com.obsidiansoln.database.model.ICCalendar;
import com.obsidiansoln.database.model.ICCourse;
import com.obsidiansoln.database.model.ICEnrollment;
import com.obsidiansoln.database.model.ICSection;
import com.obsidiansoln.database.model.ICSectionInfo;
import com.obsidiansoln.database.model.ICStaff;
import com.obsidiansoln.database.model.ICTemplate;
import com.obsidiansoln.database.model.ICUser;
import com.obsidiansoln.util.EmailManager;
import com.obsidiansoln.util.RestManager;
import com.obsidiansoln.web.model.AdminInfo;
import com.obsidiansoln.web.model.ConfigData;
import com.obsidiansoln.web.model.ContactModel;
import com.obsidiansoln.web.model.FilterInfo;
import com.obsidiansoln.web.model.LocationInfo;
import com.obsidiansoln.web.model.LtiInfo;
import com.obsidiansoln.web.model.PortalInfo;
import com.obsidiansoln.web.model.RestInfo;
import com.obsidiansoln.web.model.SearchFormData;
import com.obsidiansoln.web.model.SemesterInfo;
import com.obsidiansoln.web.service.BBSchedulerService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RESTController {

	private static Logger mLog = LoggerFactory.getLogger(RESTController.class);
	private BBSchedulerService m_service = null;
	@Autowired
	private InfiniteCampusDAO dao;

	private final String SUCCESS = "{ \"success\": true}";
	private final String FAILURE = "{ \"success\": false}";

	@Value("${PROJECT_VERSION}")
	private String RELEASE;

	public RESTController() {
		m_service = new BBSchedulerService();
	}

	@RequestMapping(value = "/api/restData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getRestData(HttpServletRequest request) {
		mLog.trace("In getRestData ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				ConfigData l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);
				BBRestCounts l_counts = l_manager.getBBRestCounts();
				RestInfo l_rest = new RestInfo();
				l_rest.setHost(l_configData.getRestHost());
				l_rest.setKey(l_configData.getRestKey());
				l_rest.setSecret(l_configData.getRestSecret());
				l_rest.setLimit(l_counts.getL_rateLimit());
				l_rest.setLimitRemaining(l_counts.getL_rateLimitRemaining());
				l_rest.setLimitReset(l_counts.getL_rateLimitReset());
				l_rest.setApplicationVersion(RELEASE);
				return mapper.writeValueAsString(l_rest);
			} catch (JsonProcessingException e) {
				mLog.error(e.getMessage());
				return FAILURE;
			} catch (Exception e) {
				mLog.error(e.getMessage());
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}

	@RequestMapping(value = "/api/restData", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public String putRestData(@RequestBody final RestInfo restData, HttpServletRequest request) {
		mLog.trace("In putRestData ...");
		if (checkApiKey(request)) {
			try {
				ConfigData l_configData = m_service.getConfigData();
				l_configData.setRestHost(restData.getHost());
				l_configData.setRestKey(restData.getKey());
				l_configData.setRestSecret(restData.getSecret());
				m_service.saveConfigData(l_configData);
			} catch (Exception l_ex) {
				mLog.error(l_ex.getMessage());
			}
		} else {
			return FAILURE;
		}
		return SUCCESS;
	}

	@RequestMapping(value = "/api/ltiData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getLtiData(HttpServletRequest request) {
		mLog.trace("In getLtiData ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				ConfigData l_configData = m_service.getConfigData();
				LtiInfo l_lti = new LtiInfo();
				l_lti.setKey(l_configData.getLtiKey());
				l_lti.setSecret(l_configData.getLtiSecret());

				return mapper.writeValueAsString(l_lti);
			} catch (JsonProcessingException e) {
				mLog.error(e.getMessage());
				return FAILURE;
			} catch (Exception e) {
				mLog.error(e.getMessage());
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}

	@RequestMapping(value = "/api/ltiData", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public String putLtiData(@RequestBody final LtiInfo ltiData, HttpServletRequest request) {
		mLog.info("In putLtiData ..." + ltiData.getKey());
		if (checkApiKey(request)) {
			try {
				ConfigData l_configData = m_service.getConfigData();
				l_configData.setLtiKey(ltiData.getKey());
				l_configData.setLtiSecret(ltiData.getSecret());

				m_service.saveConfigData(l_configData);

			} catch (Exception l_ex) {
				mLog.error(l_ex.getMessage());
			}
		} else {
			return FAILURE;
		}
		return SUCCESS;
	}

	@RequestMapping(value = "/api/portalData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getPortalData(HttpServletRequest request) {
		mLog.trace("In getPortalData ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				ConfigData l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);
				PortalInfo l_portalData = new PortalInfo();
				l_portalData.setLogLevel(l_configData.getLogLevel());
				l_portalData.setSemesterStart1(l_configData.getSemester1StartDate());
				l_portalData.setSemesterEnd1(l_configData.getSemester1EndDate());
				l_portalData.setSemesterStart2(l_configData.getSemester2StartDate());
				l_portalData.setSemesterEnd2(l_configData.getSemester2EndDate());
				l_portalData.setDefaultSemester(l_configData.getDefaultSemester());
				l_portalData.setDefaultTerm(l_configData.getDefaultTerm());
				l_portalData.setAdminEmail(l_configData.getAdminReportEmail());
				l_portalData.setAdminInstructor(l_configData.getAdminReportInstructor());
				l_portalData.setAdminPhone(l_configData.getAdminReportPhone());
				// Add Terms
				List<TermProxy> l_terms = l_manager.getTerms();
				ArrayList<String> l_termList = new ArrayList<String>();
				for (TermProxy l_term : l_terms) {
					if (l_term.getName().contains("LT")) {
						l_termList.add(l_term.getName());
					}
				}
				l_portalData.setTerms(l_termList);
				return mapper.writeValueAsString(l_portalData);
			} catch (JsonProcessingException e) {
				mLog.error(e.getMessage());
				return FAILURE;
			} catch (Exception e) {
				mLog.error(e.getMessage());
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}

	@RequestMapping(value = "/api/portalData", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public String putPortalData(@RequestBody final PortalInfo portalData, HttpServletRequest request) {
		mLog.trace("In putPortalData ...");
		if (checkApiKey(request)) {
			try {
				ConfigData l_configData = m_service.getConfigData();
				l_configData.setLogLevel(portalData.getLogLevel());
				l_configData.setSemester1StartDate(new Timestamp(portalData.getSemesterStart1().getTime()));
				l_configData.setSemester1EndDate(new Timestamp(portalData.getSemesterEnd1().getTime()));
				l_configData.setSemester2StartDate(new Timestamp(portalData.getSemesterStart2().getTime()));
				l_configData.setSemester2EndDate(new Timestamp(portalData.getSemesterEnd2().getTime()));
				l_configData.setDefaultSemester(portalData.getDefaultSemester());
				l_configData.setDefaultTerm(portalData.getDefaultTerm());
				l_configData.setAdminReportEmail(portalData.getAdminEmail());
				l_configData.setAdminReportInstructor(portalData.getAdminInstructor());
				l_configData.setAdminReportPhone(portalData.getAdminPhone());
				m_service.saveConfigData(l_configData);
			} catch (Exception l_ex) {
				mLog.error(l_ex.getMessage());
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
		return SUCCESS;
	}

	@RequestMapping(value = "/api/adminData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getAdminData(HttpServletRequest request) {
		mLog.trace("In getAdminData ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				ConfigData l_configData = m_service.getConfigData();
				AdminInfo l_adminData = new AdminInfo();
				l_adminData.setHost(l_configData.getEmailHost());
				l_adminData.setPort(l_configData.getEmailPort());
				l_adminData.setUsername(l_configData.getEmailUsername());
				l_adminData.setPw(l_configData.getEmailPassword());
				l_adminData.setNote(l_configData.getEmailNote());
				l_adminData.setAuthenticate(l_configData.isEmailAuthenticate());
				l_adminData.setSsl(l_configData.isEmailUseSSL());
				l_adminData.setDebug(l_configData.isEmailDebug());

				return mapper.writeValueAsString(l_adminData);
			} catch (JsonProcessingException e) {
				mLog.error(e.getMessage());
				return FAILURE;
			} catch (Exception e) {
				mLog.error(e.getMessage());
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}

	@RequestMapping(value = "/api/adminData", method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public String putAdminData(@RequestBody final AdminInfo adminData, HttpServletRequest request) {
		mLog.trace("In putAdminData ...");
		if (checkApiKey(request)) {
			try {
				ConfigData l_configData = m_service.getConfigData();
				l_configData.setEmailHost(adminData.getHost());
				l_configData.setEmailPort(adminData.getPort());
				l_configData.setEmailUsername(adminData.getUsername());
				l_configData.setEmailPassword(adminData.getPw());
				l_configData.setEmailNote(adminData.getNote());
				l_configData.setEmailAuthenticate(adminData.isAuthenticate());
				l_configData.setEmailUseSSL(adminData.isSsl());
				l_configData.setEmailDebug(adminData.isDebug());
				m_service.saveConfigData(l_configData);
			} catch (Exception l_ex) {
				mLog.error("Error: ", l_ex);
			}
		} else {
			return FAILURE;
		}
		return SUCCESS;
	}

	@RequestMapping(value = "/api/getTemplates", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getTemplates (HttpServletRequest request) {
		mLog.info("In getTemplates ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				List<ICTemplate> l_templates = dao.getTemplates();
				return mapper.writeValueAsString(l_templates);
			} catch (Exception e) {
				mLog.error(e.getMessage());
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}


	@RequestMapping(value = "/api/getCalendars/{userName}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getCalendars (@PathVariable("userName") String userName, HttpServletRequest request) {
		mLog.info("In getCalendars ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				List<ICCalendar> l_calendars = dao.getByYearEnd(userName);
				return mapper.writeValueAsString(l_calendars);
			} catch (Exception e) {
				mLog.error(e.getMessage());
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}

	@RequestMapping(value = "/api/getCourses/{userName}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getCourses (@PathVariable("userName") String userName,
			HttpServletRequest request) {
		mLog.info("In getCourses ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				List<ICCourse> l_courses = dao.getCoursesByUsername(userName);
				return mapper.writeValueAsString(l_courses);
			} catch (Exception e) {
				mLog.error(e.getMessage());
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}

	@RequestMapping(value = "/api/getSections/{courseId}/{userName}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getSections (@PathVariable("courseId") String courseId, @PathVariable("userName") String userName, HttpServletRequest request) {
		mLog.info("In getSections ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				List<ICSection> l_sections = dao.getSectionsByCourseId(courseId, userName);
				return mapper.writeValueAsString(l_sections);
			} catch (Exception e) {
				mLog.error(e.getMessage());
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}


	@RequestMapping(value = "/api/createCourse", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String createCourse(@RequestBody final CourseInfo courseInfo, HttpServletRequest request) {
		mLog.info("In createCourse ...");

		mLog.info(" Course ID: " + courseInfo.getCourseTemplateId());
		mLog.info(" Target Course ID: " + courseInfo.getTargetCourseId());
		mLog.info(" Target Course Name: " + courseInfo.getTargetCourseName());
		mLog.info(" Target Course Description: " + courseInfo.getTargetCourseDescription());
		mLog.info(" Target Course Data Source: " + courseInfo.getTargetCourseDataSource());
		mLog.info(" Target Course Person Id: " + courseInfo.getPersonId());
		mLog.info(" Target Course School Year: " + courseInfo.getEndYear());
		mLog.info(" Target Calendar ID: " + courseInfo.getCalendarId());
		mLog.info(" Target Term: " + courseInfo.getTargetCourseTerm());
		mLog.info(" Target SECTIONS: " + courseInfo.getSections());
		ConfigData l_configData;
		if (checkApiKey(request)) {
			if (courseInfo.getSections().size() > 0) {

				// Insert a record in the SDWBlackboardSchedulerBbCourses
				Number l_key = dao.insertBBCourseLink(courseInfo);
				if (l_key != null) {

					// Fix The Course ID to contain the Key
					String l_convertedKey = StringUtils.leftPad(Long.toString(l_key.longValue()), 9, "0");
					mLog.info(" KEY: " + l_convertedKey);

					courseInfo.setTargetCourseId(courseInfo.getTargetCourseId().concat("_"+l_convertedKey));

					// Update the Course ID in SDWBlackboardSchedulerBbCourses
					Number l_key2 = dao.updateBBCourseLink(l_key, courseInfo);
					if (l_key2 != null ) {

						try {
							l_configData = m_service.getConfigData();
							RestManager l_manager = new RestManager(l_configData);
							CourseProxy l_course = l_manager.getCourseByName(courseInfo.getCourseTemplateId());
							courseInfo.setCourseTemplateId(l_course.getId());
							l_manager.createCourseCopy(courseInfo);


							// Update The Section Link Info
							SectionInfo l_sectionInfo = new SectionInfo();
							for (String l_section : courseInfo.getSections()) {
								ICSectionInfo l_sectionICInfo = dao.getSectionInfo(l_section);
								l_sectionInfo.setBbCourseId(l_key.longValue());
								l_sectionInfo.setCalendarId(l_sectionICInfo.getCalendarID());
								l_sectionInfo.setCourseId(l_sectionICInfo.getCourseID());
								l_sectionInfo.setSectionId(l_sectionICInfo.getSectionID());
								l_sectionInfo.setPersonId(courseInfo.getPersonId());
								dao.insertBBSectionLink(l_sectionInfo);
							}

							// Create Enrollments In New Course
							List<ICEnrollment> l_enrollments = dao.getEnrollmentsForSections(courseInfo.getSections());
							mLog.info("Number of Enrollments Returned: " + l_enrollments.size());
							for (ICEnrollment l_enrollment : l_enrollments) {
								mLog.info("       Username: " + l_enrollment.getUsername());
								l_manager.createMembership(courseInfo.getTargetCourseId(), l_enrollment.getUsername(), "Student");
							}

							return SUCCESS;
						} catch (Exception e) {
							mLog.error(e.getMessage());
							return FAILURE;
						}

					} else {
						mLog.error ("SDW BB Course Update Failed");
						return FAILURE;
					}
				} else {
					mLog.error ("SDW BB Course Insert Failed");
					return FAILURE;
				}
			} else {
				mLog.error ("No Sections Detected");
				return FAILURE;
			}
		} else {
			mLog.error ("API KEY Incorrect");
			return FAILURE;
		}
	}

	@RequestMapping(value = "/api/getEnrollments/{sections}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getEnrollments (@PathVariable("sections") String[] sections, HttpServletRequest request) {
		mLog.trace("In getEnrollments ...");
		List<String> list = Arrays.asList(sections);
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				List<ICEnrollment> l_enrollments = dao.getEnrollmentsForSections(list);
				return mapper.writeValueAsString(l_enrollments);
			} catch (Exception e) {
				mLog.error(e.getMessage());
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}

	@RequestMapping(value = "/api/syncUsers", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String syncUsers (HttpServletRequest request) {
		mLog.info("In syncSUsers() ...");
		if (checkApiKey(request)) {
			mLog.info ("Starting to Sync the Users between Infinite Campus and Blackboard");
			SnapshotFileManager l_manager = new SnapshotFileManager();

			// Get Students
			List<ICUser> l_students = dao.getSISStudents();
			mLog.info("Number of Students: " + l_students.size());

			// Get Staff

			// Add Staff
			List<ICStaff> l_staffs = dao.getSISStaff();
			mLog.info("Number of Staff: " + l_staffs.size());

			String l_file = l_manager.createFile(l_students, l_staffs);
			if (l_file != null) {
				l_manager.sendFile(l_file);
				return SUCCESS;
			} else {
				mLog.error("Error: " + "Unable to create Snapshot File");
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}

	@RequestMapping(value = "/api/getStudents", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<EnrollmentProxy> getStudents(HttpServletRequest request) {
		mLog.info("In getStudents ...");
		if (checkApiKey(request)) {

			ConfigData l_configData;
			try {
				l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);

				List<UserProxy> l_list = l_manager.getUsersByDatasource("SIS", false);

				List<EnrollmentProxy> l_enrollments= new ArrayList<EnrollmentProxy>();
				for (UserProxy l_user:l_list) {
					EnrollmentProxy l_enrollment = new EnrollmentProxy();
					l_enrollment.setTeacherId(l_user.getId());
					l_enrollment.setTeacherName(l_user.getName().getFamily());
					l_enrollment.setTeacherUsername(l_user.getUserName());
					l_enrollments.add(l_enrollment);
				}
				return l_enrollments;
			} catch (Exception e) {
				mLog.error("Error: ", e);
			}
		}

		return null;
	}

	@RequestMapping(value = "/api/cleanupData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String cleanupData(HttpServletRequest request) {
		mLog.info("In cleanUpData ...");
		if (checkApiKey(request)) {

			dao.deleteBBCourses();
			dao.deleteBBSections();
		}
		return SUCCESS;
	}
	@RequestMapping(value = "/api/getTeachers", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<EnrollmentProxy> getTeachers(HttpServletRequest request) {
		mLog.trace("In getTeachers ...");
		if (checkApiKey(request)) {

			ConfigData l_configData;
			try {
				l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);

				List<UserProxy> l_list = l_manager.getUsersByDatasource("SIS.Staff", false);

				List<EnrollmentProxy> l_enrollments= new ArrayList<EnrollmentProxy>();
				for (UserProxy l_user:l_list) {
					EnrollmentProxy l_enrollment = new EnrollmentProxy();
					l_enrollment.setTeacherId(l_user.getId());
					l_enrollment.setTeacherName(l_user.getName().getFamily());
					l_enrollment.setTeacherUsername(l_user.getUserName());
					l_enrollments.add(l_enrollment);
				}
				return l_enrollments;
			} catch (Exception e) {
				mLog.error("Error: ", e);
			}
		}

		return null;
	}

	@RequestMapping(value = "/api/searchFormData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getSearchFormData(HttpServletRequest request) {
		mLog.trace("In getSearchFormData ...");
		ObjectMapper mapper = new ObjectMapper();
		if (checkApiKey(request)) {
			SearchFormData l_searchForm = new SearchFormData();
			try {
				ConfigData l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);
				SemesterInfo l_semester1 = new SemesterInfo();
				l_semester1.setLabel("Semester 1");
				l_semester1.setStartDate(l_configData.getSemester1StartDate());
				l_semester1.setEndDate(l_configData.getSemester1EndDate());

				SemesterInfo l_semester2 = new SemesterInfo();
				l_semester2.setLabel("Semester 2");
				l_semester2.setStartDate(l_configData.getSemester2StartDate());
				l_semester2.setEndDate(l_configData.getSemester2EndDate());
				ArrayList<SemesterInfo> l_semesters = new ArrayList<SemesterInfo>();
				l_semesters.add(l_semester1);
				l_semesters.add(l_semester2);
				l_searchForm.setSemesters(l_semesters);

				// Add Default Semester
				if (l_configData.getDefaultSemester().equalsIgnoreCase("Semester 1")) {
					l_searchForm.setDefaultSemesterIndex(0);
				} else {
					l_searchForm.setDefaultSemesterIndex(1);
				}

				// Add Terms
				List<TermProxy> l_terms = l_manager.getTerms();
				ArrayList<String> l_termList = new ArrayList<String>();
				for (TermProxy l_term : l_terms) {
					if (l_term.getName().contains("LT")) {
						l_termList.add(l_term.getName());
					}
				}
				l_searchForm.setTerms(l_termList);

				// Add Filters
				FilterInfo l_courseIdFilter = new FilterInfo();
				l_courseIdFilter.setName("courseid");
				l_courseIdFilter.setDisableContains(false);
				FilterInfo l_userIdFilter = new FilterInfo();
				l_userIdFilter.setName("userid");
				l_userIdFilter.setDisableContains(false);
				FilterInfo l_studentIdFilter = new FilterInfo();
				l_studentIdFilter.setName("studentid");
				l_studentIdFilter.setDisableContains(false);
				FilterInfo l_teacherIdFilter = new FilterInfo();
				l_teacherIdFilter.setName("teacher");
				l_teacherIdFilter.setDisableContains(false);
				// Removed Filters Per Tulsa Tech Meeting - testing
				// FilterInfo l_studentNameFilter = new FilterInfo();
				// l_studentNameFilter.setName("studentname");
				// l_studentNameFilter.setDisableContains(false);
				// FilterInfo l_classNameFilter = new FilterInfo();
				// l_classNameFilter.setName("classname");
				// l_classNameFilter.setDisableContains(false);
				FilterInfo l_termFilter = new FilterInfo();
				l_termFilter.setName("term");
				l_termFilter.setDisableContains(true);
				FilterInfo l_locationFilter = new FilterInfo();
				l_locationFilter.setName("location");
				l_locationFilter.setDisableContains(true);

				List<LocationInfo> l_locations = l_manager.getLocations();
				mLog.info("Number of Locations: " + l_locations.size());
				List<String> l_options = new ArrayList<String>();
				for (LocationInfo l_location : l_locations) {
					l_options.add(l_location.getLocation());
				}
				l_locationFilter.setOptions(l_options);
				ArrayList<FilterInfo> l_filters = new ArrayList<FilterInfo>();
				l_filters.add(l_courseIdFilter);
				l_filters.add(l_userIdFilter);
				l_filters.add(l_studentIdFilter);
				l_filters.add(l_teacherIdFilter);
				// l_filters.add(l_studentNameFilter);
				// l_filters.add(l_classNameFilter);
				l_filters.add(l_termFilter);
				l_filters.add(l_locationFilter);
				l_searchForm.setFilters(l_filters);

				// Set Default Term
				l_searchForm.setDefaultTerm(l_configData.getDefaultTerm());

				return mapper.writeValueAsString(l_searchForm);
			} catch (JsonProcessingException e) {
				mLog.error("Error: ", e);
				return FAILURE;
			} catch (Exception e) {
				mLog.error("Error: ", e);
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}

	private boolean checkApiKey(HttpServletRequest request) {
		mLog.trace("In checkApiKey ...");
		try {
			ConfigData l_configData = m_service.getConfigData();
			if (request.getHeader("ApiKey") != null
					&& request.getHeader("ApiKey").trim().equals(l_configData.getApiKey())) {
				return true;
			}
		} catch (Exception e) {
			mLog.error(e.getMessage());
			return false;
		}
		return false;
	}


	private void sendEmail(UserProxy p_student, String p_email, String p_subject, String p_message, String p_fileName) {
		// Now Send Email
		try {
			mLog.info("Sending Email: ");
			EmailManager l_email = new EmailManager();
			ContactModel l_contact = new ContactModel();
			l_contact.setEmail(p_email);
			l_contact.setSubject(p_subject);
			l_contact.setMessage(p_message);
			// l_contact.setAttachement(p_fileName);
			l_contact.setNote(m_service.getConfigData().getEmailNote());
			if (p_student != null && p_student.getContact() != null && p_student.getContact().getEmail() != null) {
				l_contact.setEmail(p_student.getContact().getEmail());
				l_email.sendEmail(p_student.getContact().getEmail(), p_email, l_contact);
			} else {
				l_email.sendEmail(m_service.getConfigData().getAdminReportEmail(), p_email, l_contact);
			}
		} catch (Exception l_ex) {
			mLog.error("ERROR", l_ex);
		}
	}
}
