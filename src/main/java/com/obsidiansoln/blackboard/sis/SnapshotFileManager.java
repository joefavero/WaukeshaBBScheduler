/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.blackboard.sis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.obsidiansoln.blackboard.model.DataSetStatus;
import com.obsidiansoln.blackboard.model.SnapshotJobDetails;
import com.obsidiansoln.database.model.ICStaff;
import com.obsidiansoln.database.model.ICUser;
import com.obsidiansoln.util.EmailManager;
import com.obsidiansoln.web.model.ConfigData;
import com.obsidiansoln.web.model.ContactModel;
import com.obsidiansoln.web.service.BBSchedulerService;

public class SnapshotFileManager {

	private static Logger mLog = LoggerFactory.getLogger(SnapshotFileManager.class);

	private BBSchedulerService m_service = null;

	public SnapshotFileManager() {
		m_service = new BBSchedulerService();
	}

	public String createFile(List<ICUser>l_students, List<ICStaff> l_staffs) {
		mLog.trace("In createFile() ...");

		// Create File;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-hhmmss");
		LocalDateTime now = LocalDateTime.now();
		String fileName = "SIS_PERSON_" + dtf.format(now) + ".txt";
		String l_snapshotFilename = null;

		FileWriter fileWriter = null;
		PrintWriter p = null;
		try {
			l_snapshotFilename = m_service.getConfigData().getWorkingDirectory() + "/" + fileName;
			fileWriter = new FileWriter(l_snapshotFilename);
			p = new PrintWriter(fileWriter);
			p.write("data_source_key|external_person_key|user_id|passwd|firstname|lastname|system_role|institution_role|row_status|student_id|middlename|new_external_person_key|company|email|street_1|street_2|gender|birthdate|title|city|state|zip_code|department|country|b_phone_1|b_phone_2|h_fax|b_fax|h_phone_1|h_phone_2|m_phone|job_title|available_ind|educ_level|webpage|locale");
			p.write("\r\n");

			for (ICUser l_student:l_students) {
				p.write("SIS.Student"+"|"						//data_source
						+ l_student.getStudentNumber() + "|"	//external_person_key
						+ l_student.getUserName() + "|" 		//user_id
						+ l_student.getStudentNumber() + "|" 	//passwd
						+ l_student.getFirstName() + "|"		//firstname
						+ l_student.getLastName() + "|"			//lastname
						+ "N" + "|"								//system_role
						+ "WTA" + "|"							//institution_role
						+ "enabled" + "|"						//row_status
						+ l_student.getStudentNumber() + "|"	//student_id
						+ l_student.getMiddleName() + "|"		//middlename
						+ "" + "|"                              //new_external_person_key
						+ "School District Of Waukesha - Student" + "|" //company
						+ l_student.getEmail1() + "|"			//email
						+ l_student.getAddress() + "|"			//street_1
						+ "" + "|"						        //street_2
						+ l_student.getGender() + "|"			//gender
						+ l_student.getBirthDate() + "|"		//birthdate
						+ "Student" + "|"			            //title
						+ l_student.getCity() + "|"			    //city
						+ l_student.getState() + "|"			//state
						+ l_student.getZip() + "|"			    //zip
						+ l_student.getDepartment() + "|"		//department
						+ l_student.getCountry() + "|"          //country
						+ "" + "|"			                    //b_phone_1
						+ "" + "|"			                    //b_phone_2
						+ "" + "|"			                    //h_fax
						+ "" + "|"			                    //b_fax
						+ l_student.getHomePhone1() + "|"			//h_phone_1
						+ l_student.getHomePhone2() + "|"			//h_phone_2
						+ l_student.getCellPhone() + "|"           //m_phone
						+ l_student.getJobTitle() + "|"           //job_title
						+ l_student.getAvailableInd() + "|"        //available_ind
						+ l_student.getGradeLevel() + "|"          //educ_level
						+ l_student.getWebpage() + "|"             //webpage
						+ l_student.getLocale());			        //locale
				p.write("\r\n");
			}

			HashMap<String, List<String>> l_institutionRoles = new HashMap<String, List<String>>();
			int i=0;
			for (ICStaff l_staff:l_staffs) {
				if (l_institutionRoles.get(l_staff.getPersonId()) == null) {
					i++;
					List<String> l_roles = new ArrayList<String>();
					l_roles.add(l_staff.getInstitutionRole());
					l_institutionRoles.put(l_staff.getPersonId(), l_roles);
					String l_role;
					if (l_staff.getRoleCount()==1) {
						l_role = translateRole(l_staff.getInstitutionRole());
					} else {
						l_role = "";
					}
					p.write("SIS.Staff"+"|"							//data_source
							+ l_staff.getStaffNumber() + "|"		//external_person_key
							+ l_staff.getUserName() + "|" 			//user_id
							+ "FaCP@$sW0RD4Bb!" + "|" 		        //passwd
							+ l_staff.getFirstName() + "|"			//firstname
							+ l_staff.getLastName() + "|"			//lastname
							+ "N" + "|"								//system_role
							+ l_role + "|"							//institution_role
							+ "enabled" + "|"						//row_status
							+ "" + "|"		    //student_id
							+ l_staff.getMiddleName() + "|"			//middlename
							+ "" + "|"                              //new_external_person_key
							+ "School District Of Waukesha - Faculty/Staff" + "|" //company
							+ l_staff.getEmail1() + "|"				//email
							+ "" + "|"				                //street_1
							+ "" + "|"						        //street_2
							+ l_staff.getGender() + "|"			    //gender
							+ l_staff.getBirthDate() + "|"			//birthdate
							+ l_staff.getTitle() + "|"			    //title
							+ "" + "|"			                    //city
							+ "" + "|"			                    //state
							+ "" + "|"			                    //zip
							+ l_staff.getDepartment() + "|"			//department
							+ l_staff.getCountry() + "|"             //country
							+ ""  + "|"			                    //b_phone_1
							+ "" + "|"			                    //b_phone_2
							+ "" + "|"			                    //h_fax
							+ "" + "|"			                    //b_fax
							+ "" + "|"			                    //h_phone_1
							+ "" + "|"			                    //h_phone_2
							+ "" + "|"                              //m_phone
							+ l_staff.getJobTitle() + "|"            //job_title
							+ l_staff.getAvailableInd() + "|"        //available_ind
							+ "" + "|"                              //educ_level
							+ l_staff.getWebpage() + "|"             //webpage
							+ l_staff.getLocale());			        //locale
					p.write("\r\n");
				} else {
					List<String> l_roles = l_institutionRoles.get(l_staff.getPersonId());
					l_roles.add(l_staff.getInstitutionRole());
					l_institutionRoles.put(l_staff.getPersonId(), l_roles);
				}
			}
			p.flush();
			p.close();
			fileWriter.close();

			for (HashMap.Entry<String, List<String>> entry : l_institutionRoles.entrySet()) {
				//mLog.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			}

		} catch (Exception l_ex) {
			mLog.error("Error: ", l_ex);
		} finally  {
			p.flush();
			p.close();
			try {
				fileWriter.close();
			} catch (IOException e) {
				mLog.error("Error: ", e);
			}
		}

		return l_snapshotFilename;
	}

	public DataSetStatus sendFile(String p_file) {
		mLog.trace("In sendFile() ...");

		DataSetStatus status = null;
		String l_message = null;
		try {
			// Create an object of credentialsProvider
			CredentialsProvider credentialsPovider = new BasicCredentialsProvider();

			ConfigData l_configData = m_service.getConfigData();

			// Set the credentials
			String host = l_configData.getRestHost().substring(l_configData.getRestHost().indexOf("//") + 2);
			int port = 80;
			if (l_configData.getRestHost().toLowerCase().contains("https")) {
				port = 443;
			}
			AuthScope scope = new AuthScope(host, port);

			mLog.info("Shared Username: " + l_configData.getSnapshotSharedUsername());
			mLog.info("Shared Password: " + l_configData.getSnapshotSharedPassword());
			Credentials credentials = new UsernamePasswordCredentials(l_configData.getSnapshotSharedUsername(),
					l_configData.getSnapshotSharedPassword());
			credentialsPovider.setCredentials(scope, credentials);

			// Creating the HttpClientBuilder
			HttpClientBuilder clientbuilder = HttpClients.custom();

			// Setting the credentials
			clientbuilder = clientbuilder.setDefaultCredentialsProvider(credentialsPovider);

			// Building the CloseableHttpClient object
			CloseableHttpClient httpclient = clientbuilder.build();

			if (httpclient != null) {

				// Parse the Filename to determine the endpoints
				//SnapshotJobDetails details = SnapshotJobDetails.fromFileName(p_file);
				SnapshotJobDetails details = new SnapshotJobDetails();
				details.setEndpoint("person");
				details.setOperation("refresh");
				details.setJobTitle("Infinite Campus Integration");
				mLog.info("Endpoint: " + details.getEndpoint());
				mLog.info("Operation: " + details.getOperation());
				mLog.info("Job Title: " + details.getJobTitle());
				mLog.info("Timestamp: " + details.getTimestamp());

				try {
					HttpPost httppost = new HttpPost(l_configData.getRestHost()
							+ "/webapps/bb-data-integration-flatfile-" + l_configData.getSnapshotBbInstanceId()
							+ "/endpoint/" + details.getEndpoint() + "/" + details.getOperation());
					FileEntity tmp = new FileEntity(new File(p_file), ContentType.create("text/plain", "UTF-8"));
					httppost.setEntity(tmp);
					mLog.info("executing request " + details.getOperation());
					mLog.info("executing request " + httppost.getRequestLine());
					CloseableHttpResponse response = httpclient.execute(httppost);
					try {
						if (response.getStatusLine().getStatusCode() == 200) {
							mLog.info("----------------------------------------");
							mLog.info("Status Line: " + response.getStatusLine());
							HttpEntity resEntity = response.getEntity();
							if (resEntity != null) {
								InputStream data = resEntity.getContent();
								byte[] text = data.readAllBytes();
								String s = new String(text, StandardCharsets.UTF_8);
								if (s.contains("code") && s.contains("to")) {
									String code = s.substring(s.indexOf("code") + 4, s.indexOf("to"));
									code = code.trim();
									mLog.info("Code: " + code);

									String resultURL = l_configData.getRestHost()
											+ "/webapps/bb-data-integration-flatfile-"
											+ l_configData.getSnapshotBbInstanceId() + "/endpoint/dataSetStatus/"
											+ code;

									HttpPost httppost1 = new HttpPost(resultURL);
									CloseableHttpResponse response1 = httpclient.execute(httppost1);
									mLog.info("Code: " + code);
									HttpEntity resEntity1 = response1.getEntity();
									if (response1.getStatusLine().getStatusCode() == 200) {

										do {
											response1 = httpclient.execute(httppost1);
											resEntity1 = response1.getEntity();
											mLog.info("Status Line: " + response1.getStatusLine());

											InputStream data1 = resEntity1.getContent();
											text = data1.readAllBytes();
											s = new String(text, StandardCharsets.UTF_8);
											XmlMapper xmlMapper = new XmlMapper();
											status = xmlMapper.readValue(s, DataSetStatus.class);
											mLog.info("Code: " + s);
											mLog.info("Completed Count: " + status.getCompletedCount());
											mLog.info("Error Count: " + status.getErrorCount());
											mLog.info("Queued Count: " + status.getQueuedCount());
											mLog.info("Warning Count: " + status.getWarningCount());
											Thread.sleep(3000);
										} while (status.getQueuedCount() > 0);

										l_message = "Snapshot Integration Completed" + "<br>"
												+ "    File Processsed: " + p_file + "<br>"
												+ "    Completed Count: " + status.getCompletedCount() + "<br>"
												+ "    Error Count: " + status.getErrorCount() + "<br>"
												+ "    Queued Count: " + status.getQueuedCount() + "<br>"
												+ "    Warning Count: " + status.getWarningCount() + "<br>"
												+ "    BB Learn URL: " + resultURL + "<br>";
									} else {
										l_message = response1.getStatusLine().getReasonPhrase();
									}
								} else {
									mLog.info("Unable to processs Status Response");
								}
							}

							EntityUtils.consume(resEntity);
						} else {
							l_message = "Snapshot Integration Error for File: " + p_file + "<br>"
									+ "    HTTP Status Code: " + response.getStatusLine().getStatusCode() + "<br>";
						}
					} catch(Exception ex) {
						mLog.error("Error: ", ex);
					} finally {
						response.close();
					}
				} catch(Exception ex) {
					mLog.error("Error: ", ex);
				} finally {
					httpclient.close();
				}
			} else {
				l_message = "Snapshot Integration Error for File: " + p_file + "<br>"
						+ "    File Format Incorrect: " + "<br>";

			}

			// Send Email
			sendEmail(l_configData.getSnapshotEmail(), "Infinite Campus Integration", l_message);
		} catch (Exception l_ex) {
			mLog.error("ERROR", l_ex);
		}
		return status;
	}

	private void sendEmail(String p_email, String p_subject, String p_message) {
		mLog.trace("In sendEmail() ...");
		// Now Send Email
		try {
			mLog.info("Sending Email: ");
			EmailManager l_email = new EmailManager();
			ContactModel l_contact = new ContactModel();
			l_contact.setEmail(p_email);
			l_contact.setSubject(p_subject);
			l_contact.setMessage(p_message);
			l_email.sendEmail("joefavero@gmail.com", p_email, l_contact);
		} catch (Exception l_ex) {
			mLog.error("ERROR", l_ex);
		}
	}

	private String translateRole(String p_role) {
		String translatedRole;
		switch (p_role) {
		case "0450":
			translatedRole = "AHP";
			break;
		case "0020":
			translatedRole = "BAN";
			break;
		case "0060":
			translatedRole = "BET";
			break;
		case "0100":
			translatedRole = "BUT";
			break;
		case "0470":
			translatedRole = "eAchieve";
			break;
		case "0150":
			translatedRole = "eAchieve";
			break;
		case "0490":
			translatedRole = "EAS";
			break;
		case "0160":
			translatedRole = "HAD";
			break;
		case "0140":
			translatedRole = "HAW";
			break;
		case "0190":
			translatedRole = "HEY";
			break;
		case "0200":
			translatedRole = "HIL";
			break;
		case "0210":
			translatedRole = "HOR";
			break;
		case "0120":
			translatedRole = "LES";
			break;
		case "0260":
			translatedRole = "LOW";
			break;
		case "0280":
			translatedRole = "MEA";
			break;
		case "0290":
			translatedRole = "NOR";
			break;
		case "0360":
			translatedRole = "PRA";
			break;
		case "0410":
			translatedRole = "ROS";
			break;
		case "0460":
			translatedRole = "SOU";
			break;
		case "0480":
			translatedRole = "SUM";
			break;
		case "0430":
			translatedRole = "EPA";
			break;
		case "0400":
			translatedRole = "STS";
			break;
		case "0131":
			translatedRole = "STE";
			break;
		case "0560":
			translatedRole = "WES";
			break;
		case "0110":
			translatedRole = "WRC";
			break;
		case "0520":
			translatedRole = "WRS";
			break;
		case "0415":
			translatedRole = "WTA";
			break;
		default:
			translatedRole = "SDW";
			break;
		}
		return translatedRole;
	}
}
