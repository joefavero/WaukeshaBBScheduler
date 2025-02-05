/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obsidiansoln.blackboard.course.CourseHandler;
import com.obsidiansoln.blackboard.course.CourseListProxy;
import com.obsidiansoln.blackboard.course.CourseProxy;
import com.obsidiansoln.blackboard.course.CourseResponseProxy;
import com.obsidiansoln.blackboard.gradebook.ColumnHandler;
import com.obsidiansoln.blackboard.gradebook.ColumnListProxy;
import com.obsidiansoln.blackboard.gradebook.ColumnProxy;
import com.obsidiansoln.blackboard.gradebook.ColumnResponseProxy;
import com.obsidiansoln.blackboard.gradebook.GradebookHandler;
import com.obsidiansoln.blackboard.gradebook.GradebookListProxy;
import com.obsidiansoln.blackboard.gradebook.GradebookProxy;
import com.obsidiansoln.blackboard.gradebook.GradebookResponseProxy;
import com.obsidiansoln.blackboard.membership.MembershipHandler;
import com.obsidiansoln.blackboard.membership.MembershipListProxy;
import com.obsidiansoln.blackboard.membership.MembershipProxy;
import com.obsidiansoln.blackboard.membership.MembershipResponseProxy;
import com.obsidiansoln.blackboard.model.Exclude;
import com.obsidiansoln.blackboard.model.RequestData;
import com.obsidiansoln.blackboard.term.TermHandler;
import com.obsidiansoln.blackboard.term.TermListProxy;
import com.obsidiansoln.blackboard.term.TermProxy;
import com.obsidiansoln.blackboard.term.TermResponseProxy;
import com.obsidiansoln.blackboard.user.ParentHandler;
import com.obsidiansoln.blackboard.user.ParentListProxy;
import com.obsidiansoln.blackboard.user.ParentResponseProxy;
import com.obsidiansoln.blackboard.user.UserHandler;
import com.obsidiansoln.blackboard.user.UserListProxy;
import com.obsidiansoln.blackboard.user.UserProxy;
import com.obsidiansoln.blackboard.user.UserResponseProxy;
import com.obsidiansoln.util.RestManager;
import com.obsidiansoln.web.model.ConfigData;
import com.obsidiansoln.web.service.BBSchedulerService;

public class TestRest {

	private Token m_token = null;
	private static final Logger log = LoggerFactory.getLogger(TestRest.class);
	private ConfigData m_configData = null;

	public TestRest() {
		BBSchedulerService l_service = new BBSchedulerService();
		try {
			m_configData = l_service.getConfigData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		log.trace("Starting Test Rest Tool '..");
		TestRest l_client = new TestRest();

		// Get Token
		log.info("Getting Token ...");
		l_client.authorize();

		// Get Terms
		log.info("Getting Terms ...");
		// l_client.getTerms();

		// Get Users
		log.info("Getting Users ...");
		// l_client.getUsers();

		// Get Courses
		log.info("Getting Course ...");
		// l_client.getCourses();

		// Get Membership
		log.info("Getting Membership ...");
		// l_client.getMembership();

		// Get Columns for Course
		log.info("Getting Columns For Course ...");
		//l_client.getCourseColumns();

		// Get Grades for User
		log.info("Getting Grades for User ...");
		// GradebookListProxy l_studentGrades = l_client.getGradesUser();
		
		// Test Parents
		log.info("GettingParents for User ...");
		ParentListProxy l_parents = l_client.getParents();
	}

	private ParentListProxy getParents() {
		log.trace("In getParents()");
		ParentResponseProxy l_returnJson = null;
		ParentListProxy l_list = null;
		
		RequestData l_data = new RequestData();
		l_data.setUserId("_257415_1");
		ParentHandler l_parentHandler = new ParentHandler();
		ParentResponseProxy l_parents = l_parentHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), null, l_data);
		
		if (l_returnJson != null) {
			l_list = l_returnJson.getResults();
			PagingProxy l_page = l_returnJson.getPaging();
		}
		
