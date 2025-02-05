/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */

package com.obsidiansoln.web.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.obsidiansoln.blackboard.RestRequest;
import com.obsidiansoln.blackboard.course.CourseProxy;
import com.obsidiansoln.blackboard.coursecopy.CourseInfo;
import com.obsidiansoln.blackboard.templates.TemplateProxy;
import com.obsidiansoln.blackboard.user.UserProxy;
import com.obsidiansoln.database.dao.InfiniteCampusDAO;
import com.obsidiansoln.database.model.ICCalendar;
import com.obsidiansoln.database.model.ICCourse;
import com.obsidiansoln.database.model.ICEnrollment;
import com.obsidiansoln.database.model.ICSection;
import com.obsidiansoln.database.model.ICTemplate;
import com.obsidiansoln.util.LTIRequestHandler;
import com.obsidiansoln.util.LtiLaunch;
import com.obsidiansoln.util.RestManager;
import com.obsidiansoln.web.model.UserMode;
import com.obsidiansoln.web.service.BBSchedulerService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@PropertySource("classpath:application.properties")
@Controller
public class BBSchedulerController {
	private static Logger mLog = LoggerFactory.getLogger(BBSchedulerController.class);
	private BBSchedulerService m_service = null;
	@Autowired
	ServletContext servletContext;
	@Autowired
	private InfiniteCampusDAO dao;

	public final static String STATISTICS_FILE = "/statistics.csv";

	private final String SUCCESS = "{ \"success\": true}";
	private final String FAILURE = "{ \"success\": false}";

	public BBSchedulerController() {
		m_service = new BBSchedulerService();
	}

	@Value("${PROJECT_VERSION}")
	private String RELEASE;

	@RequestMapping(value = "/")
	public ModelAndView home(HttpServletResponse response) throws IOException {
		mLog.info("In Home()");
		ModelAndView mv = new ModelAndView("intro");
		mv.addObject("version", RELEASE);
		return mv;
	}



