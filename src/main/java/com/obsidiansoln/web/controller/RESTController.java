/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.web.controller;

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
import com.obsidiansoln.blackboard.model.SeparatedCourses;
import com.obsidiansoln.blackboard.model.SnapshotFileInfo;
import com.obsidiansoln.blackboard.model.StudentInfo;
import com.obsidiansoln.blackboard.model.TeacherInfo;
import com.obsidiansoln.blackboard.sis.SnapshotFileManager;
import com.obsidiansoln.blackboard.term.TermProxy;
import com.obsidiansoln.database.dao.InfiniteCampusDAO;
import com.obsidiansoln.database.model.ICBBCourse;
import com.obsidiansoln.database.model.ICBBEnrollment;
import com.obsidiansoln.database.model.ICBBGroup;
import com.obsidiansoln.database.model.ICBBSection;
import com.obsidiansoln.database.model.ICCalendar;
import com.obsidiansoln.database.model.ICCourse;
import com.obsidiansoln.database.model.ICEnrollment;
import com.obsidiansoln.database.model.ICGuardian;
import com.obsidiansoln.database.model.ICMessage;
import com.obsidiansoln.database.model.ICSection;
import com.obsidiansoln.database.model.ICSectionInfo;
import com.obsidiansoln.database.model.ICStaff;
import com.obsidiansoln.database.model.ICStudent;
import com.obsidiansoln.database.model.ICTeacher;
import com.obsidiansoln.database.model.ICTemplate;
import com.obsidiansoln.database.model.ICUser;
import com.obsidiansoln.database.model.UpdateCourseInfo;
import com.obsidiansoln.util.RestManager;
import com.obsidiansoln.web.model.AdminInfo;
import com.obsidiansoln.web.model.ConfigData;
import com.obsidiansoln.web.model.LtiInfo;
import com.obsidiansoln.web.model.PortalInfo;
import com.obsidiansoln.web.model.RestInfo;
import com.obsidiansoln.web.model.RestResponse;
import com.obsidiansoln.web.model.SnapshotInfo;
import com.obsidiansoln.web.model.ToastMessage;
import com.obsidiansoln.web.model.UtilityInfo;
import com.obsidiansoln.web.model.VerifyInfo;
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

	@RequestMapping(value = "/api/verifyAdminMode", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RestResponse verifyAdminMode(@RequestBody final VerifyInfo verifyData, HttpServletRequest request) {
		mLog.trace("In verifyAdminMode ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			try {
				ConfigData l_configData = m_service.getConfigData();
				String l_adminPW = l_configData.getAdminPW();
				if (l_adminPW.equals(verifyData.getAdminPassword())) {
					l_restResponse.setSuccess(true);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("success");
					l_toast.setMessage("UTILILITY Data Updated");
					l_restResponse.setToast(l_toast);
				} else {
					l_restResponse.setSuccess(false);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("error");
					l_toast.setMessage("Incorrect Admin Password");
					l_restResponse.setToast(l_toast);
				}
			} catch (Exception l_ex) {
				mLog.error(l_ex.getMessage());
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("ERROR on REST API");
				l_restResponse.setToast(l_toast);
			}
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
	}

	@RequestMapping(value = "/api/utilityData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getUtilityData(HttpServletRequest request) {
		mLog.trace("In getUtilityData ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				UtilityInfo l_utility = new UtilityInfo();
				l_utility.setTemplates(dao.getTemplates());
				return mapper.writeValueAsString(l_utility);
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

	@RequestMapping(value = "/api/utilityData", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RestResponse putUtiltyData(@RequestBody final UtilityInfo utilityData, HttpServletRequest request) {
		mLog.trace("In putUtilityData ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			try {

				List<ICTemplate> l_templates = utilityData.getTemplates();
				// Now Update Database
				dao.updateTemplates(l_templates);
				l_restResponse.setSuccess(true);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("success");
				l_toast.setMessage("UTILILITY Data Updated");
				l_restResponse.setToast(l_toast);
			} catch (Exception l_ex) {
				mLog.error(l_ex.getMessage());
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("ERROR on REST API");
				l_restResponse.setToast(l_toast);
			}
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
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

	@RequestMapping(value = "/api/restData", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RestResponse putRestData(@RequestBody final RestInfo restData, HttpServletRequest request) {
		mLog.trace("In putRestData ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			try {
				ConfigData l_configData = m_service.getConfigData();
				l_configData.setRestHost(restData.getHost());
				l_configData.setRestKey(restData.getKey());
				l_configData.setRestSecret(restData.getSecret());
				//m_service.saveConfigData(l_configData);

				l_restResponse.setSuccess(true);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("success");
				l_toast.setMessage("REST Data Updated");
				l_restResponse.setToast(l_toast);
			} catch (Exception l_ex) {
				mLog.error(l_ex.getMessage());
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("ERROR on REST API");
				l_restResponse.setToast(l_toast);
			}
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
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

	@RequestMapping(value = "/api/ltiData", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RestResponse putLtiData(@RequestBody final LtiInfo ltiData, HttpServletRequest request) {
		mLog.info("In putLtiData ..." + ltiData.getKey());
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			try {
				ConfigData l_configData = m_service.getConfigData();
				l_configData.setLtiKey(ltiData.getKey());
				l_configData.setLtiSecret(ltiData.getSecret());

				//m_service.saveConfigData(l_configData);
				l_restResponse.setSuccess(true);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("success");
				l_toast.setMessage("LTI Data Updated");
				l_restResponse.setToast(l_toast);

			} catch (Exception l_ex) {
				mLog.error(l_ex.getMessage());
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("ERROR on REST API");
				l_restResponse.setToast(l_toast);
			}
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
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
				l_portalData.setAdminPassword(l_configData.getAdminPW());
				l_portalData.setCustomMessages(dao.getMessages());

				// Add Terms
				List<TermProxy> l_terms = l_manager.getTerms();
				ArrayList<String> l_termList = new ArrayList<String>();
				for (TermProxy l_term : l_terms) {
					l_termList.add(l_term.getName());
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

	@RequestMapping(value = "/api/portalData", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RestResponse putPortalData(@RequestBody final PortalInfo portalData, HttpServletRequest request) {
		mLog.trace("In putPortalData ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			try {
				ConfigData l_configData = m_service.getConfigData();
				l_configData.setLogLevel(portalData.getLogLevel());
				l_configData.setAdminPW(portalData.getAdminPassword());

				//Now need to add the Messages to the Database

				//m_service.saveConfigData(l_configData);
				l_restResponse.setSuccess(true);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("success");
				l_toast.setMessage("PORTAL Data Updated");
				l_restResponse.setToast(l_toast);
			} catch (Exception l_ex) {
				mLog.error(l_ex.getMessage());
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("ERROR on REST API");
				l_restResponse.setToast(l_toast);
			}
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
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
				l_adminData.setFrom(l_configData.getEmailFrom());
				l_adminData.setHost(l_configData.getEmailHost());
				l_adminData.setPort(l_configData.getEmailPort());
				l_adminData.setUsername(l_configData.getEmailUsername());
				l_adminData.setPw(l_configData.getEmailPassword());
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

	@RequestMapping(value = "/api/adminData", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RestResponse putAdminData(@RequestBody final AdminInfo adminData, HttpServletRequest request) {
		mLog.trace("In putAdminData ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			try {
				ConfigData l_configData = m_service.getConfigData();
				l_configData.setEmailFrom(adminData.getFrom());
				l_configData.setEmailHost(adminData.getHost());
				l_configData.setEmailPort(adminData.getPort());
				l_configData.setEmailUsername(adminData.getUsername());
				l_configData.setEmailPassword(adminData.getPw());
				l_configData.setEmailAuthenticate(adminData.isAuthenticate());
				l_configData.setEmailUseSSL(adminData.isSsl());
				l_configData.setEmailDebug(adminData.isDebug());
				//m_service.saveConfigData(l_configData);

				l_restResponse.setSuccess(true);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("success");
				l_toast.setMessage("ADMIN Data Updated");
				l_restResponse.setToast(l_toast);
			} catch (Exception l_ex) {
				mLog.error("Error: ", l_ex);
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("ERROR on REST API");
				l_restResponse.setToast(l_toast);
			}
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
	}

	@RequestMapping(value = "/api/snapshotData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getSnapshotData(HttpServletRequest request) {
		mLog.info("In getSnapshotData ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				ConfigData l_configData = m_service.getConfigData();
				SnapshotInfo l_snapshotData = new SnapshotInfo();
				l_snapshotData.setBbInstanceId(l_configData.getSnapshotBbInstanceId());
				l_snapshotData.setSharedStudentUsername(l_configData.getSnapshotStudentSharedUsername());
				l_snapshotData.setSharedStudentPassword(l_configData.getSnapshotStudentSharedPassword());
				l_snapshotData.setStudentDatasource(l_configData.getSnapshotStudentDatasource());
				l_snapshotData.setSharedStaffUsername(l_configData.getSnapshotStaffSharedUsername());
				l_snapshotData.setSharedStaffPassword(l_configData.getSnapshotStaffSharedPassword());
				l_snapshotData.setStaffDatasource(l_configData.getSnapshotStaffDatasource());
				l_snapshotData.setSharedGuardianUsername(l_configData.getSnapshotGuardianSharedUsername());
				l_snapshotData.setSharedGuardianPassword(l_configData.getSnapshotGuardianSharedPassword());
				l_snapshotData.setGuardianDatasource(l_configData.getSnapshotGuardianDatasource());
				l_snapshotData.setEnrollmentDatasource(l_configData.getSnapshotEnrollmentDatasource());
				l_snapshotData.setStudentAssociationDatasource(l_configData.getSnapshotStudentAssociationDatasource());
				l_snapshotData.setStaffAssociationDatasource(l_configData.getSnapshotStaffAssociationDatasource());
				l_snapshotData.setGuardianAssociationDatasource(l_configData.getSnapshotGuardianAssociationDatasource());
				l_snapshotData.setEmail(l_configData.getSnapshotEmail());

				return mapper.writeValueAsString(l_snapshotData);
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

	@RequestMapping(value = "/api/snapshotData", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RestResponse putSnapshotData(@RequestBody final SnapshotInfo snapshotData, HttpServletRequest request) {
		mLog.info("In putSnapshotData ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			try {
				ConfigData l_configData = m_service.getConfigData();
				l_configData.setSnapshotBbInstanceId(snapshotData.getBbInstanceId());
				l_configData.setSnapshotStudentSharedUsername(snapshotData.getSharedStudentUsername());
				l_configData.setSnapshotStudentSharedPassword(snapshotData.getSharedStudentPassword());
				l_configData.setSnapshotStudentDatasource(snapshotData.getStudentDatasource());
				l_configData.setSnapshotStaffSharedUsername(snapshotData.getSharedStaffUsername());
				l_configData.setSnapshotStaffSharedPassword(snapshotData.getSharedStaffPassword());
				l_configData.setSnapshotStaffDatasource(snapshotData.getStaffDatasource());
				l_configData.setSnapshotGuardianSharedUsername(snapshotData.getSharedGuardianUsername());
				l_configData.setSnapshotGuardianSharedPassword(snapshotData.getSharedGuardianPassword());
				l_configData.setSnapshotGuardianDatasource(snapshotData.getGuardianDatasource());
				l_configData.setSnapshotEnrollmentDatasource(snapshotData.getEnrollmentDatasource());
				l_configData.setSnapshotStudentAssociationDatasource(snapshotData.getStudentAssociationDatasource());
				l_configData.setSnapshotStaffAssociationDatasource(snapshotData.getStaffAssociationDatasource());
				l_configData.setSnapshotGuardianAssociationDatasource(snapshotData.getGuardianAssociationDatasource());
				l_configData.setSnapshotEmail(snapshotData.getEmail());
				//m_service.saveConfigData(l_configData);
				l_restResponse.setSuccess(true);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("success");
				l_toast.setMessage("SNAPSHOT Data Updated");
				l_restResponse.setToast(l_toast);
			} catch (Exception l_ex) {
				mLog.error("Error: ", l_ex);
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("ERROR on REST API");
				l_restResponse.setToast(l_toast);
			}
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
	}

	@RequestMapping(value = "/api/getTemplates", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getTemplates (HttpServletRequest request) {
		mLog.trace("In getTemplates ...");
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
		mLog.trace("In getCourses ...");
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

	@RequestMapping(value = "/api/getBBCourses/{userName}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getBBCourses (@PathVariable("userName") String userName,
			HttpServletRequest request) {
		mLog.trace("In getBBCourses ...");
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

	@RequestMapping(value = "/api/updateBBCourse", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RestResponse updateBBCourse (@RequestBody final UpdateCourseInfo courseInfo, HttpServletRequest request) {
		mLog.trace("In updateBBCourse ...");
		mLog.debug("Course ID: " + courseInfo.getBbCourseId());
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			try {
				dao.updateBBCourseInfo(courseInfo);
				ConfigData l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);
				l_manager.updateCourse(courseInfo);
				l_restResponse.setSuccess(true);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("success");
				l_toast.setMessage("Course Successfully Updated");
				l_restResponse.setToast(l_toast);
			} catch (Exception e) {
				mLog.error(e.getMessage());
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("Error On REST API");
				l_restResponse.setToast(l_toast);
			}
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
	}

	@RequestMapping(value = "/api/getBBSections/{bbCourseId}/{userName}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getBBSections (@PathVariable("bbCourseId") String bbCourseId, @PathVariable("userName") String userName,
			HttpServletRequest request) {
		mLog.trace("In getBBSections ...");
		if (checkApiKey(request)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				List<ICBBSection> l_sections = dao.getBBSectionsByCourseIdUsername(bbCourseId, userName);
				return mapper.writeValueAsString(l_sections);
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
		mLog.trace("In getSections ...");
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

	@RequestMapping(value = "/api/createCourse", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RestResponse createCourse(@RequestBody final CourseInfo courseInfo, HttpServletRequest request) {
		mLog.info("In createCourse ...");
		RestResponse l_restResponse = new RestResponse();
		mLog.info(" Course ID: " + courseInfo.getCourseTemplateId());
		mLog.info(" Course Duration: " + courseInfo.getCourseDuration());
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
										l_restResponse.setSuccess(false);
										ToastMessage l_toast = new ToastMessage();
										l_toast.setType("error");
										l_toast.setMessage("Error Async Enrollments");
										l_restResponse.setToast(l_toast);
									}
								}
								CompletableFuture.allOf(data.toArray(new CompletableFuture[0])).join();
								mLog.info("All Data Returned");

								mLog.info("Number of Enrollments Returned: " + l_enrollments.size());

								// Add the Extra students
								if (courseInfo.getAdditionalStudents() != null) {
									for (String l_student : courseInfo.getAdditionalStudents()) {
										mLog.info("Adding Student: " + l_student);
										l_manager.createMembership(courseInfo.getTargetCourseId(), l_student, "Student");
										// Add to SDW Person Table
										Long l_personId = dao.getPersonId(l_student);
										PersonInfo l_personInfo = new PersonInfo();
										l_personInfo.setBbCourseId(l_key.longValue());
										l_personInfo.setPersonId(l_personId);
										l_personInfo.setPersonType("S");
										l_personInfo.setSourcePersonType("S");
										l_personInfo.setModifiedByPersonId(courseInfo.getPersonId());
										dao.insertBBPersonLink(l_personInfo);
									}
								}


								// Add the Extra teachers
								if (courseInfo.getAdditionalTeachers() != null) {
									for (String l_teacher : courseInfo.getAdditionalTeachers()) {
										mLog.info("Adding Teacher: " + l_teacher);
										l_manager.createMembership(courseInfo.getTargetCourseId(), l_teacher, "Instructor");
										// Add to SDW Person Table
										Long l_personId = dao.getPersonId(l_teacher);
										PersonInfo l_personInfo = new PersonInfo();
										l_personInfo.setBbCourseId(l_key.longValue());
										l_personInfo.setPersonId(l_personId);
										l_personInfo.setPersonType("T");
										l_personInfo.setSourcePersonType("T");
										l_personInfo.setModifiedByPersonId(courseInfo.getPersonId());
										dao.insertBBPersonLink(l_personInfo);
									}
								}

								l_restResponse.setSuccess(true);
								ToastMessage l_toast = new ToastMessage();
								l_toast.setType("success");
								l_toast.setMessage("BB Course Successfully copied/created");
								l_restResponse.setToast(l_toast);
							} else {
								mLog.error("Template Course Not Found");
								l_restResponse.setSuccess(false);
								ToastMessage l_toast = new ToastMessage();
								l_toast.setType("error");
								l_toast.setMessage("Template Course Not Found");
								l_restResponse.setToast(l_toast);
							}
						} catch (Exception e) {
							mLog.error(e.getMessage());
							l_restResponse.setSuccess(false);
							ToastMessage l_toast = new ToastMessage();
							l_toast.setType("error");
							l_toast.setMessage("Error On REST API");
							l_restResponse.setToast(l_toast);
						}

					} else {
						mLog.error ("SDW BB Course Update Failed");
						l_restResponse.setSuccess(false);
						ToastMessage l_toast = new ToastMessage();
						l_toast.setType("error");
						l_toast.setMessage("SDW BB Course Update Failed");
						l_restResponse.setToast(l_toast);
					}
				} else {
					mLog.error ("SDW BB Course Insert Failed");
					l_restResponse.setSuccess(false);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("error");
					l_toast.setMessage("SDW BB Course Insert Failed");
					l_restResponse.setToast(l_toast);
				}
			} else {
				mLog.error ("No Sections Detected");
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("No Sections Detected");
				l_restResponse.setToast(l_toast);
			}
		} else {
			mLog.error ("API KEY Incorrect");
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
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
	public RestResponse syncUsers (HttpServletRequest request) {
		mLog.info("In syncUsers() ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			mLog.info ("Starting to Sync the Users between Infinite Campus and Blackboard");
			SnapshotFileManager l_manager = new SnapshotFileManager();

			// Get Students
			List<ICUser> l_students = dao.getSISStudents();
			mLog.info("Number of Students: " + l_students.size());

			List<SnapshotFileInfo> l_files = l_manager.createStudentFile(l_students);
			for (SnapshotFileInfo l_file:l_files) {
				try {
					service.processSISFile(l_file, l_manager);
					l_restResponse.setSuccess(true);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("success");
					l_toast.setMessage("Sync Users Successfully Submitted");
					l_restResponse.setToast(l_toast);
				} catch (InterruptedException e) {
					mLog.error("Error: " + "Unable to queue Snapshot File");
					l_restResponse.setSuccess(false);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("error");
					l_toast.setMessage("Unable to queue Snapshot File");
					l_restResponse.setToast(l_toast);
				}
				//l_manager.sendFile(l_file, "person", 1, l_students.size());

			} 
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
	}

	@RequestMapping(value = "/api/syncStaff", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public RestResponse syncStaff (HttpServletRequest request) {
		mLog.info("In syncStaff() ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			mLog.info ("Starting to Sync the Staff between Infinite Campus and Blackboard");
			SnapshotFileManager l_manager = new SnapshotFileManager();
			// Get Staff
			List<ICStaff> l_staffs = dao.getSISStaff();
			mLog.info("Number of Staff: " + l_staffs.size());

			List<SnapshotFileInfo> l_files = l_manager.createStaffFile(l_staffs);
			for (SnapshotFileInfo l_file:l_files) {
				try {
					service.processSISFile(l_file, l_manager);
					l_restResponse.setSuccess(true);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("success");
					l_toast.setMessage("Sync Staff Successfully Submitted");
					l_restResponse.setToast(l_toast);
				} catch (InterruptedException e) {
					mLog.error("Error: " + "Unable to queue Snapshot File");
					l_restResponse.setSuccess(false);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("error");
					l_toast.setMessage("Unable to queue Snapshot File");
					l_restResponse.setToast(l_toast);
				}
			} 
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
	}

	@RequestMapping(value = "/api/syncEnrollments", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public RestResponse syncEnrollments (HttpServletRequest request) {
		mLog.info("In syncEnrollments() ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			mLog.info ("Starting to Sync the Enrollments between Infinite Campus and Blackboard");
			SnapshotFileManager l_manager = new SnapshotFileManager();

			List<ICBBEnrollment> l_enrollments = dao.getBBEnrollments();
			mLog.info("Number of Enrollments: " + l_enrollments.size());

			List<SnapshotFileInfo> l_files = l_manager.createEnrollmentFile(l_enrollments);
			for (SnapshotFileInfo l_file:l_files) {
				try {
					service.processSISFile(l_file, l_manager);
					l_restResponse.setSuccess(true);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("success");
					l_toast.setMessage("Sync Enrollments Successfully Submitted");
					l_restResponse.setToast(l_toast);
				} catch (InterruptedException e) {
					mLog.error("Error: " + "Unable to queue Snapshot File");
					l_restResponse.setSuccess(false);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("error");
					l_toast.setMessage("Unable to queue Snapshot File");
					l_restResponse.setToast(l_toast);
				}
			} 
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
	}

	@RequestMapping(value = "/api/syncGroups", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public RestResponse syncGroups (HttpServletRequest request) {
		mLog.info("In syncGroups() ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			mLog.info ("Starting to Sync the Groups between Infinite Campus and Blackboard");

			List<ICBBGroup> l_groups = dao.getBBGroups();
			ConfigData l_configData;
			for (ICBBGroup l_group:l_groups) {
				try {
					l_configData = m_service.getConfigData();
					RestManager l_manager = new RestManager(l_configData);
					l_manager.createGroupMembership(l_group);
					l_restResponse.setSuccess(true);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("success");
					l_toast.setMessage("Sync Groups Successfull");
					l_restResponse.setToast(l_toast);
				} catch (Exception e) {
					l_restResponse.setSuccess(false);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("error");
					l_toast.setMessage("Error On REST API");
					l_restResponse.setToast(l_toast);
				}
			}
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
	}


	@RequestMapping(value = "/api/syncGuardians", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public RestResponse syncGuardians (HttpServletRequest request) {
		mLog.info("In syncGuardians() ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			mLog.info ("Starting to Sync the Guardians between Infinite Campus and Blackboard");
			SnapshotFileManager l_manager = new SnapshotFileManager();

			List<ICGuardian> l_guardians = dao.getSISGuardians();
			mLog.info("Number of Guardians: " + l_guardians.size());

			List<SnapshotFileInfo> l_files = l_manager.createGuardianFile(l_guardians);
			for (SnapshotFileInfo l_file:l_files) {
				mLog.info("Processing File: " + l_file.getFileName());
				try {
					service.processSISFile(l_file, l_manager);
					l_restResponse.setSuccess(true);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("success");
					l_toast.setMessage("Sync Guardians Successfully Submitted");
					l_restResponse.setToast(l_toast);
				} catch (Exception e) {
					mLog.error("Error: " + "Unable to queue Snapshot File");
					l_restResponse.setSuccess(false);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("error");
					l_toast.setMessage("Unable to queue Snapshot File");
					l_restResponse.setToast(l_toast);
				}
			} 

			mLog.info("DONE");

		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
	}


	@RequestMapping(value = "/api/getStudents", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<ICStudent> getStudents(HttpServletRequest request) {
		mLog.trace("In getStudents ...");
		if (checkApiKey(request)) {
			List<ICStudent> l_students = dao.getStudents();

			return l_students;
		}

		return null;
	}

	@RequestMapping(value = "/api/getBBStudents/{courseId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<ICStudent> getBBStudents(@PathVariable("courseId") String courseId, HttpServletRequest request) {
		mLog.trace("In getBBStudents ...");
		List<ICStudent> l_students = null;
		if (checkApiKey(request)) {
			try {
				l_students = dao.getBBStudents(courseId);
			} catch (Exception ex) {

			}

		} else {

		}

		return l_students;
	}

	@RequestMapping(value = "/api/getBBTeachers/{courseId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<ICTeacher> getBBTeachers(@PathVariable("courseId") String courseId, HttpServletRequest request) {
		mLog.trace("In getBBTeachers ...");
		List<ICTeacher> l_teachers = null;
		if (checkApiKey(request)) {

			try {
				l_teachers= dao.getBBTeachers(courseId);
			} catch (Exception ex) {

			}

		} else {

		}

		return l_teachers;
	}

	@RequestMapping(value = "/api/removeBBStudent/{courseId}/{student}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public RestResponse removeBBStudent(@PathVariable("courseId") String courseId,
			@PathVariable("student") String student, HttpServletRequest request) {
		mLog.info("In removeBBStudent ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			ConfigData l_configData;
			try {
				l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);
				l_manager.removeMembership(courseId, student);

				// Remove From SDWBlackboaardSchedulerSISPersons Table
				Long l_personId = dao.getPersonId(student);
				ICBBCourse l_bbCourse = dao.getBBCourseById(courseId);
				dao.deleteBBPersonLink(Long.valueOf(l_bbCourse.getBbCourseId()), l_personId);
				l_restResponse.setSuccess(true);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("success");
				l_toast.setMessage("Remove Student Successfull");
				l_restResponse.setToast(l_toast);

			} catch (Exception ex) {
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("Error On REST API");
				l_restResponse.setToast(l_toast);
			}

		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}

		return l_restResponse;
	}


	@RequestMapping(value = "/api/addBBStudent", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RestResponse addBBStudent(@RequestBody final StudentInfo studentInfo, HttpServletRequest request) {
		mLog.trace("In addBBStudent ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			ConfigData l_configData;
			try {
				l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);

				// Add the Extra teachers
				if (studentInfo != null) {
					ICBBCourse l_bbCourse = dao.getBBCourseById(studentInfo.getCourseId());
					if (l_bbCourse != null) {
						for (String l_student : studentInfo.getStudents()) {
							mLog.info("Adding Student: " + l_student);

							l_manager.createMembership(studentInfo.getCourseId(), l_student, "Student");
							// Add to SDW Person Table
							Long l_personId = dao.getPersonId(l_student);
							PersonInfo l_personInfo = new PersonInfo();
							l_personInfo.setBbCourseId(Long.valueOf(l_bbCourse.getBbCourseId()));
							l_personInfo.setPersonId(l_personId);
							l_personInfo.setPersonType("S");
							l_personInfo.setSourcePersonType("S");
							l_personInfo.setModifiedByPersonId(Long.valueOf(studentInfo.getPersonId()));
							Number rows = dao.insertBBPersonLink(l_personInfo);
							if (rows == null) {
								mLog.error("Error Updating Persons Link Table");
							}
						}
						l_restResponse.setSuccess(true);
						ToastMessage l_toast = new ToastMessage();
						l_toast.setType("success");
						l_toast.setMessage("Add Students Successfull");
						l_restResponse.setToast(l_toast);
					} else {
						l_restResponse.setSuccess(false);
						ToastMessage l_toast = new ToastMessage();
						l_toast.setType("error");
						l_toast.setMessage("No BB Course Found");
						l_restResponse.setToast(l_toast);
					}

				} else {
					l_restResponse.setSuccess(false);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("error");
					l_toast.setMessage("No Teachers Found");
					l_restResponse.setToast(l_toast);
				}

			} catch (Exception ex) {
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("Error On REST API");
				l_restResponse.setToast(l_toast);
			}

		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}

		return l_restResponse;
	}

	@RequestMapping(value = "/api/removeBBTeacher/{courseId}/{teacher}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public RestResponse removeBBTeacher(@PathVariable("courseId") String courseId,
			@PathVariable("teacher") String teacher, HttpServletRequest request) {
		mLog.trace("In removeBBTeacher ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			ConfigData l_configData;
			try {
				l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);
				l_manager.removeMembership(courseId, teacher);

				// Remove From SDWBlackboaardSchedulerSISPersons Table
				Long l_personId = dao.getPersonId(teacher);
				ICBBCourse l_bbCourse = dao.getBBCourseById(courseId);
				dao.deleteBBPersonLink(Long.valueOf(l_bbCourse.getBbCourseId()), l_personId);
				l_restResponse.setSuccess(true);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("success");
				l_toast.setMessage("Remove Teacher Successfull");
				l_restResponse.setToast(l_toast);

			} catch (Exception ex) {
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("Error On REST API");
				l_restResponse.setToast(l_toast);
			}

		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}

		return l_restResponse;
	}


	@RequestMapping(value = "/api/addBBTeacher", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RestResponse addBBTeacher(@RequestBody final TeacherInfo teacherInfo, HttpServletRequest request) {
		mLog.trace("In addBBTeacher ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			ConfigData l_configData;
			try {
				l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);

				// Add the Extra teachers
				if (teacherInfo != null) {
					ICBBCourse l_bbCourse = dao.getBBCourseById(teacherInfo.getCourseId());
					if (l_bbCourse != null) {
						for (String l_teacher : teacherInfo.getTeachers()) {
							mLog.info("Adding Teacher: " + l_teacher);

							l_manager.createMembership(teacherInfo.getCourseId(), l_teacher, "Instructor");
							// Add to SDW Person Table
							Long l_personId = dao.getPersonId(l_teacher);
							PersonInfo l_personInfo = new PersonInfo();
							l_personInfo.setBbCourseId(Long.valueOf(l_bbCourse.getBbCourseId()));
							l_personInfo.setPersonId(l_personId);
							l_personInfo.setPersonType("T");
							l_personInfo.setSourcePersonType("T");
							l_personInfo.setModifiedByPersonId(Long.valueOf(teacherInfo.getPersonId()));
							Number rows = dao.insertBBPersonLink(l_personInfo);
							if (rows == null) {
								mLog.error("Error Updating Persons Link Table");
							}
						}
						l_restResponse.setSuccess(true);
						ToastMessage l_toast = new ToastMessage();
						l_toast.setType("success");
						l_toast.setMessage("Add Teachers Successfull");
						l_restResponse.setToast(l_toast);
					} else {
						l_restResponse.setSuccess(false);
						ToastMessage l_toast = new ToastMessage();
						l_toast.setType("error");
						l_toast.setMessage("No BB Course Found");
						l_restResponse.setToast(l_toast);
					}

				} else {
					l_restResponse.setSuccess(false);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("error");
					l_toast.setMessage("No Teachers Found");
					l_restResponse.setToast(l_toast);
				}

			} catch (Exception ex) {
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("Error On REST API");
				l_restResponse.setToast(l_toast);
			}

		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}

		return l_restResponse;
	}

	@RequestMapping(value = "/api/removeSection/{sectionId}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public RestResponse removeSection(@PathVariable("sectionId") String sectionId, HttpServletRequest request) {
		mLog.info("In removeSection ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			String l_course = dao.removeSection(sectionId);
			mLog.info("Course ID: " + l_course);
			if (l_course != null) {
				ConfigData l_configData;
				List<String> sections = new ArrayList<String>();
				sections.add(sectionId);
				List<ICEnrollment> l_enrollments = dao.getEnrollmentsForSections(sections);
				RestManager l_manager=null;
				try {
					l_configData = m_service.getConfigData();
					l_manager = new RestManager(l_configData);
					CourseProxy l_courseProxy = l_manager.getCourseByName(l_course);
					if (l_courseProxy != null) {
						for (ICEnrollment l_enrollment:l_enrollments) {
							if (l_enrollment.getRole().equals("Student")) {
								l_manager.removeMembership(l_course, l_enrollment.getUsername());
							}
						} 

						// Remove the Group associated with this Section
						l_manager.deleteCourseGroup (l_courseProxy.getId(), sectionId);

						l_restResponse.setSuccess(true);
						ToastMessage l_toast = new ToastMessage();
						l_toast.setType("success");
						l_toast.setMessage("Section Successfully Removed");
						l_restResponse.setToast(l_toast);
					} else {
						mLog.error("Blackboard Course Not Found");
						l_restResponse.setSuccess(true);
						ToastMessage l_toast = new ToastMessage();
						l_toast.setType("success");
						l_toast.setMessage("Blackboard Course Not Found");
						l_restResponse.setToast(l_toast);
					}
				} catch (Exception e) {
					mLog.error(e.getMessage());
					l_restResponse.setSuccess(false);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("error");
					l_toast.setMessage("Error On REST API");
					l_restResponse.setToast(l_toast);
				}
			} else {
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("Provided Course ID Empty");
				l_restResponse.setToast(l_toast);
			}
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
	}

	@RequestMapping(value = "/api/addSection/{courseId}/{sectionId}/{personId}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public RestResponse addSection(@PathVariable("courseId") String courseId, @PathVariable("sectionId") String sectionId, @PathVariable("personId") String personId , HttpServletRequest request) {
		mLog.info("In addSection ...");
		mLog.info("COURSE ID: " + courseId);
		mLog.info("SECTION ID: " + sectionId);
		mLog.info("PERSON ID: " + personId);
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {
			// Need to add to SDWBlackboardSchedulerSISCourseSections and Add Enrollments to Course
			ConfigData l_configData;
			try {
				l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);

				// Need to Add Group and Enrollments
				// Create Course Group
				ICBBCourse l_bbCourse = dao.getBBCourseById(courseId);
				if (l_bbCourse != null) {

					// If this is a BB Course Created before BB Scheduler, need to add in Group Set
					if (l_bbCourse.getGroupSetId() == null) {
						HashMap<String,GroupProxy> l_groups =l_manager.createCourseGroup(courseId, null);
						// Update SDWBlackboardSchedulerBbCourse with GroupSetId
						dao.updateBBCourseGroupSet(l_bbCourse.getId(), l_groups.get(l_bbCourse.getBbCourseId()), personId);
					}

					SectionInfo l_sectionInfo = new SectionInfo();
					ICSectionInfo l_sectionICInfo = dao.getSectionInfo(sectionId);
					if (l_sectionICInfo != null) {
						l_sectionInfo.setBbCourseId(Long.valueOf(l_bbCourse.getBbCourseId()));
						l_sectionInfo.setCalendarId(l_sectionICInfo.getCalendarID());
						l_sectionInfo.setCourseId(l_sectionICInfo.getCourseID());
						l_sectionInfo.setSectionId(l_sectionICInfo.getSectionID());
						l_sectionInfo.setPersonId(Long.valueOf(personId));
						l_sectionInfo.setSectionNumber(l_sectionICInfo.getSectionNumber());

						HashMap<String,GroupProxy> l_list = l_manager.createCourseGroup(courseId, l_sectionInfo, l_bbCourse.getGroupSetId());
						if (l_list != null) {

							List<ICEnrollment> l_enrollments = dao.addSection(courseId, sectionId, Long.valueOf(personId), l_list.get(sectionId).getId());
							if (l_enrollments != null) {
								for (ICEnrollment l_enrollment:l_enrollments) {
									mLog.info("Adding User: " + l_enrollment.getUsername());
									l_manager.createMembership(courseId, l_enrollment.getUsername(), l_enrollment.getRole());
									l_manager.createGroupMembership(courseId, l_enrollment, l_list);
								}
								l_restResponse.setSuccess(true);
								ToastMessage l_toast = new ToastMessage();
								l_toast.setType("success");
								l_toast.setMessage("Section Successfully Added");
								l_restResponse.setToast(l_toast);
							}
						}
					} else {
						mLog.error("ERROR: Unable to find IC Section");
						l_restResponse.setSuccess(false);
						ToastMessage l_toast = new ToastMessage();
						l_toast.setType("error");
						l_toast.setMessage("Unable to find IC Section");
						l_restResponse.setToast(l_toast);
					}
				} else {
					mLog.error("ERROR: Unable to find Blackboard Course");
					l_restResponse.setSuccess(false);
					ToastMessage l_toast = new ToastMessage();
					l_toast.setType("error");
					l_toast.setMessage("Unable to find Blackboard Course");
					l_restResponse.setToast(l_toast);
				}
			} catch (Exception e) {
				mLog.error("ERROR: ", e);
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("Error On REST API");
				l_restResponse.setToast(l_toast);
			}
		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
	}


	@RequestMapping(value = "/api/cleanupData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public RestResponse cleanupData(HttpServletRequest request) {
		mLog.info("In cleanUpData ...");
		RestResponse l_restResponse = new RestResponse();
		if (checkApiKey(request)) {

			dao.deleteBBCourses();
			dao.deleteBBSections();

			ConfigData l_configData;
			try {
				l_configData = m_service.getConfigData();
				RestManager l_manager = new RestManager(l_configData);
				List<String> l_courses = l_manager.getCoursesByDate("2025-03-15T22:22:10.002Z", true);
				mLog.info(" Number of Courses: " + l_courses.size());
				for (String l_course:l_courses) {
					mLog.info("Deleting Course: " + l_course);
					l_manager.deleteCourse(l_course);
				}

				l_restResponse.setSuccess(true);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("success");
				l_toast.setMessage("Cleanup Data Successfull");
				l_restResponse.setToast(l_toast);
			} catch (Exception ex) {
				l_restResponse.setSuccess(false);
				ToastMessage l_toast = new ToastMessage();
				l_toast.setType("error");
				l_toast.setMessage("Error On REST API");
				l_restResponse.setToast(l_toast);
			}

		} else {
			l_restResponse.setSuccess(false);
			ToastMessage l_toast = new ToastMessage();
			l_toast.setType("error");
			l_toast.setMessage("Apikey not found/incorrect");
			l_restResponse.setToast(l_toast);
		}
		return l_restResponse;
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

	@RequestMapping(value = "/api/getMessages", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<String> getMessages(HttpServletRequest request) {
		mLog.trace("In getMessages...");
		if (checkApiKey(request)) {
			List<String> l_returnList = new ArrayList<String>();
			List <ICMessage> l_messages = dao.getMessages();
			for (ICMessage l_message: l_messages) {
				l_returnList.add(l_message.getMessage());
			}
			return l_returnList;
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

	public String getPersonId(String username) {
		return this.getPersonId(username);
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

}
