/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.obsidiansoln.blackboard.Authorizer;
import com.obsidiansoln.blackboard.IGradesDb;
import com.obsidiansoln.blackboard.PagingProxy;
import com.obsidiansoln.blackboard.RestRequest;
import com.obsidiansoln.blackboard.Token;
import com.obsidiansoln.blackboard.course.CategoryHandler;
import com.obsidiansoln.blackboard.course.CategoryListProxy;
import com.obsidiansoln.blackboard.course.CategoryProxy;
import com.obsidiansoln.blackboard.course.CategoryResponseProxy;
import com.obsidiansoln.blackboard.course.CourseHandler;
import com.obsidiansoln.blackboard.course.CourseListProxy;
import com.obsidiansoln.blackboard.course.CourseProxy;
import com.obsidiansoln.blackboard.course.CourseResponseProxy;
import com.obsidiansoln.blackboard.course.TaskHandler;
import com.obsidiansoln.blackboard.course.TaskProxy;
import com.obsidiansoln.blackboard.coursecopy.CourseCopyHandler;
import com.obsidiansoln.blackboard.coursecopy.CourseInfo;
import com.obsidiansoln.blackboard.coursecopy.SectionInfo;
import com.obsidiansoln.blackboard.datasource.DatasourceHandler;
import com.obsidiansoln.blackboard.datasource.DatasourceListProxy;
import com.obsidiansoln.blackboard.datasource.DatasourceProxy;
import com.obsidiansoln.blackboard.datasource.DatasourceResponseProxy;
import com.obsidiansoln.blackboard.gradebook.AttemptHandler;
import com.obsidiansoln.blackboard.gradebook.AttemptListProxy;
import com.obsidiansoln.blackboard.gradebook.AttemptProxy;
import com.obsidiansoln.blackboard.gradebook.AttemptResponseProxy;
import com.obsidiansoln.blackboard.gradebook.ColumnHandler;
import com.obsidiansoln.blackboard.gradebook.ColumnListProxy;
import com.obsidiansoln.blackboard.gradebook.ColumnProxy;
import com.obsidiansoln.blackboard.gradebook.ColumnResponseProxy;
import com.obsidiansoln.blackboard.gradebook.DisplayGradeProxy;
import com.obsidiansoln.blackboard.gradebook.ElementListProxy;
import com.obsidiansoln.blackboard.gradebook.ElementProxy;
import com.obsidiansoln.blackboard.gradebook.FormulaHandler;
import com.obsidiansoln.blackboard.gradebook.FormulaParserProxy;
import com.obsidiansoln.blackboard.gradebook.GradebookHandler;
import com.obsidiansoln.blackboard.gradebook.GradebookListProxy;
import com.obsidiansoln.blackboard.gradebook.GradebookProxy;
import com.obsidiansoln.blackboard.gradebook.GradebookResponseProxy;
import com.obsidiansoln.blackboard.gradebook.SchemaHandler;
import com.obsidiansoln.blackboard.gradebook.SchemaListProxy;
import com.obsidiansoln.blackboard.gradebook.SchemaProxy;
import com.obsidiansoln.blackboard.gradebook.SchemaResponseProxy;
import com.obsidiansoln.blackboard.group.GroupHandler;
import com.obsidiansoln.blackboard.group.GroupProxy;
import com.obsidiansoln.blackboard.membership.Available;
import com.obsidiansoln.blackboard.membership.EnrollmentOptionProxy;
import com.obsidiansoln.blackboard.membership.MembershipHandler;
import com.obsidiansoln.blackboard.membership.MembershipListProxy;
import com.obsidiansoln.blackboard.membership.MembershipProxy;
import com.obsidiansoln.blackboard.membership.MembershipResponseProxy;
import com.obsidiansoln.blackboard.model.BBRestCounts;
import com.obsidiansoln.blackboard.model.Exclude;
import com.obsidiansoln.blackboard.model.Grades;
import com.obsidiansoln.blackboard.model.HTTPStatus;
import com.obsidiansoln.blackboard.model.NotInListStatus;
import com.obsidiansoln.blackboard.model.RequestData;
import com.obsidiansoln.blackboard.model.StudentData;
import com.obsidiansoln.blackboard.templates.TemplateHandler;
import com.obsidiansoln.blackboard.templates.TemplateListProxy;
import com.obsidiansoln.blackboard.templates.TemplateProxy;
import com.obsidiansoln.blackboard.templates.TemplateResponseProxy;
import com.obsidiansoln.blackboard.term.TermHandler;
import com.obsidiansoln.blackboard.term.TermListProxy;
import com.obsidiansoln.blackboard.term.TermProxy;
import com.obsidiansoln.blackboard.term.TermResponseProxy;
import com.obsidiansoln.blackboard.user.ParentHandler;
import com.obsidiansoln.blackboard.user.UserHandler;
import com.obsidiansoln.blackboard.user.UserListProxy;
import com.obsidiansoln.blackboard.user.UserProxy;
import com.obsidiansoln.blackboard.user.UserResponseProxy;
import com.obsidiansoln.database.model.ICBBGroup;
import com.obsidiansoln.database.model.ICEnrollment;
import com.obsidiansoln.database.model.UpdateCourseInfo;
import com.obsidiansoln.web.model.ConfigData;
import com.obsidiansoln.web.model.LocationInfo;

public class RestManager implements IGradesDb {

	private static Token m_token = null;
	private static Timestamp m_refreshTime = null;
	private static ConfigData m_configData = null;
	private static final Logger log = LoggerFactory.getLogger(RestManager.class);
	private ConcurrentHashMap<String, CourseProxy> m_courseCache = new ConcurrentHashMap<String, CourseProxy>();
	private ConcurrentHashMap<String, List<ColumnProxy>> m_courseColumnCache = new ConcurrentHashMap<String, List<ColumnProxy>>();
	private ConcurrentHashMap<String, UserProxy> m_userCache = new ConcurrentHashMap<String, UserProxy>();
	private ConcurrentHashMap<String, ColumnProxy> m_columnCache = new ConcurrentHashMap<String, ColumnProxy>();
	private ConcurrentHashMap<String, List<GradebookProxy>> m_gradeCache = new ConcurrentHashMap<String, List<GradebookProxy>>();
	private ConcurrentHashMap<String, List<AttemptProxy>> m_attemptCache = new ConcurrentHashMap<String, List<AttemptProxy>>();
	private ConcurrentHashMap<String, List<StudentData>> m_studentCache = new ConcurrentHashMap<String, List<StudentData>>();
	private ConcurrentHashMap<String, List<StudentData>> m_instructorCache = new ConcurrentHashMap<String, List<StudentData>>();
	private List<TermProxy> m_termCache = new ArrayList<TermProxy>();
	private List<CategoryProxy> m_categoryCache = new ArrayList<CategoryProxy>();

