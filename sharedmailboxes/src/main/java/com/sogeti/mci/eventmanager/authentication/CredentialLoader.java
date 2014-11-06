package com.sogeti.mci.eventmanager.authentication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.sogeti.mci.eventmanager.dao.DomainCredentialsDAO;
import com.sogeti.mci.eventmanager.helper.ConstantList;
import com.sogeti.mci.eventmanager.model.DomainCredentials;

public class CredentialLoader {
	
		private static DomainCredentials domainCredentials = DomainCredentialsDAO.loadDomainCredentials();
		private static GoogleCredentialItem googleCredentialItem = generateGoogleCredentialItem(getScopes());

		public static Gmail getGmailService(){
			Gmail service = null;			
			if(googleCredentialItem != null){
				service = new Gmail.Builder(googleCredentialItem.getHttpTransport(), googleCredentialItem.getJsonFactory(), null)
			      .setHttpRequestInitializer(googleCredentialItem.getGoogleCredential()).setApplicationName("MCI").build();
			}				  
		    return service;
		}
		
		public static Drive getDriveService(){
			Drive service = null;			
			if(googleCredentialItem != null){
				service = new Drive.Builder(googleCredentialItem.getHttpTransport(), googleCredentialItem.getJsonFactory(), null)
			      .setHttpRequestInitializer(googleCredentialItem.getGoogleCredential()).setApplicationName("MCI").build();
			}				  
		    return service;
		}

		private static GoogleCredentialItem generateGoogleCredentialItem(ArrayList<String> scopes) {
			  HttpTransport httpTransport = new NetHttpTransport();
			  JacksonFactory jsonFactory = new JacksonFactory();
			  
			  GoogleCredential googleCredential = null;
			  GoogleCredentialItem googleCredentialItem = null;
				  
			  try {
				googleCredential = new GoogleCredential.Builder()
				      .setTransport(httpTransport)
				      .setJsonFactory(jsonFactory)
				      .setServiceAccountId(domainCredentials.getServiceAccountEmail())
				      .setServiceAccountScopes(scopes)
				      .setServiceAccountUser(domainCredentials.getUserEmailAddress())		      
				      .setServiceAccountPrivateKeyFromP12File(new File(CredentialLoader.class.getResource("/"+domainCredentials.getCertificatePath()).toURI()))
				      .build();
				
				googleCredentialItem = new GoogleCredentialItem();
				googleCredentialItem.setGoogleCredential(googleCredential);
				
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return googleCredentialItem;
		}
		
		public static void a() {
			URL url;
			try {
			     url = new URL("/" + domainCredentials.getCertificatePath());
			    InputStream inputStream = url.openConnection().getInputStream();
			    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			    String inputLine;
			 
			    while ((inputLine = in.readLine()) != null) {
			        System.out.println(inputLine);
			    }
			 
			    in.close();
			 
			} catch (IOException e) {
			    e.printStackTrace();
			}
			
		}
		
		
		  
		private static ArrayList<String> getScopes(){
			ArrayList<String> scopes = new ArrayList<String>();
			scopes.add(GmailScopes.GMAIL_MODIFY);
			scopes.add(DriveScopes.DRIVE);		
			return scopes;
		}
		
}