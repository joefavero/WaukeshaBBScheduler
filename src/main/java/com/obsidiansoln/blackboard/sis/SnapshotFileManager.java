/*
 * Copyright 2024-2026 Obsidian Solution LLC
 * Consulting work for Waukesha BB Scheduler
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

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.obsidiansoln.blackboard.model.DataSetStatus;
import com.obsidiansoln.blackboard.model.SnapshotFileInfo;
import com.obsidiansoln.blackboard.model.SnapshotJobDetails;
import com.obsidiansoln.database.model.ICBBEnrollment;
import com.obsidiansoln.database.model.ICGuardian;
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

	public List<SnapshotFileInfo> createStudentFile(List<ICUser>p_students) {
		mLog.trace("In createStudentFile() ...");

		List<SnapshotFileInfo> l_fileList = new ArrayList<SnapshotFileInfo>();

		// Create File;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-hhmmss");
		LocalDateTime now = LocalDateTime.now();
		String fileName = "SIS_PERSON_STUDENT_" + dtf.format(now) + ".txt";
		String l_snapshotFilename = null;

		FileWriter fileWriter = null;
		PrintWriter p = null;
		try {
			// First De-Dedup Records based on Service Type
			HashMap<String, ICUser> l_studentRecords = new HashMap<String, ICUser>();
			for (ICUser l_student:p_students) {
				if (l_studentRecords.get(l_student.getStudentNumber()) == null) {
					l_studentRecords.put(l_student.getStudentNumber(), l_student);
				} else {
					ICUser l_temp = l_studentRecords.get(l_student.getStudentNumber());
					if (l_temp.getServiceType().equals("S") && l_student.getServiceType().equals("P")) {
						l_studentRecords.put(l_student.getStudentNumber(), l_student);
					} else if (l_temp.getServiceType().equals("N") && (l_student.getServiceType().equals("P") || l_student.getServiceType().equals("S"))) {
						l_studentRecords.put(l_student.getStudentNumber(), l_student);
					}
				}
			}
			mLog.info("Number of Student Records: " + l_studentRecords.size());
			l_snapshotFilename = m_service.getConfigData().getWorkingDirectory() + "/" + fileName;
			fileWriter = new FileWriter(l_snapshotFilename);
			p = new PrintWriter(fileWriter);
			p.write("data_source_key|external_person_key|user_id|passwd|firstname|lastname|system_role|institution_role|row_status|student_id|middlename|new_external_person_key|company|email|street_1|street_2|gender|birthdate|title|city|state|zip_code|department|country|b_phone_1|b_phone_2|h_fax|b_fax|h_phone_1|h_phone_2|m_phone|job_title|available_ind|educ_level|webpage|locale");
			p.write("\r\n");

			HashMap<String, String> l_institutionRoles = new HashMap<String, String>();
			int i=0;
			for (HashMap.Entry<String, ICUser> l_students : l_studentRecords.entrySet()) {
				ICUser l_student = l_students.getValue();
				if (l_institutionRoles.get(l_student.getStudentNumber()) == null) {
					i++;
					String l_role = "Student-SDW";
					if (l_student.getInstitutionRole().equals("EAC") || l_student.getInstitutionRole().equals("EAE")) {
						l_role = "Student-eA";
					}
					l_institutionRoles.put(l_student.getStudentNumber(), l_role);
					p.write(m_service.getConfigData().getSnapshotStudentDatasource() +"|"						//data_source
							+ l_student.getStudentNumber() + "|"	//external_person_key
							+ l_student.getUserName() + "|" 		//user_id
							+ l_student.getStudentNumber() + "|" 	//passwd
							+ l_student.getFirstName() + "|"		//firstname
							+ l_student.getLastName() + "|"			//lastname
							+ "N" + "|"								//system_role
							+ l_student.getInstitutionRole() + "|"	//institution_role
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
				} else {
					String l_role = "Student-SDW";
					if (l_student.getInstitutionRole().equals("EAC") || l_student.getInstitutionRole().equals("EAE")) {
						l_role = "Student-eA";
					}
					l_institutionRoles.put(l_student.getStudentNumber(), l_role);
				}
			}

			SnapshotFileInfo l_studentFile = new SnapshotFileInfo();
			l_studentFile.setFileName(l_snapshotFilename);
			l_studentFile.setEndpoint("person");
			l_studentFile.setType(1);
			l_studentFile.setNumberOfRecords(l_studentRecords.size());
			l_fileList.add(l_studentFile);

			p.flush();
			p.close();
			fileWriter.close();

			// Create File;
			now = LocalDateTime.now();
			fileName = "SIS_STUDENT_ASSOCIATION_" + dtf.format(now) + ".txt";
			l_snapshotFilename = m_service.getConfigData().getWorkingDirectory() + "/" + fileName;
			fileWriter = new FileWriter(l_snapshotFilename);
			p = new PrintWriter(fileWriter);
			p.write("data_source_key|external_person_key|role_id");
			p.write("\r\n");
			int j=0;
			for (HashMap.Entry<String, String> l_role : l_institutionRoles.entrySet()) {
				String l_studentRole = l_role.getValue();

				j++;
				p.write(m_service.getConfigData().getSnapshotStudentAssociationDatasource() + "|"	 //data_source
						+ l_role.getKey() + "|"                                //external_person_key
						+ l_studentRole);                                          //role_id
				p.write("\r\n");
			}
			p.flush();
			p.close();
			fileWriter.close();

			SnapshotFileInfo l_studentAssociationFile = new SnapshotFileInfo();
			l_studentAssociationFile.setFileName(l_snapshotFilename);
			l_studentAssociationFile.setEndpoint("secondaryinstrole");
			l_studentAssociationFile.setType(1);
			l_studentAssociationFile.setNumberOfRecords(j);
			l_fileList.add(l_studentAssociationFile);

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

		return l_fileList;
	}

	public List<SnapshotFileInfo> createStaffFile(List<ICStaff> l_staffs) {
		mLog.trace("In createStaffFile() ...");

		List<SnapshotFileInfo> l_fileList = new ArrayList<SnapshotFileInfo>();

		// Create File;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-hhmmss");
		LocalDateTime now = LocalDateTime.now();
		String fileName = "SIS_PERSON_STAFF_" + dtf.format(now) + ".txt";
		String l_snapshotFilename = null;

		FileWriter fileWriter = null;
		PrintWriter p = null;
		try {
			l_snapshotFilename = m_service.getConfigData().getWorkingDirectory() + "/" + fileName;
			fileWriter = new FileWriter(l_snapshotFilename);
			p = new PrintWriter(fileWriter);
			p.write("data_source_key|external_person_key|user_id|passwd|firstname|lastname|system_role|institution_role|row_status|student_id|middlename|new_external_person_key|company|email|street_1|street_2|gender|birthdate|title|city|state|zip_code|department|country|b_phone_1|b_phone_2|h_fax|b_fax|h_phone_1|h_phone_2|m_phone|job_title|available_ind|educ_level|webpage|locale");
			p.write("\r\n");

			HashMap<String, String> l_institutionRoles = new HashMap<String, String>();
			int i=0;
			for (ICStaff l_staff:l_staffs) {
				if (l_institutionRoles.get(l_staff.getStaffNumber()) == null) {
					i++;
					String l_roles = "Faculty-SDW";
					if (translateRole(l_staff.getInstitutionRole()).equals("eAchieve")) {
						l_roles = "Faculty-eA";
					}
					l_institutionRoles.put(l_staff.getStaffNumber(), l_roles);
					String l_role;
					if (l_staff.getRoleCount()==1) {
						l_role = translateRole(l_staff.getInstitutionRole());
					} else {
						l_role = "";
					}
					p.write(m_service.getConfigData().getSnapshotStaffDatasource()+"|"							//data_source
							+ l_staff.getStaffNumber() + "|"		//external_person_key
							+ l_staff.getUserName() + "|" 			//user_id
							+ "FaCP@$sW0RD4Bb!" + "|" 		        //passwd
							+ l_staff.getFirstName() + "|"			//firstname
							+ l_staff.getLastName() + "|"			//lastname
							+ "SELFMANAGER" + "|"				    //system_role
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
					String l_roles = l_institutionRoles.get(l_staff.getStaffNumber());
					if (translateRole(l_staff.getInstitutionRole()).equals("eAchieve")) {
						l_roles = "Faculty-eA";
					}
					l_institutionRoles.put(l_staff.getStaffNumber(), l_roles);
				}
			}
			p.flush();
			p.close();
			fileWriter.close();

			SnapshotFileInfo l_staffFile = new SnapshotFileInfo();
			l_staffFile.setFileName(l_snapshotFilename);
			l_staffFile.setEndpoint("person");
			l_staffFile.setType(2);
			l_staffFile.setNumberOfRecords(i);
			l_fileList.add(l_staffFile);

			// Now Create the Staff Association file or Institution Roles

			// Create File;
			now = LocalDateTime.now();
			fileName = "SIS_STAFF_ASSOCIATION_" + dtf.format(now) + ".txt";
			l_snapshotFilename = m_service.getConfigData().getWorkingDirectory() + "/" + fileName;
			fileWriter = new FileWriter(l_snapshotFilename);
			p = new PrintWriter(fileWriter);
			p.write("data_source_key|external_person_key|role_id");
			p.write("\r\n");
			int j=0;
			for (HashMap.Entry<String, String> l_roles : l_institutionRoles.entrySet()) {
				String l_staffRole = l_roles.getValue();
				j++;
				p.write(m_service.getConfigData().getSnapshotStaffAssociationDatasource() + "|"	 //data_source
						+ l_roles.getKey() + "|"                                //external_person_key
						+ l_staffRole);                                          //role_id
				p.write("\r\n");

			}
			p.flush();
			p.close();
			fileWriter.close();

			SnapshotFileInfo l_staffAssociationFile = new SnapshotFileInfo();
			l_staffAssociationFile.setFileName(l_snapshotFilename);
			l_staffAssociationFile.setEndpoint("secondaryinstrole");
			l_staffAssociationFile.setType(2);
			l_staffAssociationFile.setNumberOfRecords(j);
			l_fileList.add(l_staffAssociationFile);


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

		return l_fileList;
	}

	public List<SnapshotFileInfo> createEnrollmentFile(List<ICBBEnrollment>p_enrollments) {
		mLog.trace("In createEnrollmentFile() ...");

		List<SnapshotFileInfo> l_fileList = new ArrayList<SnapshotFileInfo>();

		// Create File;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-hhmmss");
		LocalDateTime now = LocalDateTime.now();
		String fileName = "SIS_CLASS_ASSOCIATION_STUDENTS_" + dtf.format(now) + ".txt";
		String l_snapshotFilename = null;

		FileWriter fileWriter = null;
		PrintWriter p = null;
		try {
			l_snapshotFilename = m_service.getConfigData().getWorkingDirectory() + "/" + fileName;
			fileWriter = new FileWriter(l_snapshotFilename);
			p = new PrintWriter(fileWriter);
			p.write("data_source_key|external_course_key|external_person_key|role|row_status|available_ind");
			p.write("\r\n");

			for (ICBBEnrollment l_enrollment:p_enrollments) {
				p.write(m_service.getConfigData().getSnapshotEnrollmentDatasource()+"|"					//data_source
						+ l_enrollment.getCourseId() + "|"		//external_course_key
						+ l_enrollment.getStudentNumber() + "|" //external_person_key
						+ l_enrollment.getRole() + "|" 			//role
						+ l_enrollment.getRowStatus() + "|"		//row_status
						+ l_enrollment.getAvailableInd());		//available_ind

				p.write("\r\n");
			}

			p.flush();
			p.close();
			fileWriter.close();

			SnapshotFileInfo l_enrollmentFile = new SnapshotFileInfo();
			l_enrollmentFile.setFileName(l_snapshotFilename);
			l_enrollmentFile.setEndpoint("membership");
			l_enrollmentFile.setType(4);
			l_enrollmentFile.setNumberOfRecords(p_enrollments.size());
			l_fileList.add(l_enrollmentFile);

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

		return l_fileList;
	}

	public List<SnapshotFileInfo> createGuardianFile(List<ICGuardian>p_guardians) {
		mLog.trace("In createGuardianFile() ...");

		List<SnapshotFileInfo> l_fileList = new ArrayList<SnapshotFileInfo>();

		// Create File;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-hhmmss");
		LocalDateTime now = LocalDateTime.now();
		String fileName = "SIS_GUARDIAN_" + dtf.format(now) + ".txt";
		String l_snapshotFilename = null;

		FileWriter fileWriter = null;
		PrintWriter p = null;
		try {
			l_snapshotFilename = m_service.getConfigData().getWorkingDirectory() + "/" + fileName;
			fileWriter = new FileWriter(l_snapshotFilename);
			p = new PrintWriter(fileWriter);
			p.write("data_source_key|external_person_key|user_id|passwd|firstname|lastname|system_role|institution_role|row_status|company|email|street_1|street_2|title|city|state|zip_code|department|job_title|available_ind|locale");
			p.write("\r\n");
			HashMap<String, List<String>> l_observers = new HashMap<String, List<String>>();
			int i=0;
			for (ICGuardian l_guardian:p_guardians) {
				if (l_observers.get(l_guardian.getBbPersonId()) == null) {
					i++;
					List<String> l_observees = new ArrayList<String>();
					l_observees.add(l_guardian.getStudentNumber());
					//l_observers.put(l_guardian.getContactNumber(), l_observees);
					l_observers.put(l_guardian.getBbPersonId(), l_observees);
					p.write(m_service.getConfigData().getSnapshotGuardianDatasource()+"|"	//data_source
							+ "Observer"+l_guardian.getContactNumber() + "|"   //external_person_key
							+ l_guardian.getBbUsername() + "|" 	    //user_id
							+ l_guardian.getBbPassword() + "|"		//passwd
							+ l_guardian.getFirstName() + "|"		//firstname
							+ l_guardian.getLastName()+ "|"	    	//lastname
							+ l_guardian.getSystemRole()+ "|"	    //system_role
							+ l_guardian.getInstitutionRole()+ "|"  //institution_role
							+ l_guardian.getRowStatus()+ "|"		//rowStatus
							+ l_guardian.getCompany()+ "|"			//company
							+ l_guardian.getEmail()+ "|"			//email
							+ l_guardian.getStreet1()+ "|"			//street1
							+ l_guardian.getStreet2()+ "|"			//street2
							+ l_guardian.getTitle()+ "|"			//title
							+ l_guardian.getCity()+ "|"			    //city
							+ l_guardian.getState()+ "|"			//state
							+ l_guardian.getZip()+ "|"			    //zip_code
							+ l_guardian.getDepartment()+ "|"	    //department
							+ l_guardian.getJobTitle()+ "|"			//job_title
							+ l_guardian.getAvailableInd()+ "|"	    //available_ind
							+ l_guardian.getLocale());				//locale

					p.write("\r\n");
				} else {
					List<String> l_observees = l_observers.get(l_guardian.getBbPersonId());
					l_observees.add(l_guardian.getStudentNumber());
					//l_observers.put(l_guardian.getContactNumber(), l_observees);
					l_observers.put(l_guardian.getBbPersonId(), l_observees);
				}
			}
			p.flush();
			p.close();
			fileWriter.close();

			SnapshotFileInfo l_guardianFile = new SnapshotFileInfo();
			l_guardianFile.setFileName(l_snapshotFilename);
			l_guardianFile.setEndpoint("person");
			l_guardianFile.setType(3);
			l_guardianFile.setNumberOfRecords(i);
			l_fileList.add(l_guardianFile);


			// Now Create the Guardian Association file or Observers

			// Create File;
			now = LocalDateTime.now();
			fileName = "SIS_GUARDIAN_ASSOCIATION_" + dtf.format(now) + ".txt";
			l_snapshotFilename = m_service.getConfigData().getWorkingDirectory() + "/" + fileName;
			fileWriter = new FileWriter(l_snapshotFilename);
			p = new PrintWriter(fileWriter);
			p.write("DATA_SOURCE_KEY|EXTERNAL_OBSERVER_KEY|EXTERNAL_USER_KEY");
			p.write("\r\n");
			int j=0;
			for (HashMap.Entry<String, List<String>> l_observer : l_observers.entrySet()) {
				List<String> l_observees = l_observer.getValue();
				for (String l_observee:l_observees) {
					j++;
					p.write(m_service.getConfigData().getSnapshotGuardianAssociationDatasource() + "|"					           //data_source
							+ "Observer"+l_observer.getKey() + "|"             //external_observer_key
							+ l_observee);                                     //external_user_key
					p.write("\r\n");
				}
			}
			p.flush();
			p.close();
			fileWriter.close();

			SnapshotFileInfo l_guardianAssociationFile = new SnapshotFileInfo();
			l_guardianAssociationFile.setFileName(l_snapshotFilename);
			l_guardianAssociationFile.setEndpoint("associateobserver");
			l_guardianAssociationFile.setType(3);
			l_guardianAssociationFile.setNumberOfRecords(j);
			l_fileList.add(l_guardianAssociationFile);

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

		return l_fileList;
	}

	public DataSetStatus sendFile(SnapshotFileInfo p_file) {
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

			Credentials credentials = null;
			if (p_file.getType() == 1) {
				mLog.debug("OPTION 1");
				credentials = new UsernamePasswordCredentials(l_configData.getSnapshotStudentSharedUsername(),
						l_configData.getSnapshotStudentSharedPassword());
			} else if (p_file.getType() == 2) {
				credentials = new UsernamePasswordCredentials(l_configData.getSnapshotStaffSharedUsername(),
						l_configData.getSnapshotStaffSharedPassword());
			} else if (p_file.getType() == 3) {
				mLog.debug("OPTION 3");
				credentials = new UsernamePasswordCredentials(l_configData.getSnapshotGuardianSharedUsername(),
						l_configData.getSnapshotGuardianSharedPassword());
			} else if (p_file.getType() == 4) {
				mLog.debug("OPTION 4");
				credentials = new UsernamePasswordCredentials(l_configData.getSnapshotStudentSharedUsername(),
						l_configData.getSnapshotStudentSharedPassword());
			}
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
				details.setEndpoint(p_file.getEndpoint());
				details.setOperation("refresh");
				details.setJobTitle("Infinite Campus Integration");
				mLog.debug("Endpoint: " + details.getEndpoint());
				mLog.debug("Operation: " + details.getOperation());
				mLog.debug("Job Title: " + details.getJobTitle());


				try {
					HttpPost httppost = new HttpPost(l_configData.getRestHost()
							+ "/webapps/bb-data-integration-flatfile-" + l_configData.getSnapshotBbInstanceId()
							+ "/endpoint/" + details.getEndpoint() + "/" + details.getOperation());
					FileEntity tmp = new FileEntity(new File(p_file.getFileName()), ContentType.create("text/plain", "UTF-8"));
					httppost.setEntity(tmp);
					mLog.debug("executing request " + details.getOperation());
					mLog.debug("executing request " + httppost.getRequestLine());
					CloseableHttpResponse response = httpclient.execute(httppost);
					try {
						if (response.getStatusLine().getStatusCode() == 200) {
							mLog.debug("----------------------------------------");
							mLog.debug("Status Line: " + response.getStatusLine());
							HttpEntity resEntity = response.getEntity();
							if (resEntity != null) {
								InputStream data = resEntity.getContent();
								byte[] text = data.readAllBytes();
								String s = new String(text, StandardCharsets.UTF_8);
								if (s.contains("code") && s.contains("to")) {
									String code = s.substring(s.indexOf("code") + 4, s.indexOf("to"));
									code = code.trim();
									mLog.debug("Code: " + code);

									String resultURL = l_configData.getRestHost()
											+ "/webapps/bb-data-integration-flatfile-"
											+ l_configData.getSnapshotBbInstanceId() + "/endpoint/dataSetStatus/"
											+ code;

									HttpPost httppost1 = new HttpPost(resultURL);
									mLog.debug("executing request " + resultURL);
									CloseableHttpResponse response1 = httpclient.execute(httppost1);
									mLog.debug("Code: " + code);
									mLog.debug("Status: " + response1.getStatusLine().getStatusCode());
									HttpEntity resEntity1 = response1.getEntity();
									if (response1.getStatusLine().getStatusCode() == 200) {

										int l_retry=0;
										do {
											response1 = httpclient.execute(httppost1);
											resEntity1 = response1.getEntity();
											mLog.debug("Status Line: " + response1.getStatusLine());

											InputStream data1 = resEntity1.getContent();
											text = data1.readAllBytes();
											s = new String(text, StandardCharsets.UTF_8);
											XmlMapper xmlMapper = new XmlMapper();
											status = xmlMapper.readValue(s, DataSetStatus.class);
											mLog.debug("Code: " + s);
											mLog.debug("Completed Count: " + status.getCompletedCount());
											mLog.debug("Error Count: " + status.getErrorCount());
											mLog.debug("Queued Count: " + status.getQueuedCount());
											mLog.debug("Warning Count: " + status.getWarningCount());
											if (status.getQueuedCount() == 0) {
												l_retry++;
											}
											Thread.sleep(3000);
										} while (status.getQueuedCount() > 0 || (status.getCompletedCount() < p_file.getNumberOfRecords() && l_retry < 5));

										l_message = "Snapshot Integration Completed" + "<br>"
												+ "    File Processsed: " + p_file.getFileName() + "<br>"
												+ "      BB Endpoint: " + p_file.getEndpoint() + "<br>"
												+ "      BB Operation: " + details.getOperation() + "<br>"
												+ "      <br>"
												+ "    Data Load Status Operation: " + "<br>"
												+ "      Completed Count: " + status.getCompletedCount() + "<br>"
												+ "      Error Count: " + status.getErrorCount() + "<br>"
												+ "      Queued Count: " + status.getQueuedCount() + "<br>"
												+ "      Warning Count: " + status.getWarningCount() + "<br>"
												+ "      BB Learn URL: " + resultURL + "<br>";
									} else {
										l_message = "Snapshot Integration Error Report" + "<br>"
												+ "    File Processsed: " + p_file.getFileName() + "<br>"
												+ "      BB Endpoint: " + p_file.getEndpoint() + "<br>"
												+ "      BB Operation: " + details.getOperation() + "<br>"
												+ "      HTTP Status: " + response1.getStatusLine().getStatusCode() + response1.getStatusLine().getReasonPhrase() + "<br>";

									}
								} else {
									mLog.info("Unable to process Status Response");
									l_message = "Snapshot Integration Error Report" + "<br>"
											+ "    File Processsed: " + p_file.getFileName() + "<br>"
											+ "      BB Endpoint: " + p_file.getEndpoint() + "<br>"
											+ "      BB Operation: " + details.getOperation() + "<br>"
											+ "      Unable to process Status Response ... Report this error" + "<br>";

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
			sendEmail(l_configData.getEmailFrom(), l_configData.getSnapshotEmail(), "Infinite Campus Integration", l_message);
		} catch (Exception l_ex) {
			mLog.error("ERROR", l_ex);
		}
		return status;
	}

	public DataSetStatus sendFile(String p_file, String p_endpoint) {
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

			Credentials credentials = new UsernamePasswordCredentials(l_configData.getSnapshotStudentSharedUsername(),
					l_configData.getSnapshotStudentSharedPassword());
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
				details.setEndpoint(p_endpoint);
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
									mLog.info("executing request " + resultURL);
									CloseableHttpResponse response1 = httpclient.execute(httppost1);
									mLog.info("Code: " + code);
									mLog.info("Status: " + response1.getStatusLine().getStatusCode());
									HttpEntity resEntity1 = response1.getEntity();
									if (response1.getStatusLine().getStatusCode() == 200) {

										int l_retry=0;
										do {
											response1 = httpclient.execute(httppost1);
											resEntity1 = response1.getEntity();
											mLog.info("Status Line: " + response1.getStatusLine());

											InputStream data1 = resEntity1.getContent();
											text = data1.readAllBytes();
											s = new String(text, StandardCharsets.UTF_8);
											XmlMapper xmlMapper = new XmlMapper();
											status = xmlMapper.readValue(s, DataSetStatus.class);
											mLog.debug("Code: " + s);
											mLog.info("Completed Count: " + status.getCompletedCount());
											mLog.info("Error Count: " + status.getErrorCount());
											mLog.info("Queued Count: " + status.getQueuedCount());
											mLog.info("Warning Count: " + status.getWarningCount());
											if (status.getQueuedCount()==0) {
												l_retry++;
											}
											Thread.sleep(3000);
										} while (status.getQueuedCount() > 0 || l_retry < 2);

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
			sendEmail(l_configData.getEmailFrom(), l_configData.getSnapshotEmail(), "Infinite Campus Integration", l_message);
		} catch (Exception l_ex) {
			mLog.error("ERROR", l_ex);
		}
		return status;
	}

	private void sendEmail(String p_from, String p_email, String p_subject, String p_message) {
		mLog.trace("In sendEmail() ...");
		// Now Send Email
		try {
			mLog.info("Sending Email: ");
			EmailManager l_email = new EmailManager();
			ContactModel l_contact = new ContactModel();
			l_contact.setEmail(p_email);
			l_contact.setSubject(p_subject);
			l_contact.setMessage(p_message);
			//l_email.sendEmail("joefavero@gmail.com", p_email, l_contact);
			l_email.sendEmail(p_from, p_email, l_contact);
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