	public RestManager(ConfigData p_configData) {
		log.trace("In authorize()");
		m_configData = p_configData;
		Authorizer auth = new Authorizer();
		m_token = auth.authorize(m_configData);
		while (m_token == null) {
			log.info("Token is Null");
			m_token = auth.authorize(m_configData);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				log.error("Error: " + e);
			}
		}
		m_refreshTime = new Timestamp(System.currentTimeMillis() + (Long.valueOf(m_token.getExpiry()) * 1000));
		log.debug("Token: " + m_token.getToken());
		log.debug("Token Expires: " + m_token.getExpiry());
	}

	public static synchronized Token getToken() {
		checkToken();
		return m_token;
	}

	public BBRestCounts getBBRestCounts() {
		log.trace("In getBBRestCounts()");
		BBRestCounts l_return = new BBRestCounts();
		try {
			HttpHeaders l_headers = RestRequest.sendRequest(m_configData.getRestHost(), "/learn/api/public/v1/terms",
					HttpMethod.GET, m_token.getToken());

			for (Map.Entry<String, List<String>> l_entry : l_headers.entrySet()) {
				if (l_entry.getKey().equalsIgnoreCase("X-Rate-Limit-Remaining")) {
					l_return.setL_rateLimitRemaining(Integer.parseInt(l_entry.getValue().get(0)));
				} else if (l_entry.getKey().equalsIgnoreCase("X-Rate-Limit-Limit")) {
					l_return.setL_rateLimit(Integer.parseInt(l_entry.getValue().get(0)));
				} else if (l_entry.getKey().equalsIgnoreCase("X-Rate-Limit-Reset")) {
					l_return.setL_rateLimitReset(Integer.parseInt(l_entry.getValue().get(0)));
				}
			}

		} catch (Exception l_ex) {
			log.error("Error: " + l_ex);
		}
		return l_return;
	}

	public static synchronized void checkToken() {
		log.trace("In checkToken()");

		// Refresh Token
		while (m_token == null || System.currentTimeMillis() >= m_refreshTime.getTime()) {
			refreshToken();
		}
	}

	static public synchronized void refreshToken() {
		log.trace("In refreshToken()");
		Authorizer auth = new Authorizer();
		m_token = auth.authorize(m_configData);
		if (m_token != null) {
			log.info("Token: " + m_token.getToken());
			m_refreshTime = new Timestamp(System.currentTimeMillis() + (Long.valueOf(m_token.getExpiry()) * 1000));
			log.info("Token Expiry: " + m_refreshTime);
		} else {
			while (m_token == null) {
				log.info("Token is Null");
				m_token = auth.authorize(m_configData);
				if (m_token != null) {
					m_refreshTime = new Timestamp(
							System.currentTimeMillis() + (Long.valueOf(m_token.getExpiry()) * 1000));
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					log.error("Error: " + e);
				}
			}
		}
	}

	public DatasourceProxy getDataSourceByName(String p_dataSourceName) {
		log.trace("In getDataSourceByName()");
		DatasourceHandler l_datasourceHandler = new DatasourceHandler();
		RequestData l_requestData = new RequestData();
		checkToken();
		DatasourceResponseProxy l_datasourceResponse = l_datasourceHandler.getClientData(m_configData.getRestHost(),
				m_token.getToken(), null, l_requestData); // Get
		if (l_datasourceResponse != null) {
			DatasourceListProxy l_list = l_datasourceResponse.getResults();
			PagingProxy l_page = l_datasourceResponse.getPaging();
			while (l_page != null) {
				for (DatasourceProxy l_datasource : l_list) {
					if (l_datasource.getExternalId().equalsIgnoreCase(p_dataSourceName)) {
						return l_datasource;
					}
				}
				// Now Iterate to next Page
				checkToken();
				l_datasourceResponse = l_datasourceHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_list = l_datasourceResponse.getResults();
				l_page = l_datasourceResponse.getPaging();

			}

			// Process Last Page
			for (DatasourceProxy l_datasource : l_list) {
				if (l_datasource.getExternalId().equalsIgnoreCase(p_dataSourceName)) {
					return l_datasource;
				}
			}
		}
		return null;
	}

	public UserProxy getUserId(String p_user) {
		log.trace("In getUser()");
		UserProxy l_user = null;
		if (m_userCache.containsKey(p_user)) {
			log.debug("USER CACHE HIT ...");
			return m_userCache.get(p_user);
		} else {
			UserHandler l_userHandler = new UserHandler();
			RequestData l_requestData = new RequestData();
			l_requestData.setUserId(p_user);
			checkToken();
			l_user = l_userHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_requestData);
			if (l_user != null)
				m_userCache.put(l_user.getId(), l_user);
		}
		return l_user;
	}

	public UserProxy getUserName(String p_user) {
		log.trace("In getUserName()");
		UserProxy l_user = null;
		UserHandler l_userHandler = new UserHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setUserName(p_user);
		checkToken();
		l_user = l_userHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_requestData);
		if (l_user != null)
			m_userCache.put(l_user.getId(), l_user);
		return l_user;
	}

	public UserProxy getUserUUID(String p_uuid) {
		log.trace("In getUserUUID()");
		UserProxy l_user = null;
		UserHandler l_userHandler = new UserHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setUuid(p_uuid);
		checkToken();
		l_user = l_userHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_requestData);
		if (l_user != null)
			m_userCache.put(l_user.getId(), l_user);
		return l_user;
	}

	public UserProxy getUserExternalId(String p_externalId) {
		log.trace("In getUserExternalId()");
		UserProxy l_user = null;

		UserHandler l_userHandler = new UserHandler();
		RequestData l_requestData = new RequestData();
		checkToken();
		l_user = l_userHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_requestData);

		if (l_user != null)
			m_userCache.put(l_user.getId(), l_user);
		return l_user;
	}

	public int createParentAssociation(String p_studentId, String p_parentId) {
		log.info("In createParentAssociation()");
		log.info("In createParentAssociation()" + p_studentId);
		log.info("In createParentAssociation()" + p_parentId);
		ParentHandler l_parentHandler = new ParentHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setUserId(p_studentId);
		l_requestData.setParentId(p_parentId);
		checkToken();
		HTTPStatus l_status = l_parentHandler.createObject(m_configData.getRestHost(), m_token.getToken(), l_requestData);
		log.info("Status: " + l_status);
		return l_status.getStatus();
	}

	public List<UserProxy> getUsers(boolean p_includeUnavailable) {
		log.trace("In getUsers()");
		ArrayList<UserProxy> l_userList = new ArrayList<UserProxy>();
		UserHandler l_userHandler = new UserHandler();
		RequestData l_requestData = new RequestData();
		checkToken();
		UserResponseProxy l_userResponse = l_userHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
				null, l_requestData); // Get
		if (l_userResponse != null) {
			UserListProxy l_list = l_userResponse.getResults();
			PagingProxy l_page = l_userResponse.getPaging();
			while (l_page != null) {
				for (UserProxy l_user : l_list) {
					log.info("User: " + l_user.getUserName());
					if (!m_userCache.containsKey(l_user.getId())) {
						// Add into Cache
						m_userCache.put(l_user.getId(), l_user);
					}
					if (p_includeUnavailable) {
						l_userList.add(l_user);
					} else if (l_user.getAvailablity() != null
							&& !l_user.getAvailablity().getAvailable().equalsIgnoreCase("No")) {
						l_userList.add(l_user);
					}
				}
				// Now Iterate to next Page
				checkToken();
				l_userResponse = l_userHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_list = l_userResponse.getResults();
				l_page = l_userResponse.getPaging();

			}

			// Process Last Page
			for (UserProxy l_user : l_list) {
				log.info("User: " + l_user.getUserName());
				if (!m_userCache.containsKey(l_user.getId())) {
					// Add into Cache
					m_userCache.put(l_user.getId(), l_user);
				}
				if (p_includeUnavailable) {
					l_userList.add(l_user);
				} else if (l_user.getAvailablity() != null
						&& !l_user.getAvailablity().getAvailable().equalsIgnoreCase("No")) {
					l_userList.add(l_user);
				}
			}
		}
		return l_userList;
	}

	public List<UserProxy> getUsersByFamilyName(String p_studentName, boolean p_includeUnavailable) {
		log.trace("In getUsersByFamilyName()");
		List<UserProxy> l_userList = new ArrayList<UserProxy>();
		UserHandler l_userHandler = new UserHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setFamilyName(p_studentName);
		checkToken();
		UserResponseProxy l_userResponse = l_userHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
				null, l_requestData); // Get
		if (l_userResponse != null) {
			UserListProxy l_list = l_userResponse.getResults();
			PagingProxy l_page = l_userResponse.getPaging();
			while (l_page != null) {
				for (UserProxy l_user : l_list) {

					if (!m_userCache.containsKey(l_user.getId())) {
						// Add into Cache
						m_userCache.put(l_user.getId(), l_user);
					}
					if (p_includeUnavailable) {
						l_userList.add(l_user);
					} else if (l_user.getAvailablity() != null
							&& !l_user.getAvailablity().getAvailable().equalsIgnoreCase("No")) {
						l_userList.add(l_user);
					}
				}
				// Now Iterate to next Page
				checkToken();
				l_userResponse = l_userHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_list = l_userResponse.getResults();
				l_page = l_userResponse.getPaging();

			}

			// Process Last Page
			for (UserProxy l_user : l_list) {
				if (!m_userCache.containsKey(l_user.getId())) {
					// Add into Cache
					m_userCache.put(l_user.getId(), l_user);
				}
				if (p_includeUnavailable) {
					l_userList.add(l_user);
				} else if (l_user.getAvailablity() != null
						&& !l_user.getAvailablity().getAvailable().equalsIgnoreCase("No")) {
					l_userList.add(l_user);
				}
			}
		}
		return l_userList;
	}

	public List<UserProxy> getUsersByDatasource(String p_datasource, boolean p_includeUnavailable) {
		log.trace("In getUsersByDatasource()");
		List<UserProxy> l_userList = new ArrayList<UserProxy>();
		UserHandler l_userHandler = new UserHandler();
		RequestData l_requestData = new RequestData();
		DatasourceProxy l_datasource = this.getDataSourceByName(p_datasource);
		if (l_datasource != null) {
			l_requestData.setDatasourceId(l_datasource.getId());
			checkToken();
			UserResponseProxy l_userResponse = l_userHandler.getClientData(m_configData.getRestHost(),
					m_token.getToken(), null, l_requestData); // Get
			if (l_userResponse != null) {
				UserListProxy l_list = l_userResponse.getResults();
				PagingProxy l_page = l_userResponse.getPaging();
				while (l_page != null) {
					for (UserProxy l_user : l_list) {

						if (!m_userCache.containsKey(l_user.getId())) {
							// Add into Cache
							m_userCache.put(l_user.getId(), l_user);
						}
						if (p_includeUnavailable) {
							l_userList.add(l_user);
						} else if (l_user.getAvailablity() != null
								&& !l_user.getAvailablity().getAvailable().equalsIgnoreCase("No")) {
							l_userList.add(l_user);
						}
					}
					// Now Iterate to next Page
					checkToken();
					l_userResponse = l_userHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
							l_page.getNextPage(), null);
					l_list = l_userResponse.getResults();
					l_page = l_userResponse.getPaging();

				}

				// Process Last Page
				for (UserProxy l_user : l_list) {
					if (!m_userCache.containsKey(l_user.getId())) {
						// Add into Cache
						m_userCache.put(l_user.getId(), l_user);
					}
					if (p_includeUnavailable) {
						l_userList.add(l_user);
					} else if (l_user.getAvailablity() != null
							&& !l_user.getAvailablity().getAvailable().equalsIgnoreCase("No")) {
						l_userList.add(l_user);
					}
				}
			}
		}
		return l_userList;
	}

	public String getTeacherEmail(String p_courseId) {
		log.trace("In getTeacherEmail()");
		List<StudentData> l_instructors = this.getMembershipInstructors(p_courseId, false);
		String l_instructorEmail = null;
		if (l_instructors != null && l_instructors.size() > 0) {
			l_instructorEmail = "mailto:" + l_instructors.get(0).getEmail();
		}
		return l_instructorEmail;
	}

	public List<MembershipProxy> getMembershipCourse(String p_courseId, String p_userId) {
		log.trace("In getMembership()");
		RequestData l_requestData = new RequestData();
		l_requestData.setCourseId(p_courseId);
		if (p_userId != null) {
			l_requestData.setUserId(p_userId);
		}
		MembershipHandler l_membershipHandler = new MembershipHandler();
		ArrayList<MembershipProxy> l_membershipList = new ArrayList<MembershipProxy>();
		checkToken();
		MembershipResponseProxy l_membershipResponse = l_membershipHandler.getClientData(m_configData.getRestHost(),
				m_token.getToken(), null, l_requestData);
		if (l_membershipResponse != null) {
			MembershipListProxy l_memberships = l_membershipResponse.getResults();
			PagingProxy l_page = l_membershipResponse.getPaging();
			while (l_page != null) {
				for (MembershipProxy l_membership : l_memberships) {
					if (l_membership.getAvailablity() != null
							&& !l_membership.getAvailablity().getAvailable().equalsIgnoreCase("Disabled")) {
						l_membershipList.add(l_membership);
					}
				}
				// Now Iterate to next Page
				checkToken();
				l_membershipResponse = l_membershipHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_memberships = l_membershipResponse.getResults();
				l_page = l_membershipResponse.getPaging();

			}

			for (MembershipProxy l_membership : l_memberships) {
				if (l_membership.getAvailablity() != null
						&& !l_membership.getAvailablity().getAvailable().equalsIgnoreCase("Disabled")) {
					l_membershipList.add(l_membership);
				}
			}
		}
		return l_membershipList;
	}

	public void createMembership (String p_courseId, String p_username, String p_type) {
		log.info("In createMembership() ...");
		RequestData l_requestData = new RequestData();
		l_requestData.setCourseName(p_courseId);
		l_requestData.setUserName(p_username);
		EnrollmentOptionProxy l_enrollmentOption = new EnrollmentOptionProxy();
		l_enrollmentOption.setCourseRoleId(p_type);
		l_enrollmentOption.setDataSourceId("externalId:SIS2.Enrollment");
		Available l_available = new Available();
		l_available.setAvailable("Yes");
		l_enrollmentOption.setavailabilityd(l_available);

		MembershipHandler l_membershipHandler = new MembershipHandler();
		HTTPStatus l_response =l_membershipHandler.createObject(m_configData.getRestHost(), m_token.getToken(),l_requestData, l_enrollmentOption);

		//  If Enrollment Already Exists .. just Set Available
		if (l_response.getStatus() == 409) {
			l_membershipHandler.deleteObject(m_configData.getRestHost(), m_token.getToken(), l_requestData, l_enrollmentOption);
		}
	}

	public void removeMembership (String p_courseId, String p_username) {
		log.info("In removeMembership() ...");
		RequestData l_requestData = new RequestData();
		l_requestData.setCourseName(p_courseId);
		l_requestData.setUserName(p_username);
		EnrollmentOptionProxy l_enrollmentOption = new EnrollmentOptionProxy();
		Available l_available = new Available();
		l_available.setAvailable("No");
		l_enrollmentOption.setavailabilityd(l_available);

		checkToken();
		MembershipHandler l_membershipHandler = new MembershipHandler();
		HTTPStatus l_status = l_membershipHandler.deleteObject(m_configData.getRestHost(), m_token.getToken(),l_requestData, l_enrollmentOption);

	}

	public List<String> getMembershipUsers(String p_courseId, boolean p_includeUnavailble) {
		log.trace("In getMembershipUsers()");
		RequestData l_requestData = new RequestData();
		l_requestData.setCourseId(p_courseId);
		l_requestData.setCourseRole("Student");
		MembershipHandler l_membershipHandler = new MembershipHandler();
		ArrayList<String> l_membershipList = new ArrayList<String>();
		checkToken();
		MembershipResponseProxy l_membershipResponse = l_membershipHandler.getClientData(m_configData.getRestHost(),
				m_token.getToken(), null, l_requestData);
		if (l_membershipResponse != null) {
			MembershipListProxy l_memberships = l_membershipResponse.getResults();
			PagingProxy l_page = l_membershipResponse.getPaging();
			while (l_page != null) {
				for (MembershipProxy l_membership : l_memberships) {
					if (l_membership.getAvailablity() != null
							&& !l_membership.getAvailablity().getAvailable().equalsIgnoreCase("Disabled")) {
						l_membershipList.add(l_membership.getUserId());
					}
				}
				// Now Iterate to next Page
				checkToken();
				l_membershipResponse = l_membershipHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_memberships = l_membershipResponse.getResults();
				l_page = l_membershipResponse.getPaging();

			}

			for (MembershipProxy l_membership : l_memberships) {
				if (l_membership.getAvailablity() != null
						&& !l_membership.getAvailablity().getAvailable().equalsIgnoreCase("Disabled")) {
					l_membershipList.add(l_membership.getUserId());
				}
			}
		}
		return l_membershipList;
	}

	public List<StudentData> getMembershipStudents(String p_courseId, boolean p_includeUnavailable) {
		log.trace("In getMembershipStudents()");
		ArrayList<StudentData> l_membershipList = new ArrayList<StudentData>();
		if (m_studentCache.containsKey(p_courseId)) {
			log.debug(" STUDENT CACHE HIT ...");
			return m_studentCache.get(p_courseId);

		} else {
			RequestData l_requestData = new RequestData();
			l_requestData.setCourseId(p_courseId);
			l_requestData.setCourseRole("Student");
			MembershipHandler l_membershipHandler = new MembershipHandler();

			checkToken();
			MembershipResponseProxy l_membershipResponse = l_membershipHandler.getClientData(m_configData.getRestHost(),
					m_token.getToken(), null, l_requestData);
			if (l_membershipResponse != null) {
				MembershipListProxy l_memberships = l_membershipResponse.getResults();
				PagingProxy l_page = l_membershipResponse.getPaging();
				while (l_page != null) {
					for (MembershipProxy l_membership : l_memberships) {
						if (l_membership.getAvailablity() != null
								&& !l_membership.getAvailablity().getAvailable().equalsIgnoreCase("Disabled")) {
							UserProxy l_user = this.getUserId(l_membership.getUserId());
							StudentData l_student = new StudentData(l_user);
							if (p_includeUnavailable) {
								l_membershipList.add(l_student);
							} else if (l_user.getAvailablity() != null
									&& l_user.getAvailablity().getAvailable().equalsIgnoreCase("Yes")) {
								l_membershipList.add(l_student);
							}
						}
					}
					// Now Iterate to next Page
					checkToken();
					l_membershipResponse = l_membershipHandler.getClientData(m_configData.getRestHost(),
							m_token.getToken(), l_page.getNextPage(), null);
					l_memberships = l_membershipResponse.getResults();
					l_page = l_membershipResponse.getPaging();

				}

				for (MembershipProxy l_membership : l_memberships) {
					if (l_membership.getAvailablity() != null
							&& !l_membership.getAvailablity().getAvailable().equalsIgnoreCase("Disabled")) {
						UserProxy l_user = this.getUserId(l_membership.getUserId());
						StudentData l_student = new StudentData(l_user);
						if (p_includeUnavailable) {
							l_membershipList.add(l_student);
						} else if (l_user.getAvailablity() != null
								&& l_user.getAvailablity().getAvailable().equalsIgnoreCase("Yes")) {
							l_membershipList.add(l_student);
						}
					}
				}
				if (!m_studentCache.containsKey(p_courseId)) {
					// Add into Cache
					log.debug("ADDING STUDENT CACHE");
					m_studentCache.put(p_courseId, l_membershipList);
				}
			}
		}
		return l_membershipList;
	}

	public List<StudentData> getMembershipInstructors(String p_courseName, boolean p_includeUnavailable) {
		log.trace("In getMembershipInstructors()");
		ArrayList<StudentData> l_membershipList = new ArrayList<StudentData>();

		CourseProxy l_course = this.getCourseByName(p_courseName);

		if (l_course != null) {

			if (m_instructorCache.containsKey(l_course.getId())) {
				log.debug(" INSTRUCTOR CACHE HIT ...");
				return m_instructorCache.get(l_course.getId());

			} else {
				RequestData l_requestData = new RequestData();
				// l_requestData.setCourseId(p_courseId);
				l_requestData.setCourseId(l_course.getId());
				l_requestData.setCourseRole("Instructor");
				MembershipHandler l_membershipHandler = new MembershipHandler();

				checkToken();
				MembershipResponseProxy l_membershipResponse = l_membershipHandler
						.getClientData(m_configData.getRestHost(), m_token.getToken(), null, l_requestData);
				if (l_membershipResponse != null) {
					MembershipListProxy l_memberships = l_membershipResponse.getResults();
					PagingProxy l_page = l_membershipResponse.getPaging();
					while (l_page != null) {
						for (MembershipProxy l_membership : l_memberships) {
							if (l_membership.getAvailablity() != null
									&& !l_membership.getAvailablity().getAvailable().equalsIgnoreCase("Disabled")) {
								UserProxy l_user = this.getUserId(l_membership.getUserId());
								StudentData l_student = new StudentData(l_user);
								if (p_includeUnavailable) {
									l_membershipList.add(l_student);
								} else if (l_user.getAvailablity() != null
										&& l_user.getAvailablity().getAvailable().equalsIgnoreCase("Yes")) {
									l_membershipList.add(l_student);
								}
							}
						}
						// Now Iterate to next Page
						checkToken();
						l_membershipResponse = l_membershipHandler.getClientData(m_configData.getRestHost(),
								m_token.getToken(), l_page.getNextPage(), null);
						l_memberships = l_membershipResponse.getResults();
						l_page = l_membershipResponse.getPaging();

					}

					for (MembershipProxy l_membership : l_memberships) {
						if (l_membership.getAvailablity() != null
								&& !l_membership.getAvailablity().getAvailable().equalsIgnoreCase("Disabled")) {
							UserProxy l_user = this.getUserId(l_membership.getUserId());
							StudentData l_student = new StudentData(l_user);
							if (p_includeUnavailable) {
								l_membershipList.add(l_student);
							} else if (l_user.getAvailablity() != null
									&& l_user.getAvailablity().getAvailable().equalsIgnoreCase("Yes")) {
								l_membershipList.add(l_student);
							}
						}
					}
					if (!m_instructorCache.containsKey(l_course.getId())) {
						// Add into Cache
						log.debug("ADDING INSTRUCTOR CACHE");
						m_instructorCache.put(l_course.getId(), l_membershipList);
					}
				}
			}
		}
		return l_membershipList;
	}

	public List<StudentData> getMembershipStudents(String p_userId, boolean p_includeUnavailable, String p_role,
			String[] p_lmsTerms) {
		log.trace("In getMembershipStudents()");

		// Get the TermIds
		List<String> l_termIds = null;
		if (p_lmsTerms != null) {
			l_termIds = getTermIds(p_lmsTerms);
		}
		RequestData l_requestData = new RequestData();
		l_requestData.setUserId(p_userId);
		if (p_role != null) {
			l_requestData.setCourseRole(p_role);
		}
		MembershipHandler l_membershipHandler = new MembershipHandler();
		HashMap<String, StudentData> l_studentList = new HashMap<String, StudentData>();
		checkToken();
		MembershipResponseProxy l_membershipResponse = l_membershipHandler.getClientData(m_configData.getRestHost(),
				m_token.getToken(), null, l_requestData);
		if (l_membershipResponse != null) {
			MembershipListProxy l_memberships = l_membershipResponse.getResults();
			PagingProxy l_page = l_membershipResponse.getPaging();
			while (l_page != null) {
				for (MembershipProxy l_membership : l_memberships) {
					if (l_membership.getAvailablity() != null
							&& !l_membership.getAvailablity().getAvailable().equalsIgnoreCase("Disabled")) {
						CourseProxy l_course = this.getCourse(l_membership.getCourseId());
						if (l_course != null) {
							if (l_termIds == null
									|| (l_course.getTermId() != null && checkTerm(l_course.getTermId(), l_termIds))) {
								List<StudentData> l_students = getMembershipStudents(l_course.getId(),
										p_includeUnavailable);
								for (StudentData l_student : l_students) {
									l_studentList.put(l_student.getId(), l_student);
								}
							}
						}
					}
				}
				// Now Iterate to next Page
				checkToken();
				l_membershipResponse = l_membershipHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_memberships = l_membershipResponse.getResults();
				l_page = l_membershipResponse.getPaging();

			}
			for (MembershipProxy l_membership : l_memberships) {
				if (l_membership.getAvailablity() != null
						&& !l_membership.getAvailablity().getAvailable().equalsIgnoreCase("Disabled")) {
					CourseProxy l_course = this.getCourse(l_membership.getCourseId());
					if (l_course != null) {
						if (l_termIds == null
								|| (l_course.getTermId() != null && checkTerm(l_course.getTermId(), l_termIds))) {
							List<StudentData> l_students = getMembershipStudents(l_course.getId(),
									p_includeUnavailable);
							for (StudentData l_student : l_students) {
								l_studentList.put(l_student.getId(), l_student);
							}
						}
					}
				}
			}
		}
		Collection<StudentData> l_values = l_studentList.values();
		ArrayList<StudentData> l_newList = new ArrayList<>(l_values);

		sort(l_newList);
		return l_newList;
	}

	private void sort(ArrayList<StudentData> list) {

		list.sort(new Comparator<StudentData>() {
			@Override
			public int compare(StudentData o1, StudentData o2) {
				return o1.getLastName().compareTo(o2.getLastName());
			}
		});
	}

	public List<MembershipProxy> getMembershipUser(String p_userId, String p_role) {
		log.trace("In getMembershipUser()");
		RequestData l_requestData = new RequestData();
		l_requestData.setUserId(p_userId);
		if (p_role != null) {
			l_requestData.setCourseRole(p_role);
		}
		MembershipHandler l_membershipHandler = new MembershipHandler();
		ArrayList<MembershipProxy> l_membershipList = new ArrayList<MembershipProxy>();
		checkToken();
		MembershipResponseProxy l_membershipResponse = l_membershipHandler.getClientData(m_configData.getRestHost(),
				m_token.getToken(), null, l_requestData);
		if (l_membershipResponse != null) {
			MembershipListProxy l_memberships = l_membershipResponse.getResults();
			PagingProxy l_page = l_membershipResponse.getPaging();
			while (l_page != null) {
				for (MembershipProxy l_membership : l_memberships) {
					if (l_membership.getAvailablity() != null
							&& !l_membership.getAvailablity().getAvailable().equalsIgnoreCase("Disabled")) {
						l_membershipList.add(l_membership);
					}
				}
				// Now Iterate to next Page
				checkToken();
				l_membershipResponse = l_membershipHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_memberships = l_membershipResponse.getResults();
				l_page = l_membershipResponse.getPaging();

			}
			for (MembershipProxy l_membership : l_memberships) {
				if (l_membership.getAvailablity() != null
						&& !l_membership.getAvailablity().getAvailable().equalsIgnoreCase("Disabled")) {
					l_membershipList.add(l_membership);
				}
			}
		}

		return l_membershipList;
	}

	public CategoryProxy getCategory(String p_categoryId) {
		log.trace("In getCategory()");
		CategoryProxy l_category = null;

		if (m_categoryCache != null && m_categoryCache.size() > 0) {
			for (CategoryProxy l_list : m_categoryCache) {
				if (l_list.getId().equalsIgnoreCase(p_categoryId)) {
					return l_list;
				}
			}
		} else {
			CategoryHandler l_categoryHandler = new CategoryHandler();
			RequestData l_requestData = new RequestData();
			l_requestData.setCategoryId(p_categoryId);
			l_requestData.setCategoryType("courses");
			checkToken();
			l_category = l_categoryHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null,
					l_requestData);
		}
		return l_category;
	}

	public List<String> getCategories(String p_categoryName, boolean p_includeUnavailable) {
		log.trace("In getCategories()");
		ArrayList<String> l_categoryList = new ArrayList<String>();

		List<LocationInfo> l_locations = getLocations();

		for (LocationInfo l_location : l_locations) {
			if (l_location.getLocation().equalsIgnoreCase(p_categoryName)) {
				if (p_includeUnavailable) {
					l_categoryList.add(l_location.getId());
				} else if (l_location.isAvailable()) {
					l_categoryList.add(l_location.getId());
				}
			}
		}

		return l_categoryList;
	}

	public List<LocationInfo> getLocations() {
		log.trace("In getLocations()");
		ArrayList<LocationInfo> l_categoryList = new ArrayList<LocationInfo>();
		CategoryHandler l_categoryHandler = new CategoryHandler();
		if (m_categoryCache != null && m_categoryCache.size() > 0) {
			for (CategoryProxy l_category : m_categoryCache) {
				l_categoryList
				.add(new LocationInfo(l_category.getId(), l_category.getTitle(), l_category.isAvailable()));
			}
		} else {
			RequestData l_requestData = new RequestData();
			checkToken();
			CategoryResponseProxy l_categoryResponse = l_categoryHandler.getClientData(m_configData.getRestHost(),
					m_token.getToken(), null, l_requestData);
			if (l_categoryResponse != null) {
				CategoryListProxy l_list = l_categoryResponse.getResults();
				PagingProxy l_page = l_categoryResponse.getPaging();
				while (l_page != null) {
					if (m_categoryCache != null) {
						m_categoryCache.addAll(l_list);
					}
					for (CategoryProxy l_category : l_list) {
						l_categoryList.add(
								new LocationInfo(l_category.getId(), l_category.getTitle(), l_category.isAvailable()));
					}
					// Now Iterate to next Page
					checkToken();
					l_categoryResponse = l_categoryHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
							l_page.getNextPage(), null);
					l_list = l_categoryResponse.getResults();
					l_page = l_categoryResponse.getPaging();

				}
				if (m_categoryCache != null) {
					m_categoryCache.addAll(l_list);
				}
				// Process Last Page
				for (CategoryProxy l_category : l_list) {
					l_categoryList
					.add(new LocationInfo(l_category.getId(), l_category.getTitle(), l_category.isAvailable()));
				}
			}
		}
		return l_categoryList;
	}

	public List<String> getCategoryCourses(String p_categoryId, String[] p_terms) {
		log.trace("In getCategoryCourses()");
		ArrayList<String> l_courseList = new ArrayList<String>();
		CategoryHandler l_categoryHandler = new CategoryHandler();

		RequestData l_request = new RequestData();
		l_request.setCategoryId(p_categoryId);
		l_request.setCategoryType("courses");

		// Get the TermIds
		List<String> l_termIds = null;
		if (p_terms != null) {
			l_termIds = getTermIds(p_terms);
		}

		checkToken();
		CategoryResponseProxy l_categoryResponse = l_categoryHandler.getClientData(m_configData.getRestHost(),
				m_token.getToken(), null, l_request);
		if (l_categoryResponse != null) {
			CategoryListProxy l_list = l_categoryResponse.getResults();
			PagingProxy l_page = l_categoryResponse.getPaging();
			while (l_page != null) {
				for (CategoryProxy l_category : l_list) {
					CourseProxy l_course = getCourse(l_category.getCourseId());
					if (l_termIds != null) {
						for (String l_termId : l_termIds) {
							if (l_termId.equalsIgnoreCase(l_course.getTermId())) {
								l_courseList.add(l_course.getId());
							}
						}
					} else {
						l_courseList.add(l_course.getId());
					}

				}
				// Now Iterate to next Page
				checkToken();
				l_categoryResponse = l_categoryHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_list = l_categoryResponse.getResults();
				l_page = l_categoryResponse.getPaging();

			}

			// Process Last Page
			for (CategoryProxy l_category : l_list) {
				CourseProxy l_course = getCourse(l_category.getCourseId());
				if (l_termIds != null) {
					for (String l_termId : l_termIds) {
						if (l_termId.equalsIgnoreCase(l_course.getTermId())) {
							l_courseList.add(l_course.getId());
						}
					}
				} else {
					l_courseList.add(l_course.getId());
				}
			}
		}
		return l_courseList;
	}

	public List<StudentData> getCategoryCoursesStudents(String p_categoryId, boolean p_includeUnavailable,
			String[] p_terms) {
		log.trace("In getCategoryCoursesStudents()");
		HashMap<String, StudentData> l_studentList = new HashMap<String, StudentData>();
		CategoryHandler l_categoryHandler = new CategoryHandler();

		RequestData l_request = new RequestData();
		l_request.setCategoryId(p_categoryId);
		l_request.setCategoryType("courses");

		// Get the TermIds
		List<String> l_termIds = null;
		if (p_terms != null) {
			l_termIds = getTermIds(p_terms);
		}

		checkToken();
		CategoryResponseProxy l_categoryResponse = l_categoryHandler.getClientData(m_configData.getRestHost(),
				m_token.getToken(), null, l_request);
		if (l_categoryResponse != null) {
			CategoryListProxy l_list = l_categoryResponse.getResults();
			PagingProxy l_page = l_categoryResponse.getPaging();
			while (l_page != null) {
				for (CategoryProxy l_category : l_list) {
					CourseProxy l_course = this.getCourse(l_category.getCourseId());
					log.info("Processing Course: " + l_category.getCourseId());
					if (l_course != null) {
						if (l_termIds == null
								|| (l_course.getTermId() != null && checkTerm(l_course.getTermId(), l_termIds))) {
							// Get the Membership for this course
							List<StudentData> l_students = getMembershipStudents(l_category.getCourseId(),
									p_includeUnavailable);
							for (StudentData l_student : l_students) {
								l_studentList.put(l_student.getId(), l_student);
							}
						}
					}
				}
				// Now Iterate to next Page
				checkToken();
				l_categoryResponse = l_categoryHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_list = l_categoryResponse.getResults();
				l_page = l_categoryResponse.getPaging();

			}

			// Process Last Page
			for (CategoryProxy l_category : l_list) {
				CourseProxy l_course = this.getCourse(l_category.getCourseId());
				if (l_course != null) {
					if (l_termIds == null
							|| (l_course.getTermId() != null && checkTerm(l_course.getTermId(), l_termIds))) {
						// Get the Membership for this course
						List<StudentData> l_students = getMembershipStudents(l_category.getCourseId(),
								p_includeUnavailable);
						for (StudentData l_student : l_students) {
							l_studentList.put(l_student.getId(), l_student);
						}
					}
				}
			}
		}
		Collection<StudentData> l_values = l_studentList.values();
		ArrayList<StudentData> l_newList = new ArrayList<>(l_values);

		sort(l_newList);
		return l_newList;
	}

	public CourseProxy getCourse(String p_courseId) {
		log.trace("In getCourse()");
		CourseProxy l_course = null;
		if (m_courseCache.containsKey(p_courseId)) {
			log.debug(" COURSE CACHE HIT ...");
			return m_courseCache.get(p_courseId);
		} else {
			CourseHandler l_courseHandler = new CourseHandler();
			RequestData l_requestData = new RequestData();
			l_requestData.setCourseId(p_courseId);
			checkToken();
			l_course = l_courseHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null,
					l_requestData);
			if (l_course != null) {
				m_courseCache.put(p_courseId, l_course);
			}
		}
		return l_course;
	}

	public CourseProxy getCourseByName(String p_courseName) {
		log.trace("In getCourseByName()");
		CourseProxy l_course = null;

		CourseHandler l_courseHandler = new CourseHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setCourseName(p_courseName);
		checkToken();
		l_course = l_courseHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_requestData);
		if (l_course != null) {
			m_courseCache.put(l_course.getId(), l_course);
		}
		return l_course;
	}

	public List<TemplateProxy> getTemplates() {
		log.trace("In getTemplates()");

		TemplateResponseProxy l_response = null;
		List<TemplateProxy> l_templateList = new ArrayList<TemplateProxy>();

		TemplateHandler l_templateHandler = new TemplateHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setTemplateId("_Master");
		checkToken();
		l_response = l_templateHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), null, l_requestData);
		if (l_response != null) {
			TemplateListProxy l_list = l_response.getResults();
			PagingProxy l_page = l_response.getPaging();
			while (l_page != null) {
				for (CourseProxy l_course : l_list) {
					log.info("Processing Template: " + l_course.getName());
					//  Now Add in the Categories
					TemplateProxy l_template = new TemplateProxy(l_course);
					List<String> l_categories = new ArrayList<String>();
					l_categories.add("Math");
					l_categories.add("History");

					l_template.setCategories(l_categories);
					l_templateList.add(l_template);
				}
				// Now Iterate to next Page
				checkToken();
				l_response = l_templateHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_list = l_response.getResults();
				l_page = l_response.getPaging();

			}

			// Process Last Page
			for (CourseProxy l_course : l_list) {
				log.info("Processing Template: " + l_course.getName());
				//  Now Add in the Categories
				TemplateProxy l_template = new TemplateProxy(l_course);
				List<String> l_categories = new ArrayList<String>();
				l_categories.add("Math");
				l_categories.add("History");

				l_template.setCategories(l_categories);
				l_templateList.add(l_template);
			}
		}
		return l_templateList;
	}

	public CourseProxy getCourseByUUID(String p_courseUUID) {
		log.trace("In getCourseByUUID()");
		CourseProxy l_course = null;

		CourseHandler l_courseHandler = new CourseHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setUuid(p_courseUUID);
		checkToken();
		l_course = l_courseHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_requestData);
		if (l_course != null) {
			m_courseCache.put(l_course.getId(), l_course);
		}
		return l_course;
	}

	public void deleteCourse(String p_courseId) {
		log.trace("In deleteCourse()");

		CourseHandler l_courseHandler = new CourseHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setCourseId(p_courseId);
		checkToken();
		HTTPStatus l_status = l_courseHandler.deleteObject(m_configData.getRestHost(), m_token.getToken(), l_requestData);
		log.info("Status: " + l_status.getStatus());

		return;
	}

	public List<String> getCourses(String p_termId, boolean p_includeUnavailable) {
		log.trace("In getCourses()");
		ArrayList<String> l_courseList = new ArrayList<String>();
		CourseHandler l_courseHandler = new CourseHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setTermId(p_termId);
		checkToken();
		CourseResponseProxy l_courseResponse = l_courseHandler.getClientData(m_configData.getRestHost(),
				m_token.getToken(), null, l_requestData); // Get
		if (l_courseResponse != null) {
			CourseListProxy l_list = l_courseResponse.getResults();
			PagingProxy l_page = l_courseResponse.getPaging();
			while (l_page != null) {
				for (CourseProxy l_course : l_list) {
					if (l_course.getTermId() != null && l_course.getTermId().equalsIgnoreCase(p_termId)) {
						if (!m_courseCache.containsKey(l_course.getId())) {
							// Add into Cache
							m_courseCache.put(l_course.getId(), l_course);
						}
						if (p_includeUnavailable) {
							l_courseList.add(l_course.getId());
						} else if (l_course.getAvailability() != null
								&& !l_course.getAvailability().getAvailable().equalsIgnoreCase("No")) {
							l_courseList.add(l_course.getId());
						}
					}
				}
				// Now Iterate to next Page
				checkToken();
				l_courseResponse = l_courseHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_list = l_courseResponse.getResults();
				l_page = l_courseResponse.getPaging();

			}

			// Process Last Page
			for (CourseProxy l_course : l_list) {
				if (l_course.getTermId().equalsIgnoreCase(p_termId)) {
					if (!m_courseCache.containsKey(l_course.getId())) {
						// Add into Cache
						m_courseCache.put(l_course.getId(), l_course);
					}
					if (p_includeUnavailable) {
						l_courseList.add(l_course.getId());
					} else if (l_course.getAvailability() != null
							&& !l_course.getAvailability().getAvailable().equalsIgnoreCase("No")) {
						l_courseList.add(l_course.getId());
					}
				}
			}
		}
		return l_courseList;
	}

	public List<String> getCoursesByName(String p_className, String p_termId, boolean p_includeUnavailable) {
		log.trace("In getCoursesByName()");
		ArrayList<String> l_courseList = new ArrayList<String>();
		CourseHandler l_courseHandler = new CourseHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setTermId(p_termId);
		checkToken();
		CourseResponseProxy l_courseResponse = l_courseHandler.getClientData(m_configData.getRestHost(),
				m_token.getToken(), null, l_requestData); // Get
		if (l_courseResponse != null) {
			CourseListProxy l_list = l_courseResponse.getResults();
			PagingProxy l_page = l_courseResponse.getPaging();
			while (l_page != null) {
				for (CourseProxy l_course : l_list) {
					if (l_course.getTermId().equalsIgnoreCase(p_termId)) {
						if (!m_courseCache.containsKey(l_course.getId())) {
							// Add into Cache
							m_courseCache.put(l_course.getId(), l_course);
						}
						if (p_includeUnavailable
								&& l_course.getName().toLowerCase().contains(p_className.toLowerCase())) {
							l_courseList.add(l_course.getId());
						} else if (l_course.getAvailability() != null
								&& !l_course.getAvailability().getAvailable().equalsIgnoreCase("No")
								&& l_course.getName().toLowerCase().contains(p_className.toLowerCase())) {
							l_courseList.add(l_course.getId());
						}
					}
				}
				// Now Iterate to next Page
				checkToken();
				l_courseResponse = l_courseHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_list = l_courseResponse.getResults();
				l_page = l_courseResponse.getPaging();

			}

			// Process Last Page
			for (CourseProxy l_course : l_list) {
				if (l_course.getTermId().equalsIgnoreCase(p_termId)) {
					if (!m_courseCache.containsKey(l_course.getId())) {
						// Add into Cache
						m_courseCache.put(l_course.getId(), l_course);
					}
					if (p_includeUnavailable && l_course.getName().toLowerCase().contains(p_className.toLowerCase())) {
						l_courseList.add(l_course.getId());
					} else if (l_course.getAvailability() != null
							&& !l_course.getAvailability().getAvailable().equalsIgnoreCase("No")
							&& l_course.getName().toLowerCase().contains(p_className.toLowerCase())) {
						l_courseList.add(l_course.getId());
					}
				}
			}
		}
		return l_courseList;
	}

	public List<String> getCoursesByDate(String p_date, boolean p_includeUnavailable) {
		log.trace("In getCoursesByDate()");
		ArrayList<String> l_courseList = new ArrayList<String>();
		CourseHandler l_courseHandler = new CourseHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setDate(p_date);
		checkToken();
		CourseResponseProxy l_courseResponse = l_courseHandler.getClientData(m_configData.getRestHost(),
				m_token.getToken(), null, l_requestData); // Get
		if (l_courseResponse != null) {
			CourseListProxy l_list = l_courseResponse.getResults();
			PagingProxy l_page = l_courseResponse.getPaging();
			while (l_page != null) {
				for (CourseProxy l_course : l_list) {


					if (p_includeUnavailable) {
						l_courseList.add(l_course.getId());
					} else if (l_course.getAvailability() != null
							&& !l_course.getAvailability().getAvailable().equalsIgnoreCase("No")) {
						l_courseList.add(l_course.getId());
					}
				}
				// Now Iterate to next Page
				checkToken();
				l_courseResponse = l_courseHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_list = l_courseResponse.getResults();
				l_page = l_courseResponse.getPaging();

			}

			// Process Last Page
			for (CourseProxy l_course : l_list) {

				if (p_includeUnavailable) {
					l_courseList.add(l_course.getId());
				} else if (l_course.getAvailability() != null
						&& !l_course.getAvailability().getAvailable().equalsIgnoreCase("No")) {
					l_courseList.add(l_course.getId());
				}
			}
		}
		return l_courseList;
	}

	public ColumnProxy getColumn(String p_courseId, String p_columnId) {
		log.trace("In getColumn()");
		ColumnProxy l_column = null;
		if (m_columnCache.containsKey(p_columnId)) {
			log.debug(" COLUMN CACHE HIT ...");
			return m_columnCache.get(p_columnId);
		} else {
			ColumnHandler l_columnHandler = new ColumnHandler();
			RequestData l_data = new RequestData();
			l_data.setCourseId(p_courseId);
			l_data.setColumnId(p_columnId);
			checkToken();
			l_column = l_columnHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_data);
			if (l_column != null) {
				m_columnCache.put(p_columnId, l_column);
			}
		}
		return l_column;
	}

	public ColumnProxy getColumnNoCache(String p_courseId, String p_columnId) {
		log.trace("In getColumnNoCache()");
		ColumnProxy l_column = null;

		ColumnHandler l_columnHandler = new ColumnHandler();
		RequestData l_data = new RequestData();
		l_data.setCourseId(p_courseId);
		l_data.setColumnId(p_columnId);
		checkToken();
		l_column = l_columnHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_data);

		return l_column;
	}


	public List<ColumnProxy> getColumns(String p_courseId) {
		log.trace("In getColumns()");
		if (m_courseColumnCache.containsKey(p_courseId)) {
			log.debug(" COURSE COLUMN CACHE HIT ...");
			return m_courseColumnCache.get(p_courseId);
		}
		ArrayList<ColumnProxy> l_columnList = new ArrayList<ColumnProxy>();
		ColumnHandler l_columnHandler = new ColumnHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setCourseId(p_courseId);
		ArrayList<ColumnProxy> l_cacheList = new ArrayList<ColumnProxy>();
		checkToken();
		ColumnResponseProxy l_columnResponse = l_columnHandler.getClientData(m_configData.getRestHost(),
				m_token.getToken(), null, l_requestData); // Get
		if (l_columnResponse != null) {
			ColumnListProxy l_list = l_columnResponse.getResults();
			PagingProxy l_page = l_columnResponse.getPaging();
			while (l_page != null) {
				l_cacheList.addAll(l_list);
				for (ColumnProxy l_column : l_list) {
					if (!m_columnCache.containsKey(l_column.getId())) {
						// Add into Cache
						m_columnCache.put(l_column.getId(), l_column);
					}
					l_columnList.add(l_column);
				}
				// Now Iterate to next Page
				checkToken();
				l_columnResponse = l_columnHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				if (l_columnResponse != null) {
					l_list = l_columnResponse.getResults();
					l_page = l_columnResponse.getPaging();
				} else {
					l_page = null;
				}

			}

			// Process Last Page
			for (ColumnProxy l_column : l_list) {
				if (!m_columnCache.containsKey(l_column.getId())) {
					// Add into Cache
					m_columnCache.put(l_column.getId(), l_column);
				}
				l_columnList.add(l_column);
			}
			l_cacheList.addAll(l_list);

			// Add into Cache
			if (!m_courseColumnCache.containsKey(p_courseId)) {
				// Add into Cache
				m_courseColumnCache.put(p_courseId, l_cacheList);
			}
		}
		return l_columnList;
	}

	public List<GradebookProxy> getGrades(String p_userId, String p_courseId) {
		log.trace("In getGrades()");
		GradebookResponseProxy l_gradesResponse = null;
		ArrayList<GradebookProxy> l_returnList = new ArrayList<GradebookProxy>();
		GradebookHandler l_gradebookHandler = new GradebookHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setUserId(p_userId);
		l_requestData.setCourseId(p_courseId);
		checkToken();
		l_gradesResponse = l_gradebookHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), null,
				l_requestData);
		if (l_gradesResponse != null) {
			GradebookListProxy l_list = l_gradesResponse.getResults();
			PagingProxy l_page = l_gradesResponse.getPaging();
			while (l_page != null) {

				l_returnList.addAll(l_list);
				// Now Iterate to next Page
				checkToken();
				l_gradesResponse = l_gradebookHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				if (l_gradesResponse != null) {
					l_list = l_gradesResponse.getResults();
					l_page = l_gradesResponse.getPaging();
				} else {
					l_page = null;
				}

			}

			// Process last Page
			l_returnList.addAll(l_list);

			if (!m_gradeCache.containsKey(p_userId + p_courseId)) {
				// Add into Cache
				log.debug("ADDING GRADE CACHE");
				m_gradeCache.put(p_userId + p_courseId, l_returnList);
			}
		}

		return l_returnList;
	}

	public GradebookProxy getGrade(String p_userId, String p_courseId, String p_columnId) {
		log.trace("In getGrade()");
		GradebookProxy l_gradeResponse = null;
		if (m_gradeCache.containsKey(p_userId + p_courseId)) {
			log.debug(" GRADE CACHE HIT ...");
			for (GradebookProxy l_grade : m_gradeCache.get(p_userId + p_courseId)) {
				if (l_grade.getColumnId().equalsIgnoreCase(p_columnId)) {
					return l_grade;
				}
			}
		} else {
			GradebookHandler l_gradebookHandler = new GradebookHandler();
			RequestData l_requestData = new RequestData();
			l_requestData.setUserId(p_userId);
			l_requestData.setCourseId(p_courseId);
			l_requestData.setColumnId(p_columnId);
			checkToken();
			l_gradeResponse = l_gradebookHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null,
					l_requestData);
		}

		return l_gradeResponse;
	}

	public List<AttemptProxy> getAttempts(String p_userId, String p_columnId, String p_courseId, Timestamp p_start) {
		log.trace("In getAttempts()");
		AttemptResponseProxy l_attemptsResponse = null;
		AttemptHandler l_attemptHandler = new AttemptHandler();
		RequestData l_requestData = new RequestData();
		if (m_attemptCache.containsKey(p_columnId)) {
			log.debug(" ATTEMPT CACHE HIT ...");
			return m_attemptCache.get(p_columnId);
		}
		ArrayList<AttemptProxy> l_returnList = new ArrayList<AttemptProxy>();
		l_requestData.setCourseId(p_courseId);
		l_requestData.setColumnId(p_columnId);
		if (p_start != null) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
			l_requestData.setAttemptDate(df.format(p_start));
		}
		checkToken();
		l_attemptsResponse = l_attemptHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), null,
				l_requestData);
		if (l_attemptsResponse != null) {
			AttemptListProxy l_attempts = l_attemptsResponse.getResults();
			PagingProxy l_page = l_attemptsResponse.getPaging();

			while (l_page != null) {
				l_returnList.addAll(l_attempts);

				// Now Iterate to next Page
				checkToken();
				l_attemptsResponse = l_attemptHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_attempts = l_attemptsResponse.getResults();
				l_page = l_attemptsResponse.getPaging();

			}

			// Process Last Page
			l_returnList.addAll(l_attempts);

			// Add into Cache
			if (!m_attemptCache.containsKey(p_columnId)) {
				// Add into Cache
				m_attemptCache.put(p_columnId, l_returnList);
			}
		} else {
			// Add into Cache
			if (!m_attemptCache.containsKey(p_columnId)) {
				// Add into Cache
				m_attemptCache.put(p_columnId, l_returnList);
			}
		}

		return l_returnList;
	}

	public List<AttemptProxy> getAnyAttempts(String p_userId, String p_columnId, String p_courseId, Timestamp p_start) {
		log.trace("In getAttempts()");
		AttemptResponseProxy l_attemptsResponse = null;
		AttemptHandler l_attemptHandler = new AttemptHandler();
		RequestData l_requestData = new RequestData();
		ArrayList<AttemptProxy> l_returnList = new ArrayList<AttemptProxy>();
		l_requestData.setCourseId(p_courseId);
		l_requestData.setColumnId(p_columnId);
		// l_requestData.setUserId(p_userId);
		checkToken();
		l_attemptsResponse = l_attemptHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), null,
				l_requestData);
		if (l_attemptsResponse != null) {
			AttemptListProxy l_attempts = l_attemptsResponse.getResults();
			PagingProxy l_page = l_attemptsResponse.getPaging();

			while (l_page != null) {
				l_returnList.addAll(l_attempts);

				// Now Iterate to next Page
				checkToken();
				l_attemptsResponse = l_attemptHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_attempts = l_attemptsResponse.getResults();
				l_page = l_attemptsResponse.getPaging();

			}

			// Process Last Page
			l_returnList.addAll(l_attempts);
		}

		return l_returnList;
	}

	public List<SchemaProxy> getSchemas(String p_courseId) {
		log.trace ("In getSchemas()");
		SchemaResponseProxy l_schemaResponse = null;
		SchemaHandler l_schemaHandler = new SchemaHandler();
		RequestData l_requestData = new RequestData();

		ArrayList<SchemaProxy> l_returnList = new ArrayList<SchemaProxy>();
		l_requestData.setCourseId(p_courseId);


		checkToken();
		l_schemaResponse = l_schemaHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), null,
				l_requestData);
		if (l_schemaResponse != null) {
			SchemaListProxy l_schemas = l_schemaResponse.getResults();
			PagingProxy l_page = l_schemaResponse.getPaging();

			while (l_page != null) {
				l_returnList.addAll(l_schemas);

				// Now Iterate to next Page
				checkToken();
				l_schemaResponse = l_schemaHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
						l_page.getNextPage(), null);
				l_schemas = l_schemaResponse.getResults();
				l_page = l_schemaResponse.getPaging();

			}

			// Process Last Page
			l_returnList.addAll(l_schemas);

		} 
		return l_returnList;
	}

	public int deleteSchema(String p_courseId, String p_schemaId) {
		log.trace("In deleteSchema()");

		SchemaHandler l_schemaHandler = new SchemaHandler();
		RequestData l_requestData = new RequestData();


		l_requestData.setCourseId(p_courseId);
		l_requestData.setSchemaId(p_schemaId);


		checkToken();
		HTTPStatus l_response = l_schemaHandler.deleteObject(m_configData.getRestHost(), m_token.getToken(), l_requestData);
		return l_response.getStatus();
	}

	public int updateSchema(String p_courseId, SchemaProxy p_schema) {
		log.trace("In updateSchema()");
		SchemaHandler l_schemaHandler = new SchemaHandler();
		RequestData l_requestData = new RequestData();

		l_requestData.setCourseId(p_courseId);
		l_requestData.setSchemaId(p_schema.getId());

		return (l_schemaHandler.updateObject(m_configData.getRestHost(), m_token.getToken(), l_requestData)).getStatus();

	}

	public HashMap<String,GroupProxy> createCourseGroup(String p_courseId, List<SectionInfo> p_sections) {
		log.info("In createCourseGroup()");
		HashMap<String,GroupProxy> l_list = new HashMap<String, GroupProxy>();
		GroupHandler l_groupHandler = new GroupHandler();
		RequestData l_requestData = new RequestData();

		CourseProxy l_course = this.getCourseByName(p_courseId);
		l_requestData.setCourseId(l_course.getId());

		// First Create the Group Set IC Enrollments
		GroupProxy l_groupSet = l_groupHandler.createObject(m_configData.getRestHost(), m_token.getToken(), l_requestData, "IC Enrollments");
		l_list.put(p_courseId, l_groupSet);

		// Now add the Groups to the Group Set, IC Enrollments
		if (p_sections != null) {
			for (SectionInfo l_section: p_sections) {
				GroupProxy l_group = l_groupHandler.createObject(m_configData.getRestHost(), m_token.getToken(), l_requestData, l_section, l_groupSet.getId());
				l_list.put(String.valueOf(l_section.getSectionId()), l_group);
				log.info("Group Created: " + l_group.getId());
			}
		}
		return l_list;
	}

	public HashMap<String,GroupProxy> createCourseGroup(String p_course, SectionInfo p_section, String p_groupSetId) {
		log.info("In createCourseGroup()");
		HashMap<String,GroupProxy> l_list = new HashMap<String, GroupProxy>();
		GroupHandler l_groupHandler = new GroupHandler();
		RequestData l_requestData = new RequestData();

		CourseProxy l_course = this.getCourseByName(p_course);

		if (l_course != null) {
			l_requestData.setCourseId(l_course.getId());

			l_requestData.setCourseName(p_course);
			GroupProxy l_group = l_groupHandler.createObject(m_configData.getRestHost(), m_token.getToken(), l_requestData, p_section, p_groupSetId);
			log.info("Group Created: " + l_group.getId());

			l_list.put(String.valueOf(p_section.getSectionId()), l_group);
			log.info("Group Created: " + l_group.getId());
		} else {
			return null;
		}

		return l_list;
	}

	public int deleteCourseGroup(String p_courseId, String p_sectionId) {
		log.info("In deleteCourseGroup()");
		GroupHandler l_groupHandler = new GroupHandler();

		RequestData l_requestData = new RequestData();
		l_requestData.setCourseId(p_courseId);
		l_requestData.setGroupId(p_sectionId);
		HTTPStatus l_status = l_groupHandler.deleteObject(m_configData.getRestHost(), m_token.getToken(), l_requestData);
		log.info("Group Deleted");

		return l_status.getStatus();
	}

	public void createGroupMembership(String p_courseName, ICEnrollment p_enrollment, HashMap<String, GroupProxy> l_groups) {
		log.info("In updateGroup()");

		GroupHandler l_groupHandler = new GroupHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setCourseName(p_courseName);
		l_requestData.setUserName(p_enrollment.getUsername());
		GroupProxy l_groupProxy = l_groups.get(p_enrollment.getSectionId());
		l_requestData.setGroupId(l_groupProxy.getId());
		l_groupHandler.updateObject(m_configData.getRestHost(), m_token.getToken(), l_requestData);
	}

	public void createGroupMembership(ICBBGroup p_group) {
		log.info("In updateGroup()");

		GroupHandler l_groupHandler = new GroupHandler();
		RequestData l_requestData = new RequestData();
		l_requestData.setCourseName(p_group.getCourseId());
		l_requestData.setUserName(p_group.getUserName());
		l_requestData.setGroupId(p_group.getGroupId());
		l_groupHandler.updateObject(m_configData.getRestHost(), m_token.getToken(), l_requestData);
	}

	@Override
	public List<Grades> getByUserId(Timestamp startTime, Timestamp endTime, String userId, String[] lmsTerms,
			boolean includeMissing) {
		log.trace("In getByUserId()");

		List<String> l_lmsTermIds = null;
		if (lmsTerms != null) {
			l_lmsTermIds = this.getTermIds(lmsTerms);
		}

		List<Grades> l_grades = new ArrayList<Grades>();

		// Add In User Data
		UserProxy l_user = getUserId(userId);

		// Now Loop through the Enrollments
		if (l_user != null) {
			List<MembershipProxy> l_list = getMembershipUser(l_user.getId(), null);
			if (l_list != null) {
				for (MembershipProxy l_membership : l_list) {

					// Now get the Course
					CourseProxy l_course = getCourse(l_membership.getCourseId());

					log.info("Start Time (Loca): " + startTime);
					log.info("End Time (Local): " + endTime);

					if (l_course != null) {
						log.info("Getting Grades for Course ID: " + l_course.getCourseId());
						if (lmsTerms == null
								|| (l_course.getTermId() != null && checkTerm(l_course.getTermId(), l_lmsTermIds))) {
							// Get Excluded Data
							ArrayList<Exclude> l_excludedList = getExcludeList(l_course);

							// Now Get Grades
							ArrayList<GradebookProxy> l_gradeList = (ArrayList<GradebookProxy>) getGrades(
									l_membership.getUserId(), l_membership.getCourseId());

							// Process Grades
							List<Grades> l_gradesReturn = processGrades(l_gradeList, l_user, l_course, l_excludedList,
									startTime, endTime);
							l_grades.addAll(l_gradesReturn);

							if (includeMissing) {
								if (l_gradesReturn != null && l_gradesReturn.size() > 0) {
									// Get Missing Grades
									ArrayList<Grades> l_missingList = getMissingGrades(l_user, l_course, l_excludedList,
											l_gradesReturn, startTime, endTime);
									l_grades.addAll(l_missingList);
								}
							}
						} else {
							log.info("Skipping Course: " + l_course.getName());
						}
					}
				}
			}
		}

		return l_grades;
	}

	private boolean checkTerm(String p_termId, List<String> p_lmsTermIds) {
		if (p_lmsTermIds != null) {
			for (String lmsTermId : p_lmsTermIds) {
				if (lmsTermId.equalsIgnoreCase(p_termId)) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public List<Grades> getByCourseId(Timestamp startTime, Timestamp endTime, String courseId, String[] lmsTerms) {
		log.trace("In getByCourseId()");
		log.info("Getting Grades for Course ID: " + courseId);
		DateFormat DFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.getDefault());
		log.info("Start Time: " + DFormat.format(startTime));
		log.info("End Time: " + DFormat.format(endTime));

		List<Grades> l_grades = new ArrayList<Grades>();

		List<String> l_lmsTermIds = null;
		if (lmsTerms != null) {
			l_lmsTermIds = this.getTermIds(lmsTerms);
		}

		// Get Course Data
		CourseProxy l_course = getCourse(courseId);

		if (l_course != null) {
			if (lmsTerms == null || (l_course.getTermId() != null && checkTerm(l_course.getTermId(), l_lmsTermIds))) {

				// Get Excluded Data
				ArrayList<Exclude> l_excludedList = getExcludeList(l_course);

				// Now Loop through the Enrollments
				List<MembershipProxy> l_list = getMembershipCourse(courseId, null);
				if (l_list != null) {
					for (MembershipProxy l_membership : l_list) {

						// Add In User Data
						UserProxy l_user = getUserId(l_membership.getUserId());
						if (l_user != null) {
							// if (l_user.getId().equals("_244549_1")) {
							// Now Get Grades
							ArrayList<GradebookProxy> l_gradeList = (ArrayList<GradebookProxy>) getGrades(
									l_membership.getUserId(), l_membership.getCourseId());

							// Process Grades
							List<Grades> l_gradesReturn = processGrades(l_gradeList, l_user, l_course, l_excludedList,
									startTime, endTime);
							l_grades.addAll(l_gradesReturn);

							/*
							 * Commented Out to increase Performance if (l_gradesReturn != null &&
							 * l_gradesReturn.size() > 0) { // Get Missing Grades ArrayList<Grades>
							 * l_missingList = getMissingGrades(l_user, l_course, l_excludedList,
							 * l_gradeList); l_grades.addAll(l_missingList); }
							 */

						}
					}
				}
			}
		}
		return l_grades;
	}

	private List<Grades> processGrades(List<GradebookProxy> l_gradeList, UserProxy l_user, CourseProxy l_course,
			ArrayList<Exclude> l_excludedList, Timestamp startTime, Timestamp endTime) {
		log.trace("In processGrades()");
		ArrayList<Grades> l_grades = new ArrayList<Grades>();
		if (l_gradeList != null) {
			Grades l_grade = null;
			for (GradebookProxy l_gradeProxy : l_gradeList) {
				ColumnProxy l_column = getColumn(l_course.getId(), l_gradeProxy.getColumnId());
				if (l_column != null && l_column.getGrading() != null
						&& !l_column.getGrading().getType().equalsIgnoreCase("Calculated")
						&& !l_gradeProxy.isExempt()) {

					// Remove if an excluded Column or Category
					if (checkNotExcluded(l_excludedList, l_column)) {

						l_grade = new Grades();
						l_grade.setId(l_user.getId());
						l_grade.setFirstName(l_user.getName().getGiven());
						l_grade.setLastName(l_user.getName().getFamily());
						l_grade.setUserBatchUID(l_user.getExternalId());
						l_grade.setTitle(l_user.getName().getTitle());
						l_grade.setUserid(l_user.getUserName());
						l_grade.setColumnId(l_column.getId());
						l_grade.setCourseBatchUID(l_course.getExternalId());
						l_grade.setCourseid(l_course.getCourseId());
						l_grade.setCourseName(l_course.getName());
						l_grade.setCatalog(l_column.getGradebookCategoryId());
						l_grade.setDisplayScore(l_column.getName());
						l_grade.setTitle(l_column.getName());
						if (l_column.getGrading() != null && l_column.getGrading().getDue() != null) {
							l_grade.setDue(new Timestamp(l_column.getGrading().getDue().getTime()));
						}

						if (l_gradeProxy.getDisplayGrade() != null) {
							if (l_gradeProxy.getDisplayGrade().getScaleType().equalsIgnoreCase("Percent")) {

								BigDecimal l_score = convertPercent(l_gradeProxy.getDisplayGrade());
								l_grade.setScore(l_score);
							} else {

								l_grade.setScore(BigDecimal.valueOf(l_gradeProxy.getDisplayGrade().getScore()));
							}
							l_grade.setPoints(BigDecimal.valueOf(l_gradeProxy.getDisplayGrade().getPossible()));
						} else {
							l_grade.setPoints(new BigDecimal(0));
							l_grade.setScore(new BigDecimal(0));
						}

						// Check for Date Range if Overridden
						if (checkDateRange(l_gradeProxy, startTime, endTime)) {
							l_grade.setAttemptId(l_gradeProxy.getChangeIndex());
							l_grade.setAttemptDate(new Timestamp(l_gradeProxy.getOverridden().getTime()));
							l_grade.setDateModified(new Timestamp(l_gradeProxy.getOverridden().getTime()));
							l_grade.setStatus("Calculated");
							l_grade.setFeedback(l_gradeProxy.getFeedback());
							log.debug("Adding Grade: " + l_grade.getDisplayScore());
							log.debug("Adding Score: " + l_grade.getScore());
							log.debug("Adding Points: " + l_grade.getPoints());
							l_grades.add(l_grade);
						} else {
							// Need Attempt Data
							AttemptProxy l_attempt = selectAttempt(l_user, l_course, l_column, startTime, endTime);

							if (l_attempt != null) {

								l_grade.setAttemptId(l_attempt.getId());
								l_grade.setAttemptDate(new Timestamp(l_attempt.getAttemptDate().getTime()));
								l_grade.setDateModified(new Timestamp(l_attempt.getModified().getTime()));
								switch (l_attempt.getStatus()) {
								case "Completed":
									l_grade.setStatus("Calculated");
									break;
								case "InProgress":
									l_grade.setStatus("In Progress");
									break;
								case "NeedsGrading":
									l_grade.setStatus("Needs Grading");
									break;
								default:
									l_grade.setStatus(l_attempt.getStatus());
									break;

								}
								l_grade.setFeedback(l_attempt.getFeedback());

								if (l_attempt.isExempt()) {
									l_grade.setStatus("Exempt");
									l_grade.setScore(null);
								}

								log.debug("Adding Grade: " + l_grade.getDisplayScore());
								log.debug("Adding Score: " + l_grade.getScore());
								log.debug("Adding Points: " + l_grade.getPoints());

								l_grades.add(l_grade);
							}
						}
					}
				}
			}
		}
		return l_grades;
	}

	private BigDecimal convertPercent(DisplayGradeProxy displayGrade) {
		log.trace("In convertPercent()");
		BigDecimal l_score = BigDecimal.valueOf(displayGrade.getScore());
		BigDecimal l_points = BigDecimal.valueOf(displayGrade.getPossible());
		BigDecimal l_scoreConverted = l_score.divide(BigDecimal.valueOf(100)).multiply(l_points);
		return l_scoreConverted;
	}

	@Override
	public List<Grades> getByCourseAndUserId(Timestamp startTime, Timestamp endTime, String userId, String courseId,
			String[] lmsTerms) {
		log.trace("In getByCourseAndUserId()");
		log.info("Getting Grades for Course id: " + courseId);
		log.info("Getting Grades for User id: " + userId);
		log.info("Start Time: " + startTime);
		log.info("End Time: " + endTime);

		List<Grades> l_grades = new ArrayList<Grades>();

		List<String> l_lmsTermIds = null;
		if (lmsTerms != null) {
			l_lmsTermIds = this.getTermIds(lmsTerms);
		}

		// Get Course Data
		CourseProxy l_course = getCourse(courseId);

		// Get Excluded Data
		ArrayList<Exclude> l_excludedList = getExcludeList(l_course);

		// Add In User Data
		UserProxy l_user = getUserId(userId);

		// Now Loop through the Enrollments
		List<MembershipProxy> l_list = getMembershipCourse(courseId, userId);
		if (l_list != null) {

			if (l_course != null) {
				if (lmsTerms == null
						|| (l_course.getTermId() != null && checkTerm(l_course.getTermId(), l_lmsTermIds))) {

					// Now Get Grades
					List<GradebookProxy> l_gradeList = getGrades(l_user.getId(), l_course.getCourseId());

					// Process Grades
					List<Grades> l_gradesReturn = processGrades(l_gradeList, l_user, l_course, l_excludedList,
							startTime, endTime);
					l_grades.addAll(l_gradesReturn);
				}
			}
		}
		return l_grades;
	}

	@Override
	public List<String> getByClassName(String className, boolean includeUnavailable) {
		log.trace("In getByClassName()");
		List<String> l_courseList = new ArrayList<String>();
		l_courseList.addAll(getCoursesByName(className, null, includeUnavailable));
		return l_courseList;
	}

	@Override
	public List<String> getByCourseId(String courseId, boolean includeUnavailable) {
		log.trace("In getByCourseId()");
		CourseProxy l_course = getCourseByName(courseId);

		List<String> l_userList = new ArrayList<String>();
		if (l_course != null) {
			l_userList.addAll(getMembershipUsers(l_course.getId(), includeUnavailable));
		}
		return l_userList;
	}

	@Override
	public List<String> getByCategory(String categoryName, boolean includeUnavailable) {
		log.trace("In getByCategory()");
		ArrayList<String> l_courseList = new ArrayList<String>();
		List<String> l_categories = getCategories(categoryName, true);
		if (l_categories != null) {
			for (String l_category : l_categories) {
				List<String> l_temp = getCategoryCourses(l_category, null);
				l_courseList.addAll(l_temp);
			}
		}
		return l_courseList;
	}

	@Override
	public List<String> getByStudentName(String p_studentName, boolean p_includeUnavailable) {
		log.trace("In getByStudentName()");
		ArrayList<String> l_userList = new ArrayList<String>();
		List<UserProxy> l_users = getUsersByFamilyName(p_studentName, p_includeUnavailable);
		for (UserProxy l_user : l_users) {
			l_userList.add(l_user.getId());
		}
		return l_userList;
	}

	@Override
	public List<String> getByTeacherID(String teacherId, boolean includeUnavailable) {
		log.trace("In getByTeacherID()");
		ArrayList<String> l_courses = new ArrayList<String>();
		UserProxy l_user = getUserName(teacherId);

		List<MembershipProxy> l_memberships = getMembershipUser(l_user.getId(), "Instructor");
		if (l_memberships != null) {
			for (MembershipProxy l_membership : l_memberships) {
				l_courses.add(l_membership.getCourseId());
			}
		}
		return l_courses;
	}

	@Override
	public List<String> getByStudentID(String studentId, boolean includeUnavailable) {
		log.trace("In getByStudentID()");

		UserProxy l_user = getUserExternalId(studentId);
		List<String> l_userList = new ArrayList<String>();
		if (l_user != null) {
			l_userList.add(l_user.getId());
		}
		return l_userList;
	}

	@Override
	public List<String> getByClassName(String className, boolean includeUnavailable, String[] lmsTerm) {
		log.trace("In getByClassName(lmsTerm)");
		List<String> l_terms = getTermIds(lmsTerm);
		List<String> l_courseList = new ArrayList<String>();
		for (String l_term : l_terms) {
			l_courseList.addAll(getCoursesByName(className, l_term, includeUnavailable));
		}
		return l_courseList;
	}

	@Override
	public List<String> getByCourseId(String courseId, boolean includeUnavailable, String[] lmsTerm) {
		log.trace("In getByCourseId(lmsTerm)");
		CourseProxy l_course = getCourseByName(courseId);
		List<String> l_userList = new ArrayList<String>();
		if (l_course != null) {
			l_userList.addAll(getMembershipUsers(l_course.getId(), includeUnavailable));
		}
		return l_userList;
	}

	@Override
	public List<String> getByCategory(String categoryName, boolean includeUnavailable, String[] lmsTerm) {
		log.trace("In getByCategory(lmsTerm)");
		ArrayList<String> l_courseList = new ArrayList<String>();
		List<String> l_categories = getCategories(categoryName, true);
		if (l_categories != null) {
			for (String l_category : l_categories) {
				List<String> l_temp = getCategoryCourses(l_category, lmsTerm);
				l_courseList.addAll(l_temp);
			}
		}
		return l_courseList;
	}

	@Override
	public List<String> getByStudentName(String studentName, boolean includeUnavailable, String[] lmsTerm) {
		log.trace("In getByStudentName(lmsTerm)");
		ArrayList<String> l_userList = new ArrayList<String>();
		List<UserProxy> l_users = getUsersByFamilyName(studentName, includeUnavailable);
		for (UserProxy l_user : l_users) {
			l_userList.add(l_user.getId());
		}
		return l_userList;
	}

	@Override
	public List<String> getByTeacherID(String teacherId, boolean includeUnavailable, String[] lmsTerm) {
		log.trace("In getByTeacherID(lmsTerm)");
		ArrayList<String> l_courses = new ArrayList<String>();
		UserProxy l_user = getUserName(teacherId);
		List<MembershipProxy> l_memberships = getMembershipUser(l_user.getId(), "Instructor");
		if (l_user != null) {
			if (l_memberships != null) {
				for (MembershipProxy l_membership : l_memberships) {
					l_courses.add(l_membership.getCourseId());
				}
			}
		}
		return l_courses;
	}

	@Override
	public List<String> getByStudentID(String studentId, boolean includeUnavailable, String[] lmsTerm) {
		log.trace("In getByStudentID(lmsTerm)");

		UserProxy l_user = getUserExternalId(studentId);
		List<String> l_userList = new ArrayList<String>();
		if (l_user != null) {
			l_userList.add(l_user.getId());
		}
		return l_userList;
	}

	@Override
	public List<String> getByUserName(String userName, boolean includeUnavailable, String[] lmsTerm) {
		log.trace("In getByUserName(lmsTerm)");
		UserProxy l_user = getUserName(userName);
		List<String> l_userList = new ArrayList<String>();
		if (l_user != null) {
			l_userList.add(l_user.getId());
		}
		return l_userList;
	}

	@Override
	public List<String> getByTerm(String[] terms, boolean includeUnavailable) {
		log.trace("In getByTerm()");
		List<String> l_courseList = null;
		try {
			l_courseList = new ArrayList<String>();
			List<String> l_termIds = getTermIds(terms);

			if (l_termIds != null && l_termIds.size() > 0) {
				for (String l_termId : l_termIds) {
					List<String> l_courseListTemp = getCourses(l_termId, includeUnavailable);
					l_courseList.addAll(l_courseListTemp);
				}
			}
		} catch (Exception l_ex) {
			log.error("Error: ", l_ex);
		}
		log.debug("Courses Found: " + l_courseList.size());
		return l_courseList;
	}

	@Override
	public List<StudentData> getUsersByCourseId(String courseId, boolean includeUnavailable, String[] lmsTerms) {
		log.trace("In getUsersByCourseId(lmsTerm)");
		CourseProxy l_course = getCourseByName(courseId);
		List<StudentData> l_studentList = new ArrayList<StudentData>();
		if (l_course != null) {
			l_studentList.addAll(getMembershipStudents(l_course.getId(), includeUnavailable));
		}
		sort((ArrayList<StudentData>) l_studentList);
		return l_studentList;
	}

	@Override
	public List<StudentData> getUsersByStudentId(String studentId, boolean includeUnavailable, String[] lmsTerm) {
		log.trace("In getUsersByStudentId(lmsTerm)");
		UserProxy l_user = this.getUserExternalId(studentId);
		List<StudentData> l_studentList = new ArrayList<StudentData>();
		if (l_user != null) {
			l_studentList.add(new StudentData(l_user));
		}
		return l_studentList;
	}

	@Override
	public List<StudentData> getUsersByObserver(String parentId, boolean includeUnavailable, String[] lmsTerm) {
		log.trace("In getUsersByObserver(lmsTerm)");
		List<StudentData> l_studentList = new ArrayList<StudentData>();
		UserProxy l_user = this.getUserName(parentId);
		if (l_user != null) {
			//List<ParentProxy> l_students = this.getParentAssociations(null, l_user.getId(), includeUnavailable);
			//for (ParentProxy l_student : l_students) {
			//	l_studentList.add(new StudentData(l_student));
			//}
		}
		return l_studentList;
	}

	@Override
	public List<StudentData> getUsersByLocation(String location, boolean includeUnavailable, String[] lmsTerm) {
		log.info("In getUsersByLocation(lmsTerm)");
		log.info("Location: " + location);
		List<StudentData> l_studentList = new ArrayList<StudentData>();
		List<String> l_categories = this.getCategories(location, includeUnavailable);
		if (l_categories != null) {
			for (String l_category : l_categories) {
				log.info("Category: " + l_category);
				l_studentList.addAll(this.getCategoryCoursesStudents(l_category, includeUnavailable, lmsTerm));
			}
		}
		sort((ArrayList<StudentData>) l_studentList);
		return l_studentList;
	}

	@Override
	public List<StudentData> getUsersByTeacher(String teacherId, boolean includeUnavailable, String[] lmsTerms) {
		log.trace("In getUsersByTeacher(lmsTerm)");
		List<StudentData> l_studentList = new ArrayList<StudentData>();
		UserProxy l_user = getUserName(teacherId.toLowerCase());
		if (l_user != null) {
			l_studentList.addAll(getMembershipStudents(l_user.getId(), includeUnavailable, "Instructor", lmsTerms));
		}
		sort((ArrayList<StudentData>) l_studentList);
		return l_studentList;
	}

	@Override
	public List<StudentData> getUsersByTerms(boolean includeUnavailable, String[] lmsTerms) {
		log.trace("In getUsersByTerms(lmsTerm)");
		HashMap<String, StudentData> l_studentList = new HashMap<String, StudentData>();
		List<String> l_termIds = getTermIds(lmsTerms);
		if (l_termIds != null) {
			for (String l_termId : l_termIds) {
				List<String> l_courseList = getCourses(l_termId, includeUnavailable);
				if (l_courseList != null) {
					int l_size = l_courseList.size();
					if (l_courseList != null) {
						int i = 1;
						for (String l_course : l_courseList) {
							log.info("Processing Course: " + l_course + " ... " + i + " of " + l_size);
							List<StudentData> l_students = getMembershipStudents(l_course, includeUnavailable);
							for (StudentData l_student : l_students) {
								l_studentList.put(l_student.getId(), l_student);
							}
							i++;
						}
					}
				}
			}
		}
		Collection<StudentData> l_values = l_studentList.values();
		ArrayList<StudentData> l_newList = new ArrayList<>(l_values);

		sort(l_newList);
		return l_newList;
	}

	@Override
	public List<StudentData> getUsersByUserId(String userId, boolean includeUnavailable, String[] lmsTerm) {
		log.trace("In getUsersByUserId(lmsTerm)");
		UserProxy l_user = getUserName(userId.toLowerCase());
		List<StudentData> l_studentList = new ArrayList<StudentData>();
		if (l_user != null) {
			l_studentList.add(new StudentData(l_user));
		}
		return l_studentList;
	}

	@Override
	public List<StudentData> getUsersByStudentName(String studentName, boolean includeUnavailable, String[] lmsTerm) {
		log.trace("In getUsersByStudentName(lmsTerm)");
		List<UserProxy> l_users = getUsersByFamilyName(studentName, includeUnavailable);
		ArrayList<StudentData> l_studentList = new ArrayList<StudentData>();
		if (l_users != null) {
			for (UserProxy l_user : l_users) {
				l_studentList.add(new StudentData(l_user));
			}
		}
		sort(l_studentList);
		return l_studentList;
	}

	@Override
	public List<StudentData> getUsersByClassName(String className, boolean includeUnavailable, String[] lmsTerms) {
		log.trace("In getUsersByClassName(lmsTerm)");
		HashMap<String, StudentData> l_studentList = new HashMap<String, StudentData>();
		List<String> l_termIds = getTermIds(lmsTerms);
		if (l_termIds != null) {
			for (String l_termId : l_termIds) {
				List<String> l_courseList = getCoursesByName(className, l_termId, includeUnavailable);
				if (l_courseList != null) {
					int l_size = l_courseList.size();
					if (l_courseList != null) {
						int i = 1;
						for (String l_course : l_courseList) {
							log.info("Processing Course: " + l_course + " ... " + i + " of " + l_size);
							List<StudentData> l_students = getMembershipStudents(l_course, includeUnavailable);
							for (StudentData l_student : l_students) {
								l_studentList.put(l_student.getId(), l_student);
							}
							i++;
						}
					}
				}
			}
		}
		Collection<StudentData> l_values = l_studentList.values();
		ArrayList<StudentData> l_newList = new ArrayList<>(l_values);

		sort(l_newList);
		return l_newList;
	}

	@Override
	public List<TermProxy> getTerms() {
		log.trace("In getTerms()");
		ArrayList<TermProxy> l_terms = new ArrayList<TermProxy>();

		if (m_termCache != null && m_termCache.size() > 0) {
			log.debug(" TERM CACHE HIT ...");
			return m_termCache;
		} else {
			checkToken();
			// Get the Term ID
			TermHandler l_termHandler = new TermHandler();
			TermResponseProxy l_termResponse = l_termHandler.getClientData(m_configData.getRestHost(),
					m_token.getToken(), null, null);
			if (l_termResponse != null) {
				TermListProxy l_list = l_termResponse.getResults();
				PagingProxy l_page = l_termResponse.getPaging();
				while (l_page != null) {
					if (m_termCache != null) {
						m_termCache.addAll(l_list);
					}
					l_terms.addAll(l_list);
					// Now Iterate to next Page
					checkToken();
					l_termResponse = l_termHandler.getClientData(m_configData.getRestHost(), m_token.getToken(),
							l_page.getNextPage(), null);
					l_list = l_termResponse.getResults();
					l_page = l_termResponse.getPaging();

				}

				// Process Last Page
				if (m_termCache != null) {
					m_termCache.addAll(l_list);
				}
				l_terms.addAll(l_list);
			}
		}
		return l_terms;
	}

	public List<String> getTermIds(String[] p_terms) {
		log.trace("In getTermIds()");
		ArrayList<String> l_termList = new ArrayList<String>();

		List<TermProxy> l_terms = getTerms();
		for (TermProxy l_term : l_terms) {
			for (String l_termId : p_terms) {
				if (l_term.getName().equalsIgnoreCase(l_termId)) {
					l_termList.add(l_term.getId());
				}
			}
		}
		return l_termList;
	}

	public TermProxy getTermName(String termId) {
		log.debug("In getTermName()");
		if (m_termCache != null && m_termCache.size() > 0) {
			log.info(" TERM CACHE HIT ...");
			for (TermProxy l_term : m_termCache) {
				if (l_term.getName().equalsIgnoreCase(termId)) {
					return l_term;
				}
			}
		} else {
			TermHandler l_termHandler = new TermHandler();
			RequestData l_request = new RequestData();
			l_request.setTermId(termId);
			TermProxy l_term = l_termHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null,
					l_request);
			return l_term;
		}
		return null;
	}

	private boolean checkDateRange(GradebookProxy p_grade, Timestamp p_start, Timestamp p_end) {
		log.trace("In checkDateRange()");
		if (p_grade != null && p_grade.getOverridden() != null && p_grade.getOverridden().after(p_start)
				&& p_grade.getOverridden().before(p_end)) {
			return true;
		}

		return false;
	}

	public ArrayList<Exclude> getExcludeList(CourseProxy p_course) {
		log.trace("In getExcludeList()");
		ArrayList<Exclude> l_excludeList = new ArrayList<Exclude>();

		List<ColumnProxy> l_columnList = getColumns(p_course.getId());
		if (l_columnList != null) {
			for (ColumnProxy l_column : l_columnList) {
				if (l_column.isExternalGrade()) {
					// Parse Formula
					if (l_column.getFormula() != null && l_column.getFormula().getFormula().contains(")@X@")) {
						String l_formula = l_column.getFormula().getFormula().substring(
								l_column.getFormula().getFormula().indexOf('{'),
								l_column.getFormula().getFormula().indexOf(")@X@"));
						log.debug("FORMULA2: " + l_formula);
						HashMap<String, String> l_aliasList = l_column.getFormula().getAliases();
						FormulaHandler l_formulaHandler = new FormulaHandler();
						FormulaParserProxy l_formulaProxy = l_formulaHandler.getClientData(l_formula, null, null);
						if (l_formulaProxy != null && l_formulaProxy.getSelected() != null) {
							ElementListProxy l_elements = l_formulaProxy.getSelected().getElements();
							for (ElementProxy l_element : l_elements) {
								ArrayList<HashMap> l_excludes = l_element.getExclude_list();
								if (l_excludes != null) {
									for (HashMap l_excludeMap : l_excludes) {
										Collection<String> l_values = l_excludeMap.values();
										for (String l_value : l_values) {
											Exclude l_exclude = new Exclude();
											// l_exclude.setValue(l_value.substring(1) + "_1");
											l_exclude.setValue(l_aliasList.get(l_value));
											if (l_value.charAt(0) == 'c') {
												l_exclude.setType(Exclude.CATEGORY);
											} else {
												l_exclude.setType(Exclude.COLUMN);
											}
											l_excludeList.add(l_exclude);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		log.debug("Excluded List Size: " + l_excludeList.size());
		return l_excludeList;
	}


	public HTTPStatus createCourseCopy(CourseInfo p_info) {
		log.info("In createCourseCopy()");

		CourseCopyHandler l_copyHandler = new CourseCopyHandler();

		RequestData l_requestData = new RequestData();
		l_requestData.setCourseId(p_info.getCourseTemplateId());
		log.info("  COURSE ID: " + p_info.getCourseTemplateId());

		checkToken();
		HTTPStatus l_response = l_copyHandler.createObject(m_configData.getRestHost(), m_token.getToken(), l_requestData, p_info.getTargetCourseId());

		if (l_response.getStatus() == 202) {
			// Need to get Location in Header
			List<String> l_location = l_response.getHeaders().get("location");

			// Use Location from Header to query Task Handler to determine of the Task has completed
			TaskHandler l_taskHandler = new TaskHandler();
			l_requestData.setUrl(l_location.get(0));
			TaskProxy l_task = null;
			do {
				l_task = l_taskHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_requestData);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException l_ex) {
					log.error("Error: ", l_ex);
				}
			} while (l_task != null && l_task.getStatus() != null);

			// Now update the Course Name and Description
			l_task.getCourse().setName(p_info.getTargetCourseName());
			CourseHandler l_courseHandler = new CourseHandler();
			l_requestData.setCourseId(l_task.getCourse().getId());
			l_task.getCourse().getAvailability().setAvailable("Yes");
			l_courseHandler.updateObject(m_configData.getRestHost(), m_token.getToken(), l_requestData, l_task.getCourse());

		}

		return l_response;
	}


	public int updateCourse(UpdateCourseInfo p_info) {
		log.info("In updateCourse()");


		RequestData l_requestData = new RequestData();
		HTTPStatus l_response = null;

		checkToken();

		// Now update the Course Name and Description
		CourseHandler l_courseHandler = new CourseHandler();

		// Get the Course
		l_requestData.setCourseName(p_info.getBbCourseId());
		CourseProxy l_course = l_courseHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_requestData);
		if (l_course != null) {
			l_course.setName(p_info.getBbCourseName());
			l_course.setDescription(p_info.getBbCourseDescription());
			l_requestData.setCourseId(l_course.getId());
			l_response = l_courseHandler.updateObject(m_configData.getRestHost(), m_token.getToken(), l_requestData, l_course);
		}

		return l_response.getStatus();
	}

	private boolean checkNotExcluded(ArrayList<Exclude> p_excludedList, ColumnProxy p_column) {
		log.trace("In checkNotExcluded()");
		if (p_column != null) {
			if (!p_column.isIncludeInCalculations()) {
				log.info("EXCLUDING: " + p_column.getId());
				return false;
			} else {
				for (Exclude l_exclude : p_excludedList) {
					if (p_column.getGradebookCategoryId() != null && l_exclude.getType().equals(Exclude.CATEGORY)) {
						if (p_column.getGradebookCategoryId().equalsIgnoreCase(l_exclude.getValue())) {
							log.debug("EXCLUDING: " + p_column.getId());
							return false;
						}

					} else if (l_exclude.getType().equals(Exclude.COLUMN)) {
						if (p_column.getId().equalsIgnoreCase(l_exclude.getValue())) {
							log.debug("EXCLUDING: " + p_column.getId());
							return false;
						}
					} 
				}
			}
		}
		return true;
	}

	private AttemptProxy selectAttempt(UserProxy p_user, CourseProxy p_course, ColumnProxy p_column, Timestamp p_start,
			Timestamp p_end) {
		log.trace("In selectAttempt()" + p_column.getName());
		AttemptProxy l_selected = null;
		List<AttemptProxy> l_attempts = getAttempts(p_user.getId(), p_column.getId(), p_course.getId(), p_start);
		if (l_attempts != null) {
			for (AttemptProxy l_attempt : l_attempts) {
				if (!l_attempt.isExempt()) { // Ignore if this is Exempt
					if (l_attempt.getStatus().equalsIgnoreCase("Completed")
							&& l_attempt.getUserId().equalsIgnoreCase(p_user.getId())) {
						if (l_attempt.getAttemptDate().after(p_start) && l_attempt.getAttemptDate().before(p_end)) {
							if (l_selected != null) {
								if (l_attempt.getAttemptDate().after(l_selected.getAttemptDate())) {
									l_selected = l_attempt;
								}
							} else {
								l_selected = l_attempt;
							}
						}
					}
				}
			}
		}
		return l_selected;
	}

	private AttemptProxy selectAnyAttempt(UserProxy p_user, CourseProxy p_course, ColumnProxy p_column,
			Timestamp p_start, Timestamp p_end) {
		log.trace("In selectAnyAttempt()");
		AttemptProxy l_selected = null;
		List<AttemptProxy> l_attempts = getAnyAttempts(p_user.getId(), p_column.getId(), p_course.getId(), p_start);
		if (l_attempts != null) {
			for (AttemptProxy l_attempt : l_attempts) {
				if (!l_attempt.isExempt()) { // Ignore if this is Exempt
					if (l_attempt.getUserId().equalsIgnoreCase(p_user.getId())) {
						if (l_attempt.getAttemptDate().after(p_start) && l_attempt.getAttemptDate().before(p_end)) {
							if (l_selected != null) {
								if (l_attempt.getAttemptDate().after(l_selected.getAttemptDate())) {
									l_selected = l_attempt;
								}
							} else {
								l_selected = l_attempt;
							}
						}
					}
				}
			}
		}
		return l_selected;
	}

	private AttemptProxy selectLastAttempt(UserProxy p_user, CourseProxy p_course, ColumnProxy p_column,
			Timestamp p_start, Timestamp p_end) {
		log.trace("In selectLastAttempt()");
		AttemptProxy l_selected = null;
		List<AttemptProxy> l_attempts = getAnyAttempts(p_user.getId(), p_column.getId(), p_course.getId(), p_start);
		if (l_attempts != null) {
			for (AttemptProxy l_attempt : l_attempts) {
				if (!l_attempt.isExempt()) { // Ignore if this is Exempt
					if (l_attempt.getUserId().equalsIgnoreCase(p_user.getId())) {
						if (l_selected != null) {
							if (l_attempt.getAttemptDate().after(l_selected.getAttemptDate())) {
								l_selected = l_attempt;
							}
						} else {
							l_selected = l_attempt;
						}
					}
				}
			}
		}
		if (l_selected != null) {
			if (l_selected.getAttemptDate().after(p_start) && l_selected.getAttemptDate().before(p_end)) {
				return l_selected;
			} else {
				return null;
			}
		}
		return l_selected;
	}

	private ArrayList<Grades> getMissingGrades(UserProxy p_user, CourseProxy p_course, ArrayList<Exclude> p_excludeList,
			List<Grades> p_gradeList, Timestamp start, Timestamp end) {
		log.trace("In getMissingGrades: " + p_course.getName());
		Timestamp l_searchDate = new Timestamp(0);
		List<ColumnProxy> l_columns = this.getColumns(p_course.getId());
		ArrayList<Grades> l_gradeList = new ArrayList<Grades>();
		if (l_columns != null) {
			for (ColumnProxy l_column : l_columns) {
				// Check if Calculates
				if (l_column.getGrading() != null && !l_column.getGrading().getType().equalsIgnoreCase("Calculated")) {
					// Now check if Excluded
					if (checkNotExcluded(p_excludeList, l_column)) {
						// Now Check to see if Not In List
						NotInListStatus l_status = notInList(p_user.getId(), l_column, p_course.getId(), p_gradeList,
								start, end);
						if (l_status.isStatus()) {
							Grades l_grade = new Grades();
							l_grade.setId(p_user.getId());
							l_grade.setFirstName(p_user.getName().getGiven());
							l_grade.setLastName(p_user.getName().getFamily());
							l_grade.setUserBatchUID(p_user.getExternalId());
							l_grade.setTitle(p_user.getName().getTitle());
							l_grade.setUserid(p_user.getUserName());
							if (l_column.getGrading() != null && l_column.getGrading().getDue() != null) {
								l_grade.setDue(new Timestamp(l_column.getGrading().getDue().getTime()));
							}
							l_grade.setCourseBatchUID(p_course.getExternalId());
							l_grade.setCourseid(p_course.getCourseId());
							l_grade.setCourseName(p_course.getName());
							l_grade.setCatalog(l_column.getGradebookCategoryId());
							l_grade.setDisplayScore(l_column.getName());
							l_grade.setTitle(l_column.getName());
							l_grade.setPoints(BigDecimal.valueOf(l_column.getScore().getPossible()));
							if (l_status.getAttempt() != null) {
								if (l_status.getAttempt().getStatus() != null
										&& l_status.getAttempt().getStatus().equals("NeedsGrading")) {
									l_grade.setStatus("Needs Grading");
								} else if (l_status.getAttempt().getStatus() != null
										&& l_status.getAttempt().getStatus().equals("InProgress")) {
									l_grade.setStatus("In Progress");
								} else if (l_status.getAttempt().getStatus() != null
										&& l_status.getAttempt().getStatus().equals("Exempt")) {
									l_grade.setStatus("Exempt");
								} else {
									l_grade.setStatus("Not Submitted");
								}
								if (!Double.isNaN(l_status.getAttempt().getScore())) {
									l_grade.setScore(BigDecimal.valueOf(l_status.getAttempt().getScore()));
								} else {
									l_grade.setScore(null);
								}

							} else {
								l_grade.setStatus("Not Submitted");
							}
							l_gradeList.add(l_grade);
						}

					} else {
						// Get Grade
						GradebookProxy l_score = getGrade(p_user.getId(), p_course.getId(), l_column.getId());
						Grades l_grade = new Grades();
						l_grade.setId(p_user.getId());
						l_grade.setFirstName(p_user.getName().getGiven());
						l_grade.setLastName(p_user.getName().getFamily());
						l_grade.setUserBatchUID(p_user.getExternalId());
						l_grade.setTitle(p_user.getName().getTitle());
						l_grade.setUserid(p_user.getUserName());
						if (l_column.getGrading() != null && l_column.getGrading().getDue() != null) {
							l_grade.setDue(new Timestamp(l_column.getGrading().getDue().getTime()));
						}
						l_grade.setCourseBatchUID(p_course.getExternalId());
						l_grade.setCourseid(p_course.getCourseId());
						l_grade.setCourseName(p_course.getName());
						l_grade.setCatalog(l_column.getGradebookCategoryId());
						l_grade.setDisplayScore(l_column.getName());
						l_grade.setTitle(l_column.getName());
						l_grade.setPoints(BigDecimal.valueOf(l_column.getScore().getPossible()));
						if (l_score != null && l_score.getDisplayGrade() != null) {
							l_grade.setExcludedScore(BigDecimal.valueOf(l_score.getDisplayGrade().getScore()));
						}
						l_grade.setStatus("Excluded");
						l_gradeList.add(l_grade);
					}
				}
			}
		}
		return l_gradeList;

	}

	private NotInListStatus notInList(String p_userId, ColumnProxy l_column, String p_courseId,
			List<Grades> p_gradeList, Timestamp start, Timestamp end) {
		log.trace("In notInList()");
		NotInListStatus l_return = new NotInListStatus();
		for (Grades l_grade : p_gradeList) {
			if (l_column.getId().equalsIgnoreCase(l_grade.getColumnId())) {
				l_return.setStatus(false);
				return l_return;
			}
		}
		GradebookProxy l_grade = this.getGrade(p_userId, p_courseId, l_column.getId());
		AttemptProxy l_attempt = null;
		if (l_grade != null) {
			if (l_grade.isExempt()) {
				l_attempt = new AttemptProxy();
				l_attempt.setStatus("Exempt");
				l_attempt.setScore(Double.NaN);
				l_return.setAttempt(l_attempt);
			} else if (l_grade.getOverridden() == null) {
				// Need Attempt Data
				UserProxy l_user = getUserId(p_userId);
				CourseProxy l_course = getCourse(p_courseId);
				AttemptProxy l_attemptSelected = selectLastAttempt(l_user, l_course, l_column, start, end);
				if (l_attemptSelected != null) {
					if (!l_attemptSelected.getStatus().equalsIgnoreCase("Completed")) {
						l_attempt = new AttemptProxy();
						l_attempt.setStatus(l_attemptSelected.getStatus());
						if (l_grade.getDisplayGrade() != null) {
							l_attempt.setScore(l_grade.getDisplayGrade().getScore());
						} else {
							l_attempt.setScore(Double.NaN);
						}
					} else {
						l_return.setStatus(false);
						return l_return;
					}
				} else {
					l_attempt = new AttemptProxy();
					if (l_grade.getDisplayGrade() != null) {
						l_attempt.setStatus(l_grade.getStatus());
						l_attempt.setScore(l_grade.getDisplayGrade().getScore());
					} else {
						l_attempt.setStatus("Not Submitted");
						l_attempt.setScore(Double.NaN);
					}
				}
				l_return.setAttempt(l_attempt);
			} else {
				// AttemptProxy l_attempt = new AttemptProxy();
				// l_attempt.setScore(l_grade.getScore());
				// l_attempt.setStatus(l_grade.getStatus());
				l_return.setStatus(false);
				return l_return;
			}
		}
		l_return.setStatus(true);
		return l_return;
	}

	private NotInListStatus notInListOLD(String p_userId, ColumnProxy l_column, String p_courseId,
			List<Grades> p_gradeList, Timestamp start, Timestamp end) {
		Timestamp l_searchDate = new Timestamp(0);
		NotInListStatus l_return = new NotInListStatus();
		for (Grades l_grade : p_gradeList) {
			if (l_column.getId().equalsIgnoreCase(l_grade.getColumnId())) {
				l_return.setStatus(false);
				return l_return;
			}
		}

		// Check to see if Missing Grade has any Overrides
		// Added this check per TT Meeting on 1/11/2023

		List<AttemptProxy> l_attempts = this.getAnyAttempts(p_userId, l_column.getId(), p_courseId, l_searchDate);
		if (l_attempts.size() > 0) {
			l_return.setStatus(true);
			l_return.setAttempt(findAttempt(l_attempts, p_userId, start, end));
			return l_return;
		} else if (checkAnyOverrides(l_column.getId(), p_courseId)) {
			l_return.setStatus(true);
			return l_return;
		} else {
			l_return.setStatus(false);
			return l_return;
		}
	}

	private boolean checkAnyOverrides(String p_columnId, String p_courseId) {
		log.trace("In checkAnyOverrides: ");
		List<StudentData> l_students = this.getMembershipStudents(p_courseId, false);
		if (l_students != null) {
			for (StudentData l_student : l_students) {
				GradebookProxy l_grade = this.getGrade(l_student.getId(), p_courseId, p_columnId);
				if (l_grade != null && l_grade.getOverridden() != null) {
					return true;
				}
			}
		}
		return false;
	}

	private AttemptProxy findAttempt(List<AttemptProxy> l_attempts, String p_userId, Timestamp p_start,
			Timestamp p_end) {
		AttemptProxy l_selected = null;
		for (AttemptProxy l_attempt : l_attempts) {
			if (!l_attempt.isExempt()) { // Ignore if this is Exempt
				if (!l_attempt.getStatus().equalsIgnoreCase("Completed")
						&& l_attempt.getUserId().equalsIgnoreCase(p_userId)) {
					if (l_attempt.getAttemptDate().after(p_start) && l_attempt.getAttemptDate().before(p_end)) {
						if (l_selected != null) {
							if (l_attempt.getAttemptDate().after(l_selected.getAttemptDate())) {
								l_selected = l_attempt;
							}
						} else {
							l_selected = l_attempt;
						}
					}
				}
			}
		}
		return l_selected;
	}

	private AttemptProxy findAnyAttempt(List<AttemptProxy> l_attempts, String p_userId, Timestamp p_start,
			Timestamp p_end) {
		AttemptProxy l_selected = null;
		for (AttemptProxy l_attempt : l_attempts) {
			if (!l_attempt.isExempt()) { // Ignore if this is Exempt
				if (l_attempt.getUserId().equalsIgnoreCase(p_userId)) {
					if (l_attempt.getAttemptDate().after(p_start) && l_attempt.getAttemptDate().before(p_end)) {
						if (l_selected != null) {
							if (l_attempt.getAttemptDate().after(l_selected.getAttemptDate())) {
								l_selected = l_attempt;
							}
						} else {
							l_selected = l_attempt;
						}
					}
				}
			}
		}
		return l_selected;
	}

	public void clearCache() {

		log.trace("In clearCache()");
		m_courseCache.clear();
		m_courseColumnCache.clear();
		m_userCache.clear();
		m_columnCache.clear();
		m_attemptCache.clear();
		m_termCache.clear();
		m_gradeCache.clear();
		m_studentCache.clear();
		m_instructorCache.clear();
		m_categoryCache.clear();
	}
}
