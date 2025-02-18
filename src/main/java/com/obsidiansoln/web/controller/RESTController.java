package com.obsidiansoln.web.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
import com.obsidiansoln.blackboard.coursecopy.PersonInfo;
import com.obsidiansoln.blackboard.coursecopy.SectionInfo;
import com.obsidiansoln.blackboard.group.GroupProxy;
import com.obsidiansoln.blackboard.model.BBRestCounts;
import com.obsidiansoln.blackboard.model.Grades;
import com.obsidiansoln.blackboard.model.SeparatedCourses;
import com.obsidiansoln.blackboard.sis.SnapshotFileManager;
import com.obsidiansoln.blackboard.term.TermProxy;
import com.obsidiansoln.blackboard.user.UserProxy;
import com.obsidiansoln.database.dao.InfiniteCampusDAO;
import com.obsidiansoln.database.model.ICBBCourse;
import com.obsidiansoln.database.model.ICCalendar;
import com.obsidiansoln.database.model.ICCourse;
import com.obsidiansoln.database.model.ICEnrollment;
import com.obsidiansoln.database.model.ICSection;
import com.obsidiansoln.database.model.ICSectionInfo;
import com.obsidiansoln.database.model.ICStaff;
import com.obsidiansoln.database.model.ICStudent;
import com.obsidiansoln.database.model.ICTeacher;
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
import com.obsidiansoln.web.service.AsyncService;
import com.obsidiansoln.web.service.BBSchedulerService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RESTController {

	private static Logger mLog = LoggerFactory.getLogger(RESTController.class);
	private BBSchedulerService m_service = null;
	@Autowired
	private InfiniteCampusDAO dao;
	@Autowired
	private AsyncService service;

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

	@RequestMapping(value = "/api/getCourses", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getCourses (HttpServletRequest request) {
		mLog.info("In getCourses ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				List<ICCourse> l_courses = dao.getCourses();
				return mapper.writeValueAsString(l_courses);
			} catch (Exception e) {
				mLog.error(e.getMessage());
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}

	@RequestMapping(value = "/api/getBBCourses/{userName}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getBBCourses (@PathVariable("userName") String userName,
			HttpServletRequest request) {
		mLog.info("In getCourses ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				List<ICBBCourse> l_courses = dao.getBBCoursesByUsername(userName);
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
				List<ICSection> l_sections = dao.getSectionsByCourseIdUsername(courseId, userName);

				//  Add in the BB URL If there is a Linked Course
				for (ICSection l_section:l_sections) {
					if (l_section.getLinkedCourseName() != null) {
						ConfigData l_configData = m_service.getConfigData();
						RestManager l_manager = new RestManager(l_configData);
						if (l_section.getLinkedCourseId() != null) {
							CourseProxy l_course = l_manager.getCourseByName(l_section.getLinkedCourseId());
							if (l_course != null) {
								l_section.setLinkedCourseURL(l_configData.getRestHost()+"/ultra/courses/"+ l_course.getId() +"/outline");
							} 
						}
					}
				}
				return mapper.writeValueAsString(l_sections);
			} catch (Exception e) {
				mLog.error(e.getMessage());
				return FAILURE;
			}
		} else {
			return FAILURE;
		}
	}

	@RequestMapping(value = "/api/getSections/{courseId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getSections (@PathVariable("courseId") String courseId, HttpServletRequest request) {
		mLog.info("In getSections ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				List<ICSection> l_sections = dao.getSectionsByCourseId(courseId);

				//  Add in the BB URL If there is a Linked Course
				for (ICSection l_section:l_sections) {
					if (l_section.getLinkedCourseName() != null) {
						ConfigData l_configData = m_service.getConfigData();
						RestManager l_manager = new RestManager(l_configData);
						if (l_section.getLinkedCourseId() != null) {
							CourseProxy l_course = l_manager.getCourseByName(l_section.getLinkedCourseId());
							if (l_course != null) {
								l_section.setLinkedCourseURL(l_configData.getRestHost()+"/ultra/courses/"+ l_course.getId() +"/cl/outline");
							} 
						}
					}
				}
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
		mLog.info(" Additional Students: " + courseInfo.getAdditionalStudents());
		mLog.info(" Additional Teachers: " + courseInfo.getAdditionalTeachers());
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
							if (l_course != null) {
								courseInfo.setCourseTemplateId(l_course.getId());
								l_manager.createCourseCopy(courseInfo);


								// Update The Section Link Info
								List<SectionInfo> l_sectionInfoList = new ArrayList<SectionInfo>();
								for (String l_section : courseInfo.getSections()) {
									SectionInfo l_sectionInfo = new SectionInfo();
									ICSectionInfo l_sectionICInfo = dao.getSectionInfo(l_section);
									l_sectionInfo.setBbCourseId(l_key.longValue());
									l_sectionInfo.setCalendarId(l_sectionICInfo.getCalendarID());
									l_sectionInfo.setCourseId(l_sectionICInfo.getCourseID());
									l_sectionInfo.setSectionId(l_sectionICInfo.getSectionID());
									l_sectionInfo.setPersonId(courseInfo.getPersonId());
									l_sectionInfo.setSectionNumber(l_sectionICInfo.getSectionNumber());
									l_sectionInfoList.add(l_sectionInfo);
								}

								// Add Groups to BB Course
								HashMap<String,GroupProxy> l_list=l_manager.createCourseGroup(courseInfo.getTargetCourseId(), l_sectionInfoList);
								
								//Update the SDWBlackboardSchedulerBBCourses with groupSetId
								dao.updateBBCourseGroupSet(l_key, l_list.get(courseInfo.getTargetCourseId()), String.valueOf(courseInfo.getPersonId()));
								
								//Update the SDWBlackboardSchedulerSISCourseSection
								for (SectionInfo l_sec : l_sectionInfoList) {
									l_sec.setGroupId(l_list.get(String.valueOf(l_sec.getSectionId())).getId());
									dao.insertBBSectionLink(l_sec);
								}


								// Create Enrollments In New Course
								List<ICEnrollment> l_enrollments = dao.getEnrollmentsForSections(courseInfo.getSections());

								// Test Async to increase Performance
								ArrayList<CompletableFuture<List<String>>> data = new ArrayList<CompletableFuture<List<String>>>();
								List<SeparatedCourses> l_reportList = new ArrayList<SeparatedCourses>();

								for (ICEnrollment l_enrollment : l_enrollments) {
									try {
										data.add(service.processEnrollment(courseInfo.getTargetCourseId(), l_enrollment, l_list));
									} catch (InterruptedException e) {
										mLog.error(e.getMessage());
										return FAILURE;
									}
								}
								CompletableFuture.allOf(data.toArray(new CompletableFuture[0])).join();
								mLog.info("All Data Returned");

								mLog.info("Number of Enrollments Returned: " + l_enrollments.size());
								//for (ICEnrollment l_enrollment : l_enrollments) {
								//	l_manager.createMembership(courseInfo.getTargetCourseId(), l_enrollment.getUsername(), "Student");
								//	l_manager.updateGroup(courseInfo.getTargetCourseId(), l_enrollment, l_list);
								//}

								// Add the Extra students
								if (courseInfo.getAdditionalStudents() != null) {
									for (String l_student : courseInfo.getAdditionalStudents()) {
										mLog.info("Adding Student: " + l_student);
										l_manager.createMembership(courseInfo.getTargetCourseId(), l_student, "Student");
									}
								}

								// Add the Extra teachers
								if (courseInfo.getAdditionalTeachers() != null) {
									for (String l_teacher : courseInfo.getAdditionalTeachers()) {
										mLog.info("Adding Teacher: " + l_teacher);
									}
								}
								// Add to SDW Person Table
								PersonInfo l_personInfo = new PersonInfo();
								//l_personInfo.setBbCourseId(l_key.longValue());
								//l_personInfo.setPersonId(l_enrollment.getPersonId());
								//l_personInfo.setPersonType('S');
								//l_personInfo.setSourcePersonType('S');
								//dao.insertBBPersonLink(l_personInfo);
								return SUCCESS;
							} else {
								mLog.error("Template Course Not Found");
								return FAILURE;
							}
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
	public List<ICStudent> getStudents(HttpServletRequest request) {
		mLog.info("In getStudents ...");
		if (checkApiKey(request)) {
			List<ICStudent> l_students = dao.getStudents();

			return l_students;
		}

		return null;
	}

	@RequestMapping(value = "/api/removeSection/{sectionId}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public String removeSection(@PathVariable("sectionId") String sectionId, HttpServletRequest request) {
		mLog.info("In removeSection ...");
		int l_result = 0;
		if (checkApiKey(request)) {
			String l_course = dao.removeSection(sectionId);
			mLog.info("Course ID: " + l_course);
			if (l_course != null) {
				ConfigData l_configData;
				List<String> sections = new ArrayList<String>();
				sections.add(sectionId);
				List<ICEnrollment> l_enrollments = dao.getEnrollmentsForSections(sections);
				for (ICEnrollment l_enrollment:l_enrollments) {
					try {
						l_configData = m_service.getConfigData();
						RestManager l_manager = new RestManager(l_configData);
						l_manager.removeMembership(l_course, l_enrollment.getUsername());
					} catch (Exception e) {
						mLog.error(e.getMessage());
						return FAILURE;
					}
					return SUCCESS;
				}
			} else {
				mLog.error("Course Not Found for Section");
				return FAILURE;
			}
		} else {
			return FAILURE;
		}

		return FAILURE;
	}

	@RequestMapping(value = "/api/addSection/{courseId}/{sectionId}/{userName}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String addSection(@PathVariable("courseId") String courseId, @PathVariable("sectionId") String sectionId, @PathVariable("userName") String userName , HttpServletRequest request) {
		mLog.info("In addSection ...");
		mLog.info("COURSE ID: " + courseId);
		mLog.info("SECTION ID: " + sectionId);
		mLog.info("USERNAME: " + userName);
		if (checkApiKey(request)) {
			Long personId = dao.getPersonId(userName);
			// Need to add to SDWBlackboardSchedulerSISCourseSections and Add Enrollments to Course
			ConfigData l_configData;
			try {
				l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);

				// Need to Add Group and Enrollments
				// Create Course Group
				ICBBCourse l_bbCourse = dao.getBBCourseById(courseId);
				SectionInfo l_sectionInfo = new SectionInfo();
				ICSectionInfo l_sectionICInfo = dao.getSectionInfo(sectionId);
				l_sectionInfo.setBbCourseId(Long.valueOf(l_bbCourse.getBbCourseId()));
				l_sectionInfo.setCalendarId(l_sectionICInfo.getCalendarID());
				l_sectionInfo.setCourseId(l_sectionICInfo.getCourseID());
				l_sectionInfo.setSectionId(l_sectionICInfo.getSectionID());
				l_sectionInfo.setPersonId(personId);
				l_sectionInfo.setSectionNumber(l_sectionICInfo.getSectionNumber());

				HashMap<String,GroupProxy> l_list = l_manager.createCourseGroup(courseId, l_sectionInfo, l_bbCourse.getGroupSetId());
				if (l_list != null) {

					List<ICEnrollment> l_enrollments = dao.addSection(courseId, sectionId, personId, l_list.get(sectionId).getId());
					if (l_enrollments != null) {
						for (ICEnrollment l_enrollment:l_enrollments) {
							mLog.info("Adding User: " + l_enrollment.getUsername());
							l_manager.createMembership(courseId, l_enrollment.getUsername(), "Student");
							l_manager.createGroupMembership(courseId, l_enrollment, l_list);
						}
						return SUCCESS;
					}
				}
			} catch (Exception e) {
				mLog.error("ERROR: ", e);
				return FAILURE;
			}
		}
		return FAILURE;
	}


	@RequestMapping(value = "/api/cleanupData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String cleanupData(HttpServletRequest request) {
		mLog.info("In cleanUpData ...");
		if (checkApiKey(request)) {

			dao.deleteBBCourses();
			dao.deleteBBSections();
			
			ConfigData l_configData;
			try {
				l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);
				//l_manager.getCoursesByDate("2025-02-01T22:22:10.002Z");
				//l_manager.deleteBBCourses();
			} catch (Exception ex) {
				
			}
			
		}
		return SUCCESS;
	}

	@RequestMapping(value = "/api/getTeachers", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<ICTeacher> getTeachers(HttpServletRequest request) {
		mLog.trace("In getTeachers ...");
		if (checkApiKey(request)) {

			List <ICTeacher> l_teachers = dao.getTeachers();
			return l_teachers;
		}

		return null;
	}

	@RequestMapping(value = "/api/test", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String test(HttpServletRequest request) {
		mLog.info("In test ...");

		ConfigData l_configData;
		try {
			l_configData = m_service.getConfigData();
			RestManager l_manager = new RestManager(l_configData);

			//l_manager.createCourseGroup("SIS_2425NorthHighSchool_000008298", 123);

		} catch (Exception e) {
			return FAILURE;
		}


		return SUCCESS;
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
