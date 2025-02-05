/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import jakarta.servlet.http.HttpServletRequest;

public class PropertiesBean implements Serializable {

	private static final long serialVersionUID = -1L;

	private static Logger mLog = LoggerFactory.getLogger(PropertiesBean.class);

	private static final String REQUEST_ATTRB_EXCEPTION = "exception";

	private static PropertiesBean propertiesBean;

	private Properties p;

	public PropertiesBean() {
		super();
	}

	public void setProperties(HttpServletRequest request) throws Exception {
		Enumeration<?> pNames = request.getParameterNames();
		FileOutputStream fos = null;
		try {
			URL url = this.getClass().getResource("/application.properties");
			mLog.info(url.getPath());
		    File fileObject = new File(url.toURI());
			fos = new FileOutputStream(fileObject);
			Properties prop = new Properties();

			while (pNames.hasMoreElements()) {
				String name = (String) pNames.nextElement();
				String value = request.getParameter(name);
				mLog.debug("Module property name: " + name);
				mLog.debug("Module property value: " + value);
				prop.setProperty(name, value);
			}
			prop.store(fos, "# Properties File");

		} catch (Exception e) {
			mLog.error("Error writing the Module properties file: ", e);

			request.setAttribute(REQUEST_ATTRB_EXCEPTION, e.getMessage());
			throw e;
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	public void writeProperties(Properties props) throws Exception {

		OutputStream outputstream = null;
		try {
			URL url = this.getClass().getResource("/application.properties");
			mLog.info(url.getPath());
		    File fileObject = new File(url.toURI());
			outputstream = new FileOutputStream(fileObject);
			props.store(outputstream, "# Properties File");
		} catch (Exception e) {
			mLog.error("Error writing the Module properties file: ", e);
		} finally {
			if (outputstream != null) {
				outputstream.close();
			}
		}
	}

	public static PropertiesBean getInstance() {
		if (propertiesBean == null) {
			propertiesBean = new PropertiesBean();
		}
		return propertiesBean;
	}

	public Properties getProperties() throws Exception {
		InputStream l_propStream = null;
		Properties props = null;
		try {
			props = new Properties();
			l_propStream = this.getClass().getClassLoader().getResourceAsStream("/application.properties");
			props.load(l_propStream);
		} catch (Exception e) {
			mLog.error("Error reading the Module properties file: ", e);
			props = new Properties();
		} finally {
			if (l_propStream != null) {
				l_propStream.close();
			}
		}
		p = props;
		return props;
	}

	public String getProperty(String propertyName) {
		String prop = "";
		try {
			Properties p = this.getProperties();
			prop = p.getProperty(propertyName);
		} catch (Exception e) {
			mLog.error("Error reading an Module property. Stacktrace follows:", e);
			prop = "";
		}
		return prop;
	}

	public void setProperty(String key, String value) throws Exception {
		p.setProperty(key, value);
		this.writeProperties(p);
	}

	/**
	 * method to set the logging level according to the leglevel specified by the
	 * user
	 *
	 * @param logger
	 * @param level
	 * @return
	 */

	public void setLoggingLevel(ch.qos.logback.classic.Logger logger, String level) {
		if (level != null)
			level = level.toLowerCase();
		if ("debug".equals(level))
			logger.setLevel(Level.DEBUG);
		else if ("info".equals(level))
			logger.setLevel(Level.INFO);
		else if ("warn".equals(level))
			logger.setLevel(Level.WARN);
		else
			logger.setLevel(Level.ERROR);
	}

}
