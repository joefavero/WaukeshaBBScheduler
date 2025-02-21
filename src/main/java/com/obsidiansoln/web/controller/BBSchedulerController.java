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

					LTIRequestHandler l_ltiHandler = new LTIRequestHandler(l_key, secret);

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
							String l_mode = "teacher";
							if (l_user != null) {
								// Check for User System Role
								String[] l_systemRoles = l_user.getSystemRoleIds();
								for (String l_systemRole:l_systemRoles) {
									if (l_systemRole.equals("CourseCreator")) {
										l_mode = "admin";
									}
								}

								// Get The Infinite Campus Person ID
								//Long l_personId = dao.getPersonId(l_user.getUserName());
								Long l_personId = dao.getPersonId("lpotter");
								mLog.info("Person ID: " + l_personId);

								if (l_personId != null) {

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


									//l_userMode.setMode(l_mode);
									//l_userMode.setUserId(l_user.getUserName()); // Testing
									//mv.addObject("userMode", mapper.writeValueAsString(l_userMode));
									//mv.setViewName("bbscheduler");


									// Testing
									l_userMode.setMode("teacher");
									l_userMode.setUserId("lpotter"); 
									mv.addObject("userMode", mapper.writeValueAsString(l_userMode));
									mv.setViewName("bbscheduler");



									mLog.debug("Got User:  " + l_user.getUserName());
									mLog.debug("LTI Type:  " + l_type);
									mv.addObject("username", l_user.getUserName());
									mv.addObject("ltimessage", "Success");
									mv.addObject("ltitype", l_type);
									mv.addObject("version", RELEASE);
								} else {
									mLog.error("Invalid IC User: " + l_user.getUserName());
									mv.addObject("ltimessage", "Invalid User");
								}

							} else {
								mLog.error("Invalid User: " + result.getUser().getId());
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
