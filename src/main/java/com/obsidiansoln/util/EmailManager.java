/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
 */
package com.obsidiansoln.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.obsidiansoln.web.model.ConfigData;
import com.obsidiansoln.web.model.ContactModel;
import com.obsidiansoln.web.service.BBSchedulerService;

public class EmailManager {

	
	private static final Logger log = LoggerFactory.getLogger(EmailManager.class);
	private BBSchedulerService m_service = null;
	private ConfigData m_configData = null;
	private Session m_session = null;
	private String m_username = null;
	private String m_password = null;

	public EmailManager() {
		log.trace("In EmailManager Contructor");
		m_service = new BBSchedulerService();
		try {
			m_service = new BBSchedulerService();
			log.debug("Processing Config Data...");
			m_configData = m_service.getConfigData();

			log.debug("Using File Mail Config");
			log.info("SMTP Server Host: " + m_configData.getEmailHost());
			log.info("SMTP Server Port: " + m_configData.getEmailPort());
			log.info("SMTP Server Authenticate: " + m_configData.isEmailAuthenticate());
			log.info("SMTP Email Debug: " + m_configData.isEmailDebug());
			Properties mailprops = new Properties();
			mailprops.setProperty("mail.smtp.host", m_configData.getEmailHost());
			mailprops.setProperty("mail.smtp.port", m_configData.getEmailPort());
			if (m_configData.isEmailDebug()) {
				mailprops.setProperty("mail.debug", "true");
			} else {
				mailprops.setProperty("mail.debug", "false");
			}

			if (m_configData.isEmailAuthenticate()) {
				m_username = m_configData.getEmailUsername();
				m_password = m_configData.getEmailPassword();
				mailprops.put("mail.smtp.auth", "true");
			}

			if (m_configData.isEmailUseSSL()) {
				log.debug("Configuring SSL");
				mailprops.put("mail.smtp.ssl.checkserveridentity", "false");
				mailprops.put("mail.smtp.ssl.trust", "*");
				mailprops.put("mail.smtp.ssl.enable", "true");
				mailprops.put("mail.smtp.socketFactory.port", "465");
				mailprops.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				mailprops.put("mail.smtp.ssl.protocols", "TLSv1.2");

				log.debug("Setting Session for TLS");
				// log.info("Username: " + m_username);
				// log.info("Password: " + m_password);

				// creating Session instance referenced to
				// Authenticator object to pass in
				// Session.getInstance argument
				m_session = Session.getInstance(mailprops, new javax.mail.Authenticator() {

					// override the getPasswordAuthentication method
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(m_username, m_password);
					}
				});
			} else {
				m_session = Session.getDefaultInstance(mailprops, null);
			}
		} catch (Exception e) {
			log.error("ERROR: ", e);
		}
	}

	public void sendEmail(String fromEmail, String p_toEmail, ContactModel contactModel) {
		log.trace("In sendEmail Processing ..");

		if (m_session != null) {
			try {
				MimeMessage msg = new MimeMessage(m_session);

				// Set From: header field of the header.
				msg.setFrom(new InternetAddress(fromEmail));

				log.debug(p_toEmail);
				// Set To: header field of the header.
				msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(p_toEmail));

				// Set Subject: header field
				msg.setSubject(contactModel.getSubject());

				String body = null;
				if (contactModel.getAttachement() == null) {
					body = contactModel.getMessage();
				} else {
					// Now set the actual message
					body = "<h1>A new message from the portal has been generated</h1>";
					body += "<br>";
					body += "From: " + contactModel.getEmail();
					body += "<br>";
					body += "Message: ";
					body += "<br>";
					body += contactModel.getMessage();
					body += "<br>";
					body += "<br>";
					String url = contactModel.getAttachement();
					if (url != null) {
						body += "<h2>Select the Link Below to download the results file</h2>";
						body += "<br>";
						String content = "<a href='" + url + "'>" + url + "</a>";
						body += content;
					}
					body += "<br>";
					body += "<i>" + contactModel.getNote() + "</i>";
				}

				msg.setContent(body, "text/html");

				log.info("Message is ready");
				Transport.send(msg);
				log.info("Message is sent");

				log.info("EMail Sent Successfully!!");
			} catch (Exception e) {
				log.info("An exception occurred sending email", e);
			}
		}
	}
}