		return l_list;
	}

	private void getCourseColumns() {
		log.trace("In getCourseColumns()");
		ColumnResponseProxy l_returnJson = null;
		ColumnHandler l_columnHandler = new ColumnHandler();

		RequestData l_data = new RequestData();
		l_data.setCourseId(RestConstants.GRADEBOOK_COURSE_ID);
		
		CourseHandler l_courseHandler = new CourseHandler();
		CourseProxy l_course = l_courseHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_data);

		l_returnJson = l_columnHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), null, l_data);
		if (l_returnJson != null) {
			ColumnListProxy l_list = l_returnJson.getResults();
			PagingProxy l_page = l_returnJson.getPaging();
			for (int i = 0; i < l_list.size(); i++) {
				ColumnProxy l_column = l_list.get(i);
				if (l_column.getFormula() != null) {
					log.info("FORMULA1: " + l_column.getFormula().getFormula());
					BBSchedulerService l_service = new BBSchedulerService();
					RestManager l_mgr;
					try {
						l_mgr = new RestManager(l_service.getConfigData());
						ArrayList<Exclude> l_excludes = l_mgr.getExcludeList(l_course);
						for (Exclude l_exclude : l_excludes) {
							log.info("Type: " + l_exclude.getType());
							log.info("Value: " + l_exclude.getValue());
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
				log.info("Id: " + l_column.getId());
				log.info("Name " + l_column.getName());
				log.info("Created " + l_column.getCreated());
				if (l_column.getAvailablity() != null) {
					log.info("   Available: " + l_column.getAvailablity().getAvailable());
				}
			}
		}
	}

	private ColumnProxy getColumnInfo(String p_columnId) {
		log.trace("In getColumnInfo()");
		ColumnProxy l_returnJson = null;
		ColumnHandler l_columnHandler = new ColumnHandler();

		RequestData l_data = new RequestData();
		l_data.setCourseId(RestConstants.GRADEBOOK_COURSE_ID);
		l_data.setColumnId(p_columnId);

		l_returnJson = l_columnHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_data);

		return l_returnJson;
	}

	private GradebookListProxy getGradesUser() {
		log.trace("In getGradesUser()");
		GradebookResponseProxy l_returnJson = null;
		GradebookHandler l_membershipHandler = new GradebookHandler();
		l_returnJson = l_membershipHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), null, null);
		GradebookListProxy l_list = l_returnJson.getResults();
		PagingProxy l_page = l_returnJson.getPaging();
		for (int i = 0; i < l_list.size(); i++) {
			GradebookProxy l_gradebook = l_list.get(i);
			ColumnProxy l_column = getColumnInfo(l_gradebook.getColumnId());
			log.info("Formula: " + l_column.getFormula());
			log.info("Item: " + i);
			log.trace("UserId: " + l_gradebook.getUserId());
			log.info("ColumnId: " + l_gradebook.getColumnId());
			log.info("Column Name: " + l_column.getName());
			log.info("Exempt: " + l_gradebook.isExempt());
			log.info("ChangeIndex: " + l_gradebook.getChangeIndex());
			if (l_gradebook.getDisplayGrade() != null) {
				log.info("Score: " + l_gradebook.getDisplayGrade().getScore());
				log.info("Possible: " + l_gradebook.getDisplayGrade().getPossible());
			}

			if (l_gradebook.getAvailablity() != null) {
				log.info("   Available: " + l_gradebook.getAvailablity().getAvailable());
			}
			log.info("");
		}
		return l_list;
	}

	private void getMembership() {
		log.trace("In getMembership()");
		MembershipResponseProxy l_returnJson = null;
		MembershipHandler l_membershipHandler = new MembershipHandler();
		l_returnJson = l_membershipHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), null, null);
		MembershipListProxy l_list = l_returnJson.getResults();
		PagingProxy l_page = l_returnJson.getPaging();
		for (int i = 0; i < l_list.size(); i++) {
			MembershipProxy l_membership = l_list.get(i);
			log.info("Id: " + l_membership.getId());
			log.info("UserId: " + l_membership.getUserId());
			UserProxy l_userResponse = null;
			UserHandler l_userHandler = new UserHandler();
			RequestData l_data = new RequestData();
			l_data.setUserId(l_membership.getUserId());
			l_userResponse = l_userHandler.getClientData2(m_configData.getRestHost(), m_token.getToken(), null, l_data);
			log.info("CourseId: " + l_membership.getCourseId());
			log.info("   Role " + l_membership.getCourseRoleId());
			log.info("UserId: " + l_userResponse.getUserName());
			if (l_membership.getAvailablity() != null) {
				log.info("   Available: " + l_membership.getAvailablity().getAvailable());
			}
		}
		while (l_page != null) {
			log.info("Paging: " + l_returnJson.getPaging().getNextPage());
			for (int i = 0; i < l_list.size(); i++) {
				MembershipProxy l_membership = l_list.get(i);
				log.info("Id: " + l_membership.getId());
				log.info("UserId: " + l_membership.getUserId());
				log.info("CourseId: " + l_membership.getCourseId());
				log.info("   Role " + l_membership.getCourseRoleId());
				if (l_membership.getAvailablity() != null) {
					log.info("   Available: " + l_membership.getAvailablity().getAvailable());
				}
			}

			// Now Iterate to next Page
			l_returnJson = l_membershipHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), l_page.getNextPage(), null);
			l_list = l_returnJson.getResults();
			l_page = l_returnJson.getPaging();
		}

	}

	private void getCourses() {
		log.trace("In getCourses()");
		CourseResponseProxy l_returnJson = null;
		CourseHandler l_courseHandler = new CourseHandler();
		l_returnJson = l_courseHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), null, null);
		CourseListProxy l_list = l_returnJson.getResults();
		PagingProxy l_page = l_returnJson.getPaging();
		while (l_page != null) {
			System.out.println("Paging: " + l_returnJson.getPaging().getNextPage());
			for (int i = 0; i < l_list.size(); i++) {
				CourseProxy l_course = l_list.get(i);
				log.info("ExternalId: " + l_course.getExternalId());
				log.info("CourseId: " + l_course.getCourseId());
				log.info("Description: " + l_course.getDescription());
				log.info("   Course Name: " + l_course.getName());
				if (l_course.getAvailability() != null) {
					log.info("   Available: " + l_course.getAvailability().getAvailable());
				}
			}

			// Now Iterate to next Page
			l_returnJson = l_courseHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), l_page.getNextPage(), null);
			l_list = l_returnJson.getResults();
			l_page = l_returnJson.getPaging();
		}

	}

	private void getUsers() {
		log.trace("In getUsers()");
		UserResponseProxy l_returnJson = null;
		UserHandler l_userHandler = new UserHandler();
		l_returnJson = l_userHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), null, null);
		UserListProxy l_list = l_returnJson.getResults();
		PagingProxy l_page = l_returnJson.getPaging();
		while (l_page != null) {
			System.out.println("Paging: " + l_returnJson.getPaging().getNextPage());
			for (int i = 0; i < l_list.size(); i++) {
				UserProxy l_user = l_list.get(i);
				log.info("Id: " + l_user.getId());
				log.info("UserName: " + l_user.getUserName());
				log.info("   First Name: " + l_user.getName().getGiven());
				log.info("   Last Name: " + l_user.getName().getFamily());
				if (l_user.getContact() != null) {
					System.out.println("   EMail: " + l_user.getContact().getEmail());
				}
				if (l_user.getAvailablity() != null) {
					log.info("   Available: " + l_user.getAvailablity().getAvailable());
				}
			}

			// Now Iterate to next Page
			l_returnJson = l_userHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), l_page.getNextPage(), null);
			l_list = l_returnJson.getResults();
			l_page = l_returnJson.getPaging();
		}
	}

	private void getTerms() {
		log.trace("In getTerms()");
		TermResponseProxy l_returnJson = null;
		TermHandler l_termHandler = new TermHandler();
		l_returnJson = l_termHandler.getClientData(m_configData.getRestHost(), m_token.getToken(), null, null);
		TermListProxy l_list = l_returnJson.getResults();
		PagingProxy l_page = l_returnJson.getPaging();
		if (l_page != null) {
			log.info("Paging: " + l_returnJson.getPaging().getNextPage());
		}
		for (int i = 0; i < l_list.size(); i++) {
			TermProxy l_term = l_list.get(i);
			log.info("Id: " + l_term.getId());
			log.info("Name: " + l_term.getName());
			log.info("Description: " + l_term.getDescription());
			log.info("Availability: " + l_term.getAvailablity().getAvailable());
			log.info("Duration: " + l_term.getAvailablity().getDuration().getType());
			log.info("Start: " + l_term.getAvailablity().getDuration().getStart());
			log.info("End: " + l_term.getAvailablity().getDuration().getEnd());
		}

	}

	private void authorize() {
		log.trace("In authorize()");
		Authorizer auth = new Authorizer();
		BBSchedulerService l_service = new BBSchedulerService();
		try {
			m_token = auth.authorize(l_service.getConfigData());
			log.info("Token: " + m_token.getToken());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