	@RequestMapping(value = "/lti", method = RequestMethod.POST)
	public ModelAndView processLti(HttpServletRequest request, HttpServletResponse response) {
		mLog.info("In processLti()");
		ModelAndView mv = new ModelAndView("intro");
		RestRequest.setCounter(0);
		String l_courseId = null;
		UserProxy l_user = null;
		CourseProxy l_course = null;
		String l_type = null;
		String l_key = request.getParameter("oauth_consumer_key");

		try {
			if (l_key.equals(m_service.getConfigData().getLtiKey())) {
				String secret = null;
				try {
					secret = m_service.getConfigData().getLtiSecret();
				} catch (Exception e1) {
					mLog.error("Error: ", e1);
				}
				try {
					StringBuffer fullUrlBuf = request.getRequestURL();
					if (request.getQueryString() != null) {
						fullUrlBuf.append('?').append(request.getQueryString());
					}

					// May need to fix this due to jakarta namespace problems
					//LtiVerificationResult ltiResult = ltiVerifier.verify((javax.servlet.http.HttpServletRequest)request, secret);
					LTIRequestHandler l_ltiHandler = new LTIRequestHandler(l_key, secret);
					//ltiVerifier.verifyParameters(request.getParameterMap(), l_courseId, l_key, secret);
					//if (!ltiResult.getSuccess()) {
					if (!(l_ltiHandler.validate(request))) {
						mLog.error("Lti verification failed! ");
						response.setStatus(HttpStatus.FORBIDDEN.value());
						return mv;
					} else {
						LtiLaunch result = new LtiLaunch(request);
						RestManager l_manager = null;
						try {
							l_manager = new RestManager(m_service.getConfigData());
							l_user = l_manager.getUserUUID(result.getUser().getId());
							if (l_user != null) {
								
								// Get The Infinite Campus Person ID
								Long l_personId = dao.getPersonId("lpotter");
								mLog.info("Person ID: " + l_personId);
								
								ObjectMapper mapper = new ObjectMapper();
								UserMode l_userMode = new UserMode();
								l_userMode.setUserId(l_user.getUserName());
								l_userMode.setApiKey(m_service.getConfigData().getApiKey());

								for (Object param : Collections.list(request.getParameterNames())) {
									mLog.debug("PARAM: " + param + "  VALUE: " + request.getParameter((String) param));
									if (param.equals("context_label")) {
										l_courseId = request.getParameter((String) param);
									} else if (param.equals("custom_tt_type")) {
										l_type = request.getParameter((String) param);
									}
								}
								
								switch (l_type) {
								case "Admin":
									l_userMode.setMode("admin");
									l_userMode.setUserId("lpotter"); // Testing
									l_userMode.setPersonId("86379"); // Testing
								
									mv.addObject("userMode", mapper.writeValueAsString(l_userMode));
								
									mv.setViewName("bbscheduler");
									
									// Test Course Link Insert
									//CourseInfo l_courseInfo = new CourseInfo();
									//l_courseInfo.setCalendarId(Long.valueOf(2222));
									//l_courseInfo.setCourseId("TEST COURSE ID");
									//l_courseInfo.setPersonId(Long.valueOf(1111));
									//l_courseInfo.setSchoolYear(Long.valueOf(2025));
									//l_courseInfo.setTargetCourseDataSource("TEST DATA SOURCE");
									//l_courseInfo.setTargetCourseDescription("TEST DESCRIPTION");
									//l_courseInfo.setTargetCourseId("SIS_123");
									//l_courseInfo.setTargetCourseName("TEST NAME");
									//l_courseInfo.setTargetCourseTerm("TEST TERM");
									
									//Number l_bbCourseId = l_manager.createCourseCopy(l_courseInfo);
									//mLog.info("Create Course Copy Key: " + l_bbCourseId);
									
									
									// Test Course Copy
									//CourseInfo l_info = new CourseInfo();
									//l_info.setCourseTemplateId("_2787_1");
									//l_info.setTargetCourseId("SAMPLE44");
									//l_info.setTargetCourseName( "Test Course");
									//l_manager.createCourseCopy(l_info);

									// Test Connection to Infinite Campus
									try {
										
										mLog.info("Testing Connection to Infinite Campus");
										//List<ICCalendar> l_calendars = dao.getByYearEnd("lpotter");
										//mLog.info("Number of Calendars Returned: " + l_calendars.size());
										//for (ICCalendar l_calendar : l_calendars) {
										//	mLog.info("Calendar ID: " + l_calendar.getCalendarID() + "  Calendar Name: " + l_calendar.getName()
										//	+ " School Name: " + l_calendar.getSchoolName());
										//}
										
										mLog.info("Testing getTemplates ...");
										//List<TemplateProxy> l_templates = l_manager.getTemplates();
										//for (TemplateProxy l_template:l_templates) {
										//	mLog.info("TEMPLATE ID: " + l_template.getTemplateId());
										//	mLog.info("TEMPLATE NAME: " + l_template.getTemplateName());
										//	mLog.info("TEMPLATE CATEGORIES: " + l_template.getCategories());
										//}
										//List<ICTemplate> l_templates = dao.getTemplates();
										//for (ICTemplate l_template : l_templates) {
										//	mLog.info("BB Master ID: " + l_template.getBbMasterId());
										//	mLog.info("BB Course ID: " + l_template.getBbCourseId());
										//	mLog.info("BB Master Level: " + l_template.getMasterLevel());
										//	mLog.info("BB Master Subject Area: " + l_template.getMasterSubjectArea());
										//}
										
										mLog.info("Testing get Courses ...");
										//List<ICCourse> l_courses = dao.getCoursesByUsername("lpotter");
										//for (ICCourse l_course1 : l_courses) {
										//	mLog.info("Course ID: " + l_course1.getCourseID() 
										//    + "  Calendar Name: " + l_course1.getCourseName()
										//	+ " School Name: " + l_course1.getSchoolName()
										//	+ " Teacher: " + l_course1.getTeacherName()
										//	+ " Linked?: " + l_course1.isLinked()
										//	+ " End Year: " + l_course1.getEndYear());
										//	List<ICSection> l_sections = dao.getSectionsByCourseId(String.valueOf(l_course1.getCourseID()), "lpotter");
										//	for (ICSection l_section : l_sections) {
										//		mLog.info("       SECTION ID: " + l_section.getSectionID());
										//		mLog.info("       SECTION Number: " + l_section.getSectionNumber());
										//		mLog.info("       SECTION Term Name: " + l_section.getTermName());
										//		mLog.info("       SECTION Period: " + l_section.getPeriod());
										//	}
										//}
										
										mLog.info("Testing get Enrollments ...");
										//List<String> l_sectionList = Arrays.asList("904461","904462","904463");
										//List<ICEnrollment> l_enrollments = dao.getEnrollmentsForSections(l_sectionList);
										//mLog.info("Number of Enrollments Returned: " + l_enrollments.size());
										//for (ICEnrollment l_enrollment : l_enrollments) {
										//	mLog.info("       Username: " + l_enrollment.getUsername());
										//}
										
									}
									catch (Exception l_ex) {
										mLog.info("Error", l_ex);
									}
									break;

								case "User":
									l_userMode.setMode("user");
									mv.addObject("userMode", mapper.writeValueAsString(l_userMode));
									mv.setViewName("bbscheduler");
									break;
							
								default:
									break;
								}
								mLog.debug("Got User:  " + l_user.getUserName());
								mLog.debug("Got Course:  " + l_courseId);
								mLog.debug("LTI Type:  " + l_type);
								mv.addObject("username", l_user.getUserName());
								mv.addObject("courseid", l_courseId);
								mv.addObject("ltimessage", "Success");
								mv.addObject("ltitype", l_type);
								mv.addObject("version", RELEASE);

							} else {
								mLog.error("Invalid User" + result.getUser().getId());
								mv.addObject("ltimessage", "Invalid User");
							}
						} catch (Exception e) {
							mLog.error("Error", e);
							mv.addObject("ltimessage", e.getMessage());
						}
					}
				} catch (Exception e) {
					mLog.error("Error", e);
					mv.addObject("ltimessage", e.getMessage());
				}
			} else {
				mv.addObject("ltimessage", "Invalid Key");
			}
		} catch (Exception e) {
			mLog.error("Error", e);
			mv.addObject("ltimessage", e.getMessage());
		}

		return mv;
	}
}
