/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard;

public abstract class RestConstants {

	public final static String AUTH_PATH = "/learn/api/public/v1/oauth2/token";
	
	public final static String DATASOURCE_PATH = "/learn/api/public/v1/dataSources";

	
	public final static String TERM_PATH = "/learn/api/public/v1/terms";
	public final static String TERM_AVAILABLE = "Yes";

    
	public final static String COURSE_PATH = "/learn/api/public/v3/courses/";
	public final static String COURSE_PATH_V2 = "/learn/api/public/v2/courses/";
	public final static String COURSE_PATH_V1 = "/learn/api/public/v2/courses/";
	public final static String COURSE_COURSENAME_PARAMETER = "/courseId:";	
	public final static String COURSE_CATEGORY_PARAMETER = "/categories";
	public final static String COURSE_GROUP = "/groups/";	
	public final static String COURSE_USER = "/users/";
	public final static String COURSE_UUID_PARAMETER = "/uuid:";
	public final static String COURSE_COPY = "/copy";	
	public final static String COURSE_AVAILABLE = "Yes";
	public final static String COURSE_COURSEID_QUERY = "?courseId=";
	public final static String COURSE_GROUP_SET_PATH = "/groups";

	public final static String CATEGORY_PATH = "/learn/api/public/v1/catalog/categories/";
	public final static String CATEGORY_COURSE_TYPE = "Course/";	
	public final static String CATEGORY_ORGANZATION_TYPE = "Organization/";	


	
	public final static String MEMBERSHIP_PATH = "/learn/api/public/v1";
	public final static String MEMBERSHIP_USER_EXTENSION = "/users/";
	public final static String MEMBERSHIP_COURSE_EXTENSION = "/courses/";
	public final static String MEMBERSHIP_COURSENAME_PARAMETER = "/courseId:";
	public final static String MEMBERSHIP_USERNAME_PARAMETER = "userName:";	
	public final static String MEMBERSHIP_STUDENT_ROLE_PARAMETER = "?role=Student";
	public final static String MEMBERSHIP_INSTRUCTOR_ROLE_PARAMETER = "?role=Instructor";
	public final static String MEMBERSHIP_AVAILABLE = "Yes";
	
	public final static String GRADEBOOK_PATH = "/learn/api/public/v2/courses/";
	public final static String GRADEBOOK_PATH_V1 = "/learn/api/public/v1/courses/";
	public final static String GRADEBOOK_USERS = "/gradebook/users/";
	public final static String GRADEBOOK_USERNAME_PARAMETER = "userName:";	
	public final static String GRADEBOOK_COURSENAME_PARAMETER = "courseId:";	
	public final static String GRADEBOOK_COLUMNS = "/gradebook/columns/";
	public final static String GRADEBOOK_SCHEMAS = "/gradebook/schemas/";
	public final static String GRADEBOOK_ATTEMPTS = "/attempts";
	public final static String GRADEBOOK_ATTEMPTS_STATUS_PARAMETER = "?attemptStatuses=Completed";
	public final static String GRADEBOOK_ATTEMPT_DATE_COMPARE_PARAMETER = "&attemptDateCompare=greaterOrEqual&attemptDate=";
	public final static String GRADEBOOK_COURSE_ID = "_82872_1";
	public final static String GRADEBOOK_USER_ID = "_242333_1";
	public final static String GRADEBOOK_COLUMN_ID = "/_1539055_1";

	public final static String USER_PATH = "/learn/api/public/v1/users";
	public final static String USER_AVAILABLE = "Yes";
	public final static String USER_USERNAME_PARAMETER = "/userName:";	
	public final static String USER_EXTERNALID_PARAMETER = "/externalId:";	
	public final static String USER_UUID_PARAMETER = "/uuid:";	
	public final static String USER_FAMILY_NAME_PARAMETER = "?name.family=";	
	public final static String USER_DATA_SOURCE_PARAMETER = "?dataSourceId=";	
	public final static String USER_ID = "mmouse:";	
	
	public final static String PARENT_PATH = "/learn/api/public/v1/users";
	public final static String PARENT_OBSERVERS = "/observers";
	public final static String PARENT_OBSERVEES = "/observees";

	}
