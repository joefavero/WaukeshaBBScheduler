/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.util;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;



public class BBSchedulerUtil extends AppUtil {

	public static final String applicationHandle = "ProgressReport";
	public static final String propertiesFilename = applicationHandle + ".properties";
    public static final String LOG_LEVEL = "LOG_LEVEL";
    public static final String WORKING_DIRECTORY = "WORKING_DIRECTORY";
    public static final String API_KEY = "API_KEY";
    public static final String LTI_KEY = "LTI_KEY";
    public static final String LTI_SECRET = "LTI_SECRET";
    public static final String REST_HOST = "REST_HOST";
    public static final String REST_KEY = "REST_KEY";
    public static final String REST_SECRET = "REST_SECRET";
	public static final String SEMESTER1_START_DATE = "SEMESTER1_START_DATE";
	public static final String SEMESTER1_END_DATE = "SEMESTER1_END_DATE";
	public static final String SEMESTER2_START_DATE = "SEMESTER2_START_DATE";
	public static final String SEMESTER2_END_DATE = "SEMESTER2_END_DATE";
	public static final String DEFAULT_SEMESTER = "DEFAULT_SEMESTER";
	public static final String DEFAULT_TERM = "DEFAULT_TERM";
	public static final String MAX_REPORT_DAYS = "MAX_REPORT_DAYS";
	public static final String MAX_PDF_ENTRIES = "MAX_PDF_ENTRIES";
	public static final String ADMIN_REPORT_INSTRUCTOR = "ADMIN_REPORT_INSTRUCTOR";
	public static final String ADMIN_REPORT_EMAIL = "ADMIN_REPORT_EMAIL";
	public static final String ADMIN_REPORT_PHONE = "ADMIN_REPORT_PHONE";
	public static final String PARENT_TOOL_LEVEL = "PARENT_TOOL_LEVEL";
	public static final String PARENT_EMAIL_FROM = "PARENT_EMAIL_FROM";
	public static final String EMAIL_HOST = "EMAIL_HOST";
	public static final String EMAIL_PORT = "EMAIL_PORT";
	public static final String EMAIL_USERNAME = "EMAIL_USERNAME";
	public static final String EMAIL_PASSWORD = "EMAIL_PASSWORD";
	public static final String EMAIL_AUTHENTICATE = "EMAIL_AUTHENTICATE";
	public static final String EMAIL_USE_SSL = "EMAIL_USE_SSL";
	public static final String EMAIL_DEBUG = "EMAIL_DEBUG";
	public static final String EMAIL_NOTE = "EMAIL_NOTE";
	public static final String PARENT_INVITE_MESSAGE = "PARENT_INVITE_MESSAGE";
	public static final String PARENT_EXISTING_MESSAGE = "PARENT_EXISTING_MESSAGE";
	public static final String SNAPSHOT_BB_INSTANCE_ID = "SNAPSHOT_BB_INSTANCE_ID";
	public static final String SNAPSHOT_SHARED_USERNAME = "SNAPSHOT_SHARED_USERNAME";
	public static final String SNAPSHOT_SHARED_PASSWORD = "SNAPSHOT_SHARED_PASSWORD";
	public static final String SNAPSHOT_EMAIL = "SNAPSHOT_EMAIL";
	public static final String OPERATOR_CONTAINS = "cont";
	public static final String OPERATOR_EQ = "eq";
	public static final String OPERATOR_BEGIN = "begin";
	public static final String OPERATOR_NOT_BLANK = "notblank";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_DESCRIPTION = "description";
	public static final String ATTRIBUTE_INSTRUCTOR = "instructor";

	//outputs
	public static final String YES = "Y";
	public static final String NO = "N";
	public static final String NULL = "(!)";

	//messages
	public static final String NO_VALUE = "config.no.value";
	public static final String NOT_EXISTS = "config.not.exists";
	public static final String NOT_NUMERIC = "config.not.numeric";
	private static Logger mLog = LoggerFactory.getLogger(BBSchedulerUtil.class);
	private static String RESOURCE_FILE = "customreport.xml";

	public static Map<String, Object> getResources() {
		return getResources(RESOURCE_FILE);
	}



	public static String quoteString(String p_list, boolean toUpperCase, Locale locale) {
		StringBuffer l_quotedString = new StringBuffer();
		l_quotedString.append("'");
		boolean l_begSeq = false;
		for (int i = 0; i < p_list.length(); i++) {
			if (p_list.charAt(i) == ',') {
				l_quotedString.append("','");
				l_begSeq = true;
			} else {
				if (!l_begSeq || (l_begSeq && p_list.charAt(i) != ' ')) {
					if (toUpperCase) {
						l_quotedString.append(String.valueOf(p_list.charAt(i)).toUpperCase(locale));
					} else {
						l_quotedString.append(p_list.charAt(i));
					}
					l_begSeq = false;
				}
			}
		}
		l_quotedString.append("'");
		return l_quotedString.toString();
	}

	public static Map<String, String> getCourseOperators(ReloadableResourceBundleMessageSource messageSource, Locale locale) {
		Map<String, String> retMap = new LinkedHashMap<String, String>();
		retMap.put(OPERATOR_CONTAINS, messageSource.getMessage("operator.contains", null, locale));
		retMap.put(OPERATOR_EQ, messageSource.getMessage("operator.equals", null, locale));
		retMap.put(OPERATOR_BEGIN, messageSource.getMessage("operator.begins", null, locale));

		return retMap;
	}

	public static Map<String, String> getCourseAttributes(ReloadableResourceBundleMessageSource messageSource, Locale locale) {
		Map<String, String> retMap = new LinkedHashMap<String, String>();
		retMap.put(ATTRIBUTE_ID, messageSource.getMessage("attribute.id", null, locale));
		retMap.put(ATTRIBUTE_NAME, messageSource.getMessage("attribute.name", null, locale));
		retMap.put(ATTRIBUTE_DESCRIPTION, messageSource.getMessage("attribute.description", null, locale));
		retMap.put(ATTRIBUTE_INSTRUCTOR, messageSource.getMessage("attribute.instructor", null, locale));
		return retMap;
	}


}
